package vn.vnpay.demo2.dao;

import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * @author thebq
 * Created: 03/08/2023
 */
@Slf4j
public class GenericDA {
    public void closePreparedStatement(PreparedStatement cstmt) {
        try {
            if (cstmt != null)
                cstmt.close();
        } catch (Exception e) {
            log.error("Close PreparedStatement FAIL");
        }
    }

    public void closeResultSet(ResultSet resultSet) {
        try {
            if (resultSet != null)
                resultSet.close();
        } catch (Exception e) {
            log.error("Close resultSet FAIL");
        }
    }

    public void closeConnection(Connection connection) {
        try {
            if (connection != null)
                connection.close();
        } catch (Exception e) {
            log.error("Close connection FAIL");
        }
    }
}
