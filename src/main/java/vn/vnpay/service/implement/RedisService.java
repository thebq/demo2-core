package vn.vnpay.service.implement;

import redis.clients.jedis.Jedis;

import java.util.List;

public class RedisService {
    private final Jedis jedis = new Jedis("localhost");

    public void setValueToRedis(String key, String value) {
        jedis.lpush(key, value);
    }

    public List<String> getRequestIdByDate(String date) {
        return jedis.lrange(date, 0, 99999);
    }
}
