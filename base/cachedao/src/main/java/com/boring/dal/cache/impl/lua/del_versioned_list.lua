redis.call("del", KEYS[1])
return redis.call("incr", "version:"..KEYS[1])