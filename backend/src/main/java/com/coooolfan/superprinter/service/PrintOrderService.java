package com.coooolfan.superprinter.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coooolfan.superprinter.config.RabbitMQConfig;
import com.coooolfan.superprinter.entity.PrintOrder;
import com.coooolfan.superprinter.exception.BusinessException;
import com.coooolfan.superprinter.mapper.PrintOrderMapper;
import com.coooolfan.superprinter.util.DateUtil;
import com.coooolfan.superprinter.util.IdGenerator;
import com.coooolfan.superprinter.util.RedisLockUtil;
import com.coooolfan.superprinter.vo.OrderPreTokenVO;
import com.coooolfan.superprinter.vo.PrintOrderCreateVO;
import com.coooolfan.superprinter.vo.message.FilePageCountMessage;
import com.coooolfan.superprinter.vo.response.OrderPreBalanceResponse;
import com.coooolfan.superprinter.vo.response.OrderPreTokenResponse;
import com.coooolfan.superprinter.vo.response.PrintOrderCreateResponse;

import cn.dev33.satoken.stp.StpUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static com.coooolfan.superprinter.config.RedisConfig.PREFIX_IDEMPOTENT_COUNT;
import static com.coooolfan.superprinter.config.RedisConfig.PREFIX_LOCK_PRINT_ORDER;

/**
 * 打印订单服务实现类
 */
@Service
@AllArgsConstructor
@Slf4j
public class PrintOrderService extends ServiceImpl<PrintOrderMapper, PrintOrder> {
    private final RedisTemplate<String, Object> redisTemplate;
    private final RabbitTemplate rabbitTemplate;
    private final IdGenerator idGenerator;
    private final RedisLockUtil redisLockUtil;
    private final PrinterResourceService printerResourceService;

    public OrderPreTokenResponse getOrderPreToken(OrderPreTokenVO vo) {
        // 生成一串UUID存到Redis中，用于接口幂等性
        String token = UUID.randomUUID().toString();
        String redisKey = PREFIX_IDEMPOTENT_COUNT + token;

        // 使用Hash操作存储多个字段
        redisTemplate.opsForHash().put(redisKey, "fileIds", vo.getFileIds());
        // 初始页数设为-1，后续由异步任务更新
        // idempotent:{token}:pageCount
        redisTemplate.opsForHash().put(redisKey, "pageCount", "-1");
        redisTemplate.expire(redisKey, 5, TimeUnit.MINUTES);

        // 投递页数统计任务
        FilePageCountMessage message = new FilePageCountMessage();
        message.setTimestamp(Instant.now().toEpochMilli());
        message.setToken(token);
        message.setFileIds(vo.getFileIds());
        message.setRetryCount(0); // 初始化重试次数为0

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.PRINT_EXCHANGE,
                RabbitMQConfig.FILE_PAGE_COUNT_ROUTING_KEY,
                message
        );

        return new OrderPreTokenResponse(token);
    }

    public OrderPreBalanceResponse getOrderPreBalance(String uuid) {
        // 从Redis Hash中获取对应的文件总页数
        String pageCountStr = (String) redisTemplate.opsForHash().get(PREFIX_IDEMPOTENT_COUNT + uuid, "pageCount");
        int pageCount = -1;

        try {
            if (pageCountStr != null) {
                pageCount = Integer.parseInt(pageCountStr);
            }
        } catch (NumberFormatException e) {
            // 转换失败时保持默认值-1
        }

        return new OrderPreBalanceResponse(pageCount);
    }

    @Transactional(rollbackFor = Exception.class)
    public PrintOrderCreateResponse createOrder(PrintOrderCreateVO vo) {
        Long userId = StpUtil.getLoginIdAsLong();
        // 0. 计算幂等key、分布式锁的key和value
        String idempotentKey = PREFIX_IDEMPOTENT_COUNT + vo.getToken();
        String lockKey = PREFIX_LOCK_PRINT_ORDER + vo.getPrinterId() + ":" + userId;
        String lockValue = idGenerator.generateId();

        log.info("开始创建订单, userId={}, printerId={}, token={}", userId, vo.getPrinterId(), vo.getToken());

        // 1. 操作Redis，使用Lua脚本完成（幂等token检查、分布式锁）(需要传入vo中的token，锁的key和value)
        int pageCount = redisLockUtil.createOrderLock(idempotentKey, lockKey, lockValue);
        // 1.1 如果页数计算还未完成，让用户再等等，别急
        // 1.2 如果幂等检查失败或者分布式锁获取失败提示请求过于繁忙        
        if (pageCount <= 0) {
            String errorMsg = redisLockUtil.getCreateOrderLockErrorMessage(pageCount);
            log.warn("订单创建前置检查失败, userId={}, printerId={}, error={}", userId, vo.getPrinterId(), errorMsg);
            throw new BusinessException(errorMsg);
        }

        try {
            // 2. 操作数据库，扣减打印机剩余纸张（使用类似于乐观锁的 paperCount > ? 条件）
            // 2.1 如果是printId为0的特惠打印，还需要在订单表查询当日该用户有没有创建过特惠打印订单
            if (vo.getPrinterId() == 0) {
                log.info("检查用户特惠打印订单限制, userId={}", userId);
                query().eq("printer_id", 0)
                        .eq("user_id", userId)
                        .eq("create_day", DateUtil.getCurrentYYYYMMDD())
                        .oneOpt()
                        .ifPresent(order -> {
                            log.warn("用户今日已创建过特惠打印订单, userId={}, existingOrderId={}", userId, order.getOrderId());
                            throw new BusinessException("您今天已经创建过特惠打印订单了哦~");
                        });
            }

            // 2.2 扣减对应打印机的纸张数量
            log.info("开始扣减打印机纸张, printerId={}, pageCount={}", vo.getPrinterId(), pageCount);
            boolean update = printerResourceService.update()
                    .set("paper_count = paper_count - ?", pageCount)
                    .eq("printer_id", vo.getPrinterId())
                    .ge("paper_count", pageCount)
                    .update();

            if (!update) {
                log.warn("打印机纸张不足, printerId={}, requiredPages={}", vo.getPrinterId(), pageCount);
                throw new BusinessException("打印机剩余纸张不足");
            }

            // 3. 插入订单记录
            PrintOrder printOrder = PrintOrder.fromCreateVO(vo, userId, pageCount);
            log.info("创建订单记录, userId={}, printerId={}, orderId={}", userId, vo.getPrinterId(), printOrder.getOrderId());
            save(printOrder);

            // 4. 返回订单创建结果
            PrintOrderCreateResponse response = new PrintOrderCreateResponse();
            BeanUtils.copyProperties(printOrder, response);
            log.info("订单创建成功, orderId={}", printOrder.getOrderId());
            return response;
        } catch (BusinessException be) {
            // 业务异常直接抛出，由统一异常处理捕获
            throw be;
        } catch (Exception e) {
            // 转换为业务异常抛出
            log.error("订单创建过程中发生系统异常, userId={}, printerId={}, token={}", userId, vo.getPrinterId(), vo.getToken(), e);
            throw new BusinessException("订单创建失败，系统异常，请稍后重试");
        } finally {
            // 释放锁
            redisLockUtil.releaseLock(lockKey, lockValue);
        }
    }
}