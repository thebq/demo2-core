package vn.vnpay;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import vn.vnpay.model.FeeTask;
import vn.vnpay.server.NettyServer;
import vn.vnpay.util.LocalProperties;

import java.util.Timer;
import java.util.TimerTask;

public class Demo2Core {
    public static final ComboPooledDataSource connectionPool = new ComboPooledDataSource();
    public static JedisPool jedisPool = new JedisPool();
    private static final Logger LOGGER = LoggerFactory.getLogger(Demo2Core.class);

    public static void main(String[] args) {
        dbConfig();
        redisConfig();
        NettyServer nettyServer = new NettyServer();
        nettyServer.start();
        startCronJob();
    }

    private static void dbConfig() {
        try {
            String jdbcUrl = String.valueOf(LocalProperties.get("url"));
            String userName = String.valueOf(LocalProperties.get("username"));
            String password = String.valueOf(LocalProperties.get("password"));
            int minPoolSize = Integer.parseInt(String.valueOf(LocalProperties.get("min-pool-size")));
            int maxPoolSize = Integer.parseInt(String.valueOf(LocalProperties.get("max-pool-size")));

            connectionPool.setJdbcUrl(jdbcUrl);
            connectionPool.setUser(userName);
            connectionPool.setPassword(password);
            connectionPool.setMinPoolSize(minPoolSize);
            connectionPool.setInitialPoolSize(minPoolSize);
            connectionPool.setMaxPoolSize(maxPoolSize);
        } catch (Exception e) {
            LOGGER.error("Create connect pool FAIL");
        }
    }

    private static void redisConfig() {

        try {
            String redisHost = String.valueOf(LocalProperties.get("redis-host"));
            int port = Integer.parseInt(String.valueOf(LocalProperties.get("redis-port")));
            JedisPoolConfig poolConfig = new JedisPoolConfig();
            jedisPool = new JedisPool(poolConfig, redisHost, port);
        } catch (Exception e) {
            LOGGER.error("Create redis connect pool FAIL");
        }
    }


    private static void startCronJob() {
        Timer timer = new Timer();
        TimerTask task = new FeeTask();
        timer.schedule(task, 3000, 18000);
    }
}