local ver_exist = redis.call("exists", "version:"..KEYS[1])
if  ver_exist == 0 then
    redis.call("hmset", KEYS[1], KEYS[2], ARGV[1])
    redis.call("set", "version:"..KEYS[1], 1)
    return 1
end
return 0