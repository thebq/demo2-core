package vn.vnpay.demo2.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GenericDA {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenericDA.class);

    public void closePreparedStatement(PreparedStatement cstmt) {
        try {
            if (cstmt != null)
                cstmt.close();
        } catch (Exception e) {
            LOGGER.error("Close PreparedStatement FAIL");
        }
    }

    public void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null)
                resultSet.close();
        } catch (Exception e) {
            LOGGER.error("Close resultSet FAIL");
        }
    }

    public void closeConnection(Connection connection) {
        try {
            if (connection != null)
                connection.close();
        } catch (Exception e) {
            LOGGER.error("Close connection FAIL");
        }
    }
}
