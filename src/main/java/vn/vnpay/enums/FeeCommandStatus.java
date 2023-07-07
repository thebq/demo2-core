package vn.vnpay.enums;

public enum FeeCommandStatus {
    KHOI_TAO("01", "Khoi tao"),
    THU_PHI("02", "Thu phi"),
    DUNG_THU("03", "Dung thu");
    private final String code;
    private final String status;

    FeeCommandStatus(String code, String status) {
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }
}
