package hermesmsg.entity;

public class MessageResult {
    public static final int RET_CODE_SUCCESS = 0;
    public static final int RET_CODE_ERROR = 1;

    int retCode = RET_CODE_ERROR;
    String message = null;

    public MessageResult() {
    }

    public MessageResult(int retCode, String message) {
        this.retCode = retCode;
        this.message = message;
    }

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
