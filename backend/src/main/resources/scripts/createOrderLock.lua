-- 一共三个参数 幂等控制token 分布式锁key（由业务id拼接）、value（雪花ID）
local idempotentToken = KEYS[1]
local distributedLockKey = KEYS[2]
local snowflakeId = ARGV[1]

-- 1. 先检查幂等，然后检查业务
-- 1.1 检查幂等token是否存在，不存在直接返回1
if redis.call('EXISTS', idempotentToken) == 0 then
    return '-1'
end

-- 1.2 检查幂等token的hash中的pageCount字段的值是否大于等于0，如果不符合直接返回2
-- trick: 此处没有检查pageCount时候为正数，但是如果pageCount为负数其默认值为-2，与错误码相同
if not redis.call('HGET', idempotentToken, 'pageCount') then
    return '-2'
end

-- 2. 检查分布式锁是否存在，如果存在直接返回3
if redis.call('EXISTS', distributedLockKey) == 1 then
    return '-3'
end

-- 3. 设置分布式锁的值为雪花ID，过期时间为5秒
redis.call('SET', distributedLockKey, snowflakeId, 'EX', 5)

-- 4. 执行幂等性消费：删除token，确保其不能被重复使用
local pageCount = redis.call('HGET', idempotentToken, 'pageCount')
redis.call('DEL', idempotentToken)

-- 5. 成功返回幂等token的hash中的pageCount字段的值
return pageCount
