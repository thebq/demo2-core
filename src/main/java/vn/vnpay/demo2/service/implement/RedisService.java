package vn.vnpay.demo2.service.implement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class RedisService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);
    private static final JedisPool jedisPool = new JedisPool();
    public static Jedis getConnection() {
        return jedisPool.getResource();
    }

    public static void releaseConnection(Jedis jedis) {
        jedis.close();
    }

    public void setValueToRedis(String key, String value) {
        Jedis jedis = getConnection();
        jedis.lpush(key, value);
        releaseConnection(jedis);
    }

    public Boolean checkExist(String date, String requestId) {
        Jedis jedis = getConnection();
        boolean check = jedis.exists(String.format("%s%s", date, requestId));
        releaseConnection(jedis);
        return check;
    }
}
