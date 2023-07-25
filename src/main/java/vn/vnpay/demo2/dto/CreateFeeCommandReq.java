package vn.vnpay.demo2.dto;

import java.util.List;

public class CreateFeeCommandReq {
    private String requestId;
    private String requestTime;
    private String commandCode;
    private String totalRecord;
    private String totalFee;
    private String createdUser;
    private String createdDate;

    public List<CreateFeeTransactionReq> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<CreateFeeTransactionReq> transactions) {
        this.transactions = transactions;
    }

    private List<CreateFeeTransactionReq> transactions;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public String getCommandCode() {
        return commandCode;
    }

    public void setCommandCode(String commandCode) {
        this.commandCode = commandCode;
    }

    public String getTotalRecord() {
        return totalRecord;
    }

    public void setTotalRecord(String totalRecord) {
        this.totalRecord = totalRecord;
    }

    public String getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(String totalFee) {
        this.totalFee = totalFee;
    }

    public String getCreatedUser() {
        return createdUser;
    }

    public void setCreatedUser(String createdUser) {
        this.createdUser = createdUser;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
