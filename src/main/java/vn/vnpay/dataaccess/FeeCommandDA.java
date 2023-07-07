package vn.vnpay.dataaccess;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.dto.CreateFeeCommandReq;
import vn.vnpay.dto.CreateFeeTransactionReq;
import vn.vnpay.enums.FeeCommandStatus;
import vn.vnpay.model.FeeCommand;
import vn.vnpay.model.FeeTransaction;
import vn.vnpay.utils.LocalProperties;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FeeCommandDA {
    private static final Logger LOGGER = LoggerFactory.getLogger(FeeCommandDA.class);
    private PreparedStatement cstmt;
    private GenericDA genericDA = new GenericDA();
    private static ComboPooledDataSource connectionPool = new ComboPooledDataSource();

    static {
        try {
            String jdbcUrl = String.valueOf(LocalProperties.get("url"));
            String userName = String.valueOf(LocalProperties.get("username"));
            String password = String.valueOf(LocalProperties.get("password"));
            Integer minPoolSize = Integer.parseInt(String.valueOf(LocalProperties.get("min-pool-size")));
            Integer maxPoolSize = Integer.parseInt(String.valueOf(LocalProperties.get("max-pool-size")));

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

    public Boolean addFeeCommand(CreateFeeCommandReq createFeeCommandReq) throws SQLException {
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
            cstmt.setDate(5, Date.valueOf(createFeeCommandReq.getCreatedDate()));

            int row = cstmt.executeUpdate();
            if (row > 0) {
                LOGGER.info("Add fee command code: {} SUCCESS", createFeeCommandReq.getCommandCode());
                CreateFeeTransactionReq createFeeTransactionReq = new CreateFeeTransactionReq();
                createFeeTransactionReq.setCommandCode(createFeeCommandReq.getCommandCode());
                createFeeTransactionReq.setFeeAmount(createFeeCommandReq.getTotalFee());
                createFeeTransactionReq.setCreateDate(createFeeCommandReq.getCreatedDate());
                createFeeTransactionReq.setModifiedDate(createFeeCommandReq.getCreatedDate());
                createFeeTransactionReq.setStatus(FeeCommandStatus.KHOI_TAO.getCode());
                createFeeTransactionReq.setTotalScan("0");
                createFeeTransactionReq.setTransactionCode(String.valueOf(UUID.randomUUID()));
                createFeeTransactionReq.setAccountNumber("1092991010");
                if (addFeeTransaction(createFeeTransactionReq))
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

    public Boolean addFeeTransaction(CreateFeeTransactionReq feeTransactionReq) throws SQLException {
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
            long millis = System.currentTimeMillis();
            Date date = new Date(millis);
            cstmt.setDate(8, date);
            cstmt.setDate(9, date);

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

    public List<FeeTransaction> getFeeTransactionByCmdCode(String commandCode) throws SQLException {
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
            String sql = "UPDATE feetransaction SET totalscan = ?, modifieddate = ?, status = ?";
            cstmt = conn.prepareCall(sql);
            cstmt.setInt(1, feeTransaction.getTotalScan());
            long millis = System.currentTimeMillis();
            Date date = new Date(millis);
            cstmt.setDate(2, date);
            cstmt.setString(3, FeeCommandStatus.THU_PHI.getCode());
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

    public List<FeeCommand> getAllFeeCommand() throws SQLException {
        Connection conn = null;
        ResultSet rs = null;
        List<FeeCommand> feeCommandList = new ArrayList<>();
        LOGGER.info("START Get command code list");
        try {
            conn = connectionPool.getConnection();
            String sql = "SELECT * FROM feecommand";
            cstmt = conn.prepareCall(sql);
            rs = cstmt.executeQuery();
            while (rs.next()) {
                FeeCommand feeCommand = new FeeCommand();
                feeCommand.setCommandCode(rs.getString("commandcode"));
                feeCommand.setTotalFee(rs.getInt("totalfee"));
                feeCommand.setTotalRecord(rs.getInt("totalrecord"));
                feeCommand.setCreatedDate(rs.getDate("createdate"));
                feeCommand.setCreatedUser(rs.getString("createuser"));
                feeCommandList.add(feeCommand);
            }
        } catch (SQLException e) {
            LOGGER.error("Get command code list FAIL");
            return null;
        } finally {
            genericDA.closeResultSet(rs);
            genericDA.closePreparedStatement(cstmt);
            genericDA.closeConnection(conn);
        }
        LOGGER.info("FINISH Get command code list");
        return feeCommandList;
    }

    public void updateFeeCommand(String commandCode, Integer totalRecord, Integer totalFee) {
        Connection conn = null;
        LOGGER.info("START fee transaction, command code: {}", commandCode);
        try {
            conn = connectionPool.getConnection();
            String sql = "UPDATE feecommand SET totalrecord = ?, totalfee = ? WHERE commandcode = ?";
            cstmt = conn.prepareCall(sql);
            cstmt.setInt(1, totalRecord);
            cstmt.setInt(2, totalFee);
            cstmt.setString(3, commandCode);
            int row = cstmt.executeUpdate();
            if (row > 0) {
                LOGGER.info("Update fee transaction SUCCESS, command code: {}", commandCode);
            }
        } catch (SQLException e) {
            LOGGER.error("Update fee transaction FAIL, command code: {}", commandCode);
        } finally {
            genericDA.closePreparedStatement(cstmt);
            genericDA.closeConnection(conn);
        }
        LOGGER.info("FINISH fee transaction, command code: {}", commandCode);
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
