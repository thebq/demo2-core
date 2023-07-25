package vn.vnpay.demo2.util;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class LocalProperties {

    public static Object get(String key) throws IOException {
        FileReader reader = new FileReader("src/main/resources/config.properties");
        Properties properties = new Properties();
        properties.load(reader);
        return properties.getProperty(key);
    }
}
