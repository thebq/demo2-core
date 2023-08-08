package vn.vnpay.demo2.dto;
/**
 * @author thebq
 * Created: 03/08/2023
 */
public class UpdateFeeCommandReq {
    private String requestId;
    private String requestTime;
    private String commandCode;

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
}
