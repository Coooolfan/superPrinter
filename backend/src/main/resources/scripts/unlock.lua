-- KEYS[1]：锁的key
-- ARGV[1]：请求标识
if redis.call('get', KEYS[1]) == ARGV[1] then
    return redis.call('del', KEYS[1])
else
    return 0
end 