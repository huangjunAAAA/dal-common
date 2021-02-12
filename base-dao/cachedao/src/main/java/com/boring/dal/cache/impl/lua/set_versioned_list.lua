local ver = redis.call("get", "version:"..KEYS[1])
if  ver ==  ARGV[1] then
    redis.call("hmset", KEYS[1], KEYS[2], ARGV[2])
    for i = 3, #KEYS do
        redis.call("hdel", KEYS[1], KEYS[i])
    end
    return redis.call("incr", "version:"..KEYS[1])
end
return 0