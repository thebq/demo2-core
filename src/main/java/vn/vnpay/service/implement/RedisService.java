package vn.vnpay.service.implement;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.List;

public class RedisService {
    private static final String REDIS_HOST = "localhost";
    private static final int REDIS_PORT = 1000;

    private static final JedisPool jedisPool;

    static {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        jedisPool = new JedisPool(poolConfig, REDIS_HOST, REDIS_PORT);
    }

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

    public List<String> getRequestIdByDate(String date) {
        Jedis jedis = getConnection();
        List<String> requestIdList = jedis.lrange(date, 0, 99999);
        releaseConnection(jedis);
        return requestIdList;
    }
}
