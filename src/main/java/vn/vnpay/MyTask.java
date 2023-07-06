package vn.vnpay;

import vn.vnpay.dataaccess.FeeCommandDA;
import vn.vnpay.model.FeeTransaction;

import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.TimerTask;

public class MyTask extends TimerTask {
    private FeeCommandDA feeCommandDA = new FeeCommandDA();
    @Override
    public void run() {
        try {
            List<FeeTransaction> feeTransactionList = feeCommandDA.getFeeTransactionByTotalScan();
            for (FeeTransaction feeTransaction : feeTransactionList) {
                feeTransaction.setTotalScan(feeTransaction.getTotalScan() + 1);
                long millis=System.currentTimeMillis();
                Date date = new Date(millis);
                feeTransaction.setModifiedDate(date);
                if (feeTransaction.getTotalScan().equals(5))
                    feeTransaction.setStatus("03");
                feeCommandDA.updateFeeTransaction(feeTransaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
