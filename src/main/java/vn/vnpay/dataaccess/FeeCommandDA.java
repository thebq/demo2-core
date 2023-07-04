package vn.vnpay.dataaccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.dto.CreateFeeRequest;

import java.sql.*;

public class FeeCommandDA {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeeCommandDA.class);

    public void addFeeCommand(CreateFeeRequest createFeeRequest) throws SQLException {
        CallableStatement cstmt = null;
        Connection connection = null;
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "vnpay");
            String sql = "INSERT INTO feecommand (id, commandcode, totalrecord, totalfee, createuser, createdate) VALUES (? , ? , ? , ? , ? , ?)";
            cstmt = connection.prepareCall(sql);
            cstmt.setInt(1, 3);
            cstmt.setString(2, createFeeRequest.getCommandCode());
            cstmt.setInt(3, Integer.parseInt(createFeeRequest.getTotalRecord()));
            cstmt.setInt(4, Integer.parseInt(createFeeRequest.getTotalFee()));
            cstmt.setString(5, createFeeRequest.getCreatedUser());
            cstmt.setDate(6, Date.valueOf("2023-06-29"));

            int row = cstmt.executeUpdate();
            if (row > 0)
                LOGGER.info("Add fee command code: {} success", createFeeRequest.getCommandCode());
        } catch (Exception e) {
            LOGGER.error("Add fee command code: {} fail, {}", createFeeRequest.getCommandCode(), e.getMessage());
        } finally {
            if (cstmt != null)
                cstmt.close();
            if (connection != null)
                connection.close();
        }
    }
}
