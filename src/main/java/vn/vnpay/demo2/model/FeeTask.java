package vn.vnpay.demo2.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.demo2.dao.FeeCommandDA;
import vn.vnpay.demo2.enums.FeeCommandStatus;

import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TimerTask;

public class FeeTask extends TimerTask {
    private final FeeCommandDA feeCommandDA = new FeeCommandDA();
    private static final Logger LOGGER = LoggerFactory.getLogger(FeeTask.class);
    @Override
    public void run() {
        LOGGER.info("START cron job");
        try {
            List<FeeTransaction> feeTransactionList = feeCommandDA.getFeeTransactionByTotalScan();
            for (FeeTransaction feeTransaction : feeTransactionList) {
                feeTransaction.setTotalScan(feeTransaction.getTotalScan() + 1);
                feeTransaction.setModifiedDate(LocalDateTime.now());
                if (feeTransaction.getTotalScan() >= 5)
                    feeTransaction.setStatus(FeeCommandStatus.DUNG_THU.getCode());
                feeCommandDA.updateFeeTransaction(feeTransaction);
            }
        } catch (SQLException e) {
            LOGGER.error("Cron job Fail {}", e.getMessage());
        }
        LOGGER.info("FINISH cron job");
    }
}
