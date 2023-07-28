package vn.vnpay.demo2.enums;

public enum MetaData {
    SUCCESS(200, "Success"),
    BAD_REQUEST(400, "Bad request"),
    NOT_FOUND(404, "Not found"),
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
