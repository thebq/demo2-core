package vn.vnpay.model;

public class Result {
    private String code;
    private String message;
    private Object data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Result(String code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("{" +
                "\"code\":\"%s\"," +
                "\"message\":\"%s\"," +
                "\"data\":%s}", code, message, data);
    }
}
