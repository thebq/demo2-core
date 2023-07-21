package vn.vnpay.enums;

public enum MetaData {
    SUCCESS(200, "Success"),
    BAD_REQUEST(400, "Bad request"),
    NO_CONTENT(204, "No Content");
    private final Integer metaCode;
    private final String message;

    MetaData(Integer metaCode, String message) {
        this.metaCode = metaCode;
        this.message = message;
    }

    public Integer getMetaCode() {
        return metaCode;
    }

    public String getMessage() {
        return message;
    }
}
