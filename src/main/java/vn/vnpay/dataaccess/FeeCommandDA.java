package vn.vnpay.dataaccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.dto.CreateFeeCommandReq;
import vn.vnpay.dto.CreateFeeTransactionReq;

import java.sql.*;

public class FeeCommandDA {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeeCommandDA.class);
    private PreparedStatement cstmt;

    public void addFeeCommand(CreateFeeCommandReq createFeeCommandReq) throws SQLException {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "vnpay");
            String sql = "INSERT INTO feecommand (id, commandcode, totalrecord, totalfee, createuser, createdate) " +
                    "VALUES (? , ? , ? , ? , ? , ?)";
            cstmt = conn.prepareCall(sql);
            cstmt.setInt(1, 3);
            cstmt.setString(2, createFeeCommandReq.getCommandCode());
            cstmt.setInt(3, Integer.parseInt(createFeeCommandReq.getTotalRecord()));
            cstmt.setInt(4, Integer.parseInt(createFeeCommandReq.getTotalFee()));
            cstmt.setString(5, createFeeCommandReq.getCreatedUser());
            cstmt.setDate(6, Date.valueOf(createFeeCommandReq.getCreatedDate()));

            int row = cstmt.executeUpdate();
            if (row > 0)
                LOGGER.info("Add fee command code: {} success", createFeeCommandReq.getCommandCode());
        } catch (Exception e) {
            LOGGER.error("Add fee command code: {} fail, {}", createFeeCommandReq.getCommandCode(), e.getMessage());
        } finally {
            if (cstmt != null)
                cstmt.close();
            if (conn != null)
                conn.close();
        }
    }

   public void addFeeTransaction(CreateFeeTransactionReq feeTransactionReq) {

   }
}
