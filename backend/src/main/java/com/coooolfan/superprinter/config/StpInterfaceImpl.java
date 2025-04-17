package com.coooolfan.superprinter.config;

import java.util.List;

import com.coooolfan.superprinter.entity.User;
import com.coooolfan.superprinter.mapper.UserMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import cn.dev33.satoken.stp.StpInterface;

import static com.coooolfan.superprinter.enums.UserRole.getByCode;

/**
 * 自定义权限加载接口实现类
 */
@AllArgsConstructor
@Component    // 保证此类被 SpringBoot 扫描，完成 Sa-Token 的自定义权限验证扩展
public class StpInterfaceImpl implements StpInterface {

    private UserMapper userMapper;

    /**
     * 返回一个账号所拥有的权限码集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return List.of();
    }

    /**
     * 返回一个账号所拥有的角色标识集合 (权限与角色可分开校验)
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        if (loginId == null)
            return List.of();

        long userId;
        if (loginId instanceof Long) {
            userId = (Long) loginId;
        } else if (loginId instanceof String) {
            userId = Long.parseLong((String) loginId);
        } else {
            return List.of();
        }

        User user = userMapper.selectById(userId);
        return List.of(getByCode(user.getRole()).name());
    }

}
