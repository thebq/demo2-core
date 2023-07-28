package vn.vnpay.demo2;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import vn.vnpay.demo2.constant.FeeCommandConstant;
import vn.vnpay.demo2.model.FeeTask;
import vn.vnpay.demo2.server.NettyServer;
import vn.vnpay.demo2.util.LocalProperties;

import java.util.Timer;
import java.util.TimerTask;

public class Demo2Core {
    public static final ComboPooledDataSource connectionPool = new ComboPooledDataSource();
    public static JedisPool jedisPool = new JedisPool();
    private static final Logger LOGGER = LoggerFactory.getLogger(Demo2Core.class);

    public static void main(String[] args) {
        try {
            LOGGER.info("START run application");
            dbConfig();
            redisConfig();
            startCronJob();
            NettyServer nettyServer = new NettyServer();
            nettyServer.start();
            LOGGER.info("Run application success");
            LOGGER.info("FINISH run application");
        } catch (Exception e) {
            LOGGER.error("Run application FAIL");
        }
    }

    private static void dbConfig() {
        try {
            LOGGER.info("START create connection pool");
            String jdbcUrl = String.valueOf(LocalProperties.get(FeeCommandConstant.URL));
            String userName = String.valueOf(LocalProperties.get(FeeCommandConstant.USER_NAME));
            String password = String.valueOf(LocalProperties.get(FeeCommandConstant.PASS_WORD));
            int minPoolSize = Integer.parseInt(String.valueOf(LocalProperties.get(FeeCommandConstant.MIN_POOL_SIZE)));
            int maxPoolSize = Integer.parseInt(String.valueOf(LocalProperties.get(FeeCommandConstant.MAX_POOL_SIZE)));

            connectionPool.setJdbcUrl(jdbcUrl);
            connectionPool.setUser(userName);
            connectionPool.setPassword(password);
            connectionPool.setMinPoolSize(minPoolSize);
            connectionPool.setInitialPoolSize(minPoolSize);
            connectionPool.setMaxPoolSize(maxPoolSize);
            LOGGER.info("Create connection pool success");
            LOGGER.info("FINISH create connection pool");
        } catch (Exception e) {
            LOGGER.error("Create connection pool FAIL");
        }
    }

    private static void redisConfig() {

        try {
            LOGGER.info("START create redis connection pool");
            String redisHost = String.valueOf(LocalProperties.get(FeeCommandConstant.REDIS_HOST));
            int port = Integer.parseInt(String.valueOf(LocalProperties.get(FeeCommandConstant.REDIS_PORT)));
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            jedisPool = new JedisPool(poolConfig, redisHost, port);
            LOGGER.info("Create redis connection pool success");
            LOGGER.info("FINISH create redis connection pool");
        } catch (Exception e) {
            LOGGER.error("Create redis connection pool FAIL");
        }
    }


    private static void startCronJob() {
        Timer timer = new Timer();
        TimerTask task = new FeeTask();
        timer.schedule(task, 3000, 18000);
    }
}