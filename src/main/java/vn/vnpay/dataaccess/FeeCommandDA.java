package vn.vnpay.dataaccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.dto.CreateFeeCommandReq;
import vn.vnpay.dto.CreateFeeTransactionReq;
import vn.vnpay.model.FeeTransaction;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FeeCommandDA {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeeCommandDA.class);
    private PreparedStatement cstmt;

    public Boolean addFeeCommand(CreateFeeCommandReq createFeeCommandReq) throws SQLException {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "vnpay");
            String sql = "INSERT INTO feecommand (commandcode, totalrecord, totalfee, createuser, createdate) " +
                    "VALUES (? , ? , ? , ? , ?)";
            cstmt = conn.prepareCall(sql);
            cstmt.setString(1, createFeeCommandReq.getCommandCode());
            cstmt.setInt(2, Integer.parseInt(createFeeCommandReq.getTotalRecord()));
            cstmt.setInt(3, Integer.parseInt(createFeeCommandReq.getTotalFee()));
            cstmt.setString(4, createFeeCommandReq.getCreatedUser());
            cstmt.setDate(5, Date.valueOf(createFeeCommandReq.getCreatedDate()));

            int row = cstmt.executeUpdate();
            if (row > 0) {
                LOGGER.info("Add fee command code: {} SUCCESS", createFeeCommandReq.getCommandCode());
                CreateFeeTransactionReq createFeeTransactionReq = new CreateFeeTransactionReq();
                createFeeTransactionReq.setCommandCode(createFeeCommandReq.getCommandCode());
                createFeeTransactionReq.setFeeAmount(createFeeCommandReq.getTotalFee());
                createFeeTransactionReq.setCreateDate(createFeeCommandReq.getCreatedDate());
                createFeeTransactionReq.setModifiedDate(createFeeCommandReq.getCreatedDate());
                createFeeTransactionReq.setStatus("01");
                createFeeTransactionReq.setTotalScan("1");
                createFeeTransactionReq.setTransactionCode(String.valueOf(UUID.randomUUID()));
                createFeeTransactionReq.setAccountNumber("1092991010");
                if (addFeeTransaction(createFeeTransactionReq))
                    return true;
            }
        } catch (Exception e) {
            LOGGER.error("Add fee command code: {} FAIL, {}", createFeeCommandReq.getCommandCode(), e.getMessage());
            return false;
        } finally {
            if (cstmt != null)
                cstmt.close();
            if (conn != null)
                conn.close();
        }
        return false;
    }

    public Boolean addFeeTransaction(CreateFeeTransactionReq feeTransactionReq) throws SQLException {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "vnpay");
            String sql = "INSERT INTO feetransaction (transactioncode, commandcode, feeamount, status, accountnumber, " +
                    "totalscan, remark, createdate, modifieddate) VALUES (? , ? , ? , ? , ? , ? , ? , ? , ?)";
            cstmt = conn.prepareCall(sql);
            cstmt.setString(1, feeTransactionReq.getTransactionCode());
            cstmt.setString(2, feeTransactionReq.getCommandCode());
            cstmt.setInt(3, Integer.parseInt(feeTransactionReq.getFeeAmount()));
            cstmt.setString(4, feeTransactionReq.getStatus());
            cstmt.setString(5, feeTransactionReq.getAccountNumber());
            cstmt.setInt(6, Integer.parseInt(feeTransactionReq.getTotalScan()));
            cstmt.setString(7, feeTransactionReq.getRemark());
            cstmt.setDate(8, Date.valueOf(feeTransactionReq.getCreateDate()));
            cstmt.setDate(9, Date.valueOf(feeTransactionReq.getModifiedDate()));

            int row = cstmt.executeUpdate();
            if (row > 0) {
                LOGGER.info("Add fee transaction SUCCESS, transaction code: {}, command code: {}",
                        feeTransactionReq.getTransactionCode(), feeTransactionReq.getCommandCode());
                return true;
            }
        } catch (SQLException e) {
            LOGGER.error("Add fee transaction FAIL, transaction code: {}, command code: {}, {}",
                    feeTransactionReq.getTransactionCode(), feeTransactionReq.getCommandCode(), e.getMessage());
            return false;
        } finally {
            if (cstmt != null)
                cstmt.close();
            if (conn != null)
                conn.close();
        }
        return false;
    }

    public List<FeeTransaction> getFeeTransactionByCmdCode(String commandCode) throws SQLException {
        Connection conn = null;
        List<FeeTransaction> feeTransactionList = new ArrayList<>();
        LOGGER.info("START Get fee transaction by command code: {}", commandCode);
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "vnpay");
            String sql = "SELECT * FROM feetransaction WHERE feetransaction.commandcode = '" + commandCode + "'";
            cstmt = conn.prepareCall(sql);
            ResultSet rs = cstmt.executeQuery();
            while (rs.next()) {
                FeeTransaction feeTransaction = new FeeTransaction();
                feeTransaction.setTransactionCode(rs.getString("transactioncode"));
                feeTransaction.setCommandCode(rs.getString("commandcode"));
                feeTransaction.setFeeAmount(rs.getInt("feeamount"));
                feeTransaction.setStatus(rs.getString("status"));
                feeTransaction.setAccountNumber(rs.getString("accountnumber"));
                feeTransaction.setTotalScan(rs.getInt("totalscan"));
                feeTransaction.setRemark(rs.getString("remark"));
                feeTransaction.setCreateDate(rs.getDate("createdate"));
                feeTransaction.setModifiedDate(rs.getDate("modifieddate"));
                feeTransactionList.add(feeTransaction);
            }
        } catch (SQLException e) {
            LOGGER.error("Get fee transaction by command code: {} FAIL", commandCode);
            return null;
        } finally {
            if (cstmt != null)
                cstmt.close();
            if (conn != null)
                conn.close();
        }
        LOGGER.info("FINISH Get fee transaction by command code: {}", commandCode);
        return feeTransactionList;
    }

    public Boolean updateFeeTransaction(FeeTransaction feeTransaction) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "vnpay");
            String sql = "";
            cstmt = conn.prepareCall(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
