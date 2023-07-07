package vn.vnpay;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vn.vnpay.dataaccess.FeeCommandDA;
import vn.vnpay.enums.FeeCommandStatus;
import vn.vnpay.model.FeeTransaction;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

public class MyTask extends TimerTask {
    private FeeCommandDA feeCommandDA = new FeeCommandDA();
    private static final Logger LOGGER = LoggerFactory.getLogger(MyTask.class);
    @Override
    public void run() {
        LOGGER.info("START cron job");
        try {
            List<FeeTransaction> feeTransactionList = feeCommandDA.getFeeTransactionByTotalScan();
            for (FeeTransaction feeTransaction : feeTransactionList) {
                feeTransaction.setTotalScan(feeTransaction.getTotalScan() + 1);
                long millis=System.currentTimeMillis();
                Date date = new Date(millis);
                feeTransaction.setModifiedDate(date);
                if (feeTransaction.getTotalScan().equals(5))
                    feeTransaction.setStatus(FeeCommandStatus.DUNG_THU.getCode());
                feeCommandDA.updateFeeTransaction(feeTransaction);
            }
        } catch (SQLException e) {
            LOGGER.error("CRon job Fail {}", e.getMessage());
        }
        LOGGER.info("FINISH cron job");
    }
}
