package vn.vnpay.service.implement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import vn.vnpay.util.LocalProperties;

import java.util.List;

public class RedisService {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);
    private static JedisPool jedisPool = new JedisPool();

    static {
        try {
            String redisHost = String.valueOf(LocalProperties.get("redis-host"));
            int port = Integer.parseInt(String.valueOf(LocalProperties.get("redis-port")));
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            jedisPool = new JedisPool(poolConfig, redisHost, port);
        } catch (Exception e) {
            LOGGER.error("Create redis connect pool FAIL");
        }
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

    public Boolean checkExist(String date, String requestId) {
        Jedis jedis = getConnection();
        boolean check = jedis.exists(String.format("%s%s", date, requestId));
        releaseConnection(jedis);
        return check;
    }
}
