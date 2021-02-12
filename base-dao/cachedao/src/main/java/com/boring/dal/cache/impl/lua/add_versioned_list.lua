local ver = redis.call("get", "version:"..KEYS[1])
if  ver ==  ARGV[1] then
    redis.call("hmset", KEYS[1], KEYS[2], ARGV[2])
    return redis.call("incr", "version:"..KEYS[1])
end
return 0