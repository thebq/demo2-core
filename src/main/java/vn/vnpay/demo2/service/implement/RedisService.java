package vn.vnpay.demo2.service.implement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.time.LocalDateTime;
import java.time.ZoneId;

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
        try {
            jedis.lpush(key, value);
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime endTime = now.withHour(23).withMinute(59).withSecond(59);
            long expireTimestamp = endTime.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
            jedis.expireAt(key, expireTimestamp);
        } catch (Exception e) {

        } finally {
            releaseConnection(jedis);
        }
    }

    public Boolean checkExist(String requestId) {
        Jedis jedis = getConnection();
        boolean check = jedis.exists(requestId);
        releaseConnection(jedis);
        return check;
    }
}
