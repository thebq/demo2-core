package vn.vnpay.demo2.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.demo2.constant.FeeCommandConstant;
import vn.vnpay.demo2.dto.CreateFeeCommandReq;
import vn.vnpay.demo2.dto.CreateFeeTransactionReq;
import vn.vnpay.demo2.model.FeeTransaction;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static vn.vnpay.demo2.Demo2Core.connectionPool;

public class FeeCommandDA {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeeCommandDA.class);
    private PreparedStatement cstmt;
    private final GenericDA genericDA = new GenericDA();

    public Boolean addFeeCommand(CreateFeeCommandReq createFeeCommandReq) {
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
            String sql = "INSERT INTO feecommand (commandcode, totalrecord, totalfee, createuser, createdate) " +
                    "VALUES (? , ? , ? , ? , ?)";
            cstmt = conn.prepareCall(sql);
            cstmt.setString(1, createFeeCommandReq.getCommandCode());
            cstmt.setInt(2, Integer.parseInt(createFeeCommandReq.getTotalRecord()));
            cstmt.setInt(3, Integer.parseInt(createFeeCommandReq.getTotalFee()));
            cstmt.setString(4, createFeeCommandReq.getCreatedUser());
            LocalDateTime localDateTime = LocalDateTime.now();
            cstmt.setString(5, String.valueOf(localDateTime));

            int row = cstmt.executeUpdate();
            if (row > 0) {
                return true;
            }
        } catch (Exception e) {
            LOGGER.error("Add fee command code: {} FAIL, {}", createFeeCommandReq.getCommandCode(), e.getMessage());
            return false;
        } finally {
            genericDA.closePreparedStatement(cstmt);
            genericDA.closeConnection(conn);
        }
        return false;
    }

    public Boolean addFeeTransaction(CreateFeeTransactionReq feeTransactionReq) {
        Connection conn = null;
        try {
            conn = connectionPool.getConnection();
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
            LocalDateTime localDateTime = LocalDateTime.now();
            cstmt.setString(8, String.valueOf(localDateTime));
            cstmt.setString(9, String.valueOf(localDateTime));

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
            genericDA.closePreparedStatement(cstmt);
            genericDA.closeConnection(conn);
        }
        return false;
    }

    public List<FeeTransaction> getFeeTransactionByCmdCode(String commandCode) {
        Connection conn = null;
        ResultSet rs = null;
        List<FeeTransaction> feeTransactionList = new ArrayList<>();
        LOGGER.info("START Get fee transaction by command code: {}", commandCode);
        try {
            conn = connectionPool.getConnection();
            String sql = "SELECT * FROM feetransaction WHERE feetransaction.commandcode = '" + commandCode + "'";
            cstmt = conn.prepareCall(sql);
            rs = cstmt.executeQuery();
            while (rs.next()) {
                FeeTransaction feeTransaction = new FeeTransaction();
                feeTransaction.setTransactionCode(rs.getString(FeeCommandConstant.TRANSACTION_CODE));
                feeTransaction.setCommandCode(rs.getString(FeeCommandConstant.COMMAND_CODE));
                feeTransaction.setFeeAmount(rs.getInt(FeeCommandConstant.FEE_AMOUNT));
                feeTransaction.setStatus(rs.getString(FeeCommandConstant.STATUS));
                feeTransaction.setAccountNumber(rs.getString(FeeCommandConstant.ACCOUNT_NUMBER));
                feeTransaction.setTotalScan(rs.getInt(FeeCommandConstant.TOTAL_SCAN));
                feeTransaction.setRemark(rs.getString(FeeCommandConstant.REMARK));
                feeTransaction.setCreateDate(LocalDateTime.parse(rs.getString(FeeCommandConstant.CREATE_DATE)));
                feeTransaction.setModifiedDate(LocalDateTime.parse(rs.getString(FeeCommandConstant.MODIFIED_DATE)));
                feeTransactionList.add(feeTransaction);
            }
        } catch (SQLException e) {
            LOGGER.error("Get fee transaction by command code: {} FAIL", commandCode);
            return null;
        } finally {
            genericDA.closeResultSet(rs);
            genericDA.closePreparedStatement(cstmt);
            genericDA.closeConnection(conn);
        }
        LOGGER.info("FINISH Get fee transaction by command code: {}", commandCode);
        return feeTransactionList;
    }

    public void updateFeeTransaction(FeeTransaction feeTransaction) throws SQLException {
        Connection conn = null;
        LOGGER.info("START update fee transaction, transaction code: {}, command code: {}",
                feeTransaction.getTransactionCode(), feeTransaction.getCommandCode());
        try {
            conn = connectionPool.getConnection();
            String sql = "UPDATE feetransaction SET totalscan = ?, modifieddate = ?, status = ? WHERE transactioncode = ?";
            cstmt = conn.prepareCall(sql);
            cstmt.setInt(1, feeTransaction.getTotalScan());
            cstmt.setString(2, String.valueOf(LocalDateTime.now()));
            cstmt.setString(3, feeTransaction.getStatus());
            cstmt.setString(4, feeTransaction.getTransactionCode());
            int row = cstmt.executeUpdate();
            if (row > 0) {
                LOGGER.info("Update fee transaction SUCCESS, transaction code: {}, command code: {}",
                        feeTransaction.getTransactionCode(), feeTransaction.getCommandCode());
            }
        } catch (SQLException e) {
            LOGGER.error("Add fee transaction FAIL, transaction code: {}, command code: {}, {}",
                    feeTransaction.getTransactionCode(), feeTransaction.getCommandCode(), e.getMessage());
        } finally {
            genericDA.closePreparedStatement(cstmt);
            genericDA.closeConnection(conn);
        }
        LOGGER.info("FINISH update fee transaction, transaction code: {}, command code: {}",
                feeTransaction.getTransactionCode(), feeTransaction.getCommandCode());
    }

    public List<FeeTransaction> getFeeTransactionByTotalScan() throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        List<FeeTransaction> feeTransactionList = new ArrayList<>();
        LOGGER.info("START get fee transaction by total scan");
        try {
            conn = connectionPool.getConnection();
            String sql = "SELECT * FROM feetransaction WHERE feetransaction.totalscan < 5 AND feetransaction.totalscan > 0";
            cstmt = conn.prepareCall(sql);
            rs = cstmt.executeQuery();
            while (rs.next()) {
                FeeTransaction feeTransaction = new FeeTransaction();
                feeTransaction.setTransactionCode(rs.getString(FeeCommandConstant.TRANSACTION_CODE));
                feeTransaction.setCommandCode(rs.getString(FeeCommandConstant.COMMAND_CODE));
                feeTransaction.setFeeAmount(rs.getInt(FeeCommandConstant.FEE_AMOUNT));
                feeTransaction.setStatus(rs.getString(FeeCommandConstant.STATUS));
                feeTransaction.setAccountNumber(rs.getString(FeeCommandConstant.ACCOUNT_NUMBER));
                feeTransaction.setTotalScan(rs.getInt(FeeCommandConstant.TOTAL_SCAN));
                feeTransaction.setRemark(rs.getString(FeeCommandConstant.REMARK));
                feeTransaction.setCreateDate(LocalDateTime.parse(rs.getString(FeeCommandConstant.CREATE_DATE)));
                feeTransaction.setModifiedDate(LocalDateTime.parse(rs.getString(FeeCommandConstant.MODIFIED_DATE)));
                feeTransactionList.add(feeTransaction);
            }
        } catch (SQLException e) {
            LOGGER.error("Get fee transaction by total scan");
            return null;
        } finally {
            genericDA.closeResultSet(rs);
            genericDA.closePreparedStatement(cstmt);
            genericDA.closeConnection(conn);
        }
        LOGGER.info("FINISH get fee transaction by total scan");
        return feeTransactionList;
    }
}
