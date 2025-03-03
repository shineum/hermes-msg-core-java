package io.github.shineum.hermes_msg.entity;

public class MessageResult {
    MessageResultCode retCode = MessageResultCode.RET_CODE_ERROR;
    String message = null;

    public MessageResult() {
    }

    public MessageResult(MessageResultCode retCode, String message) {
        this.retCode = retCode;
        this.message = message;
    }

    public MessageResultCode getRetCode() {
        return retCode;
    }

    public void setRetCode(MessageResultCode retCode) {
        this.retCode = retCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

