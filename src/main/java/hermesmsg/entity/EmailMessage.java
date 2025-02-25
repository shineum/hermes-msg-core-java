package hermesmsg.entity;

import hermesmsg.util.MessageConverter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class EmailMessage {
    Logger logger = Logger.getLogger(EmailMessage.class.getName());

    String from;
    String to;
    String cc;
    String bcc;
    String subject;
    String body;
    boolean isHtml;
    List<ByteArrayAttachment> attachments;

    public EmailMessage(String from, String to, String cc, String bcc, String subject, String body, boolean isHtml) {
        this.from = from;
        this.to = to;
        this.cc = cc;
        this.bcc = bcc;
        this.subject = subject;
        this.body = body;
        this.isHtml = isHtml;
    }

    public EmailMessage addFileAttachment(File attachment) throws Exception {
        if (this.attachments == null) {
            this.attachments = new ArrayList<>();
        }
        this.attachments.add(MessageConverter.fileToByteArrayAttachment(attachment));
        return this;
    }

    public EmailMessage addAttachment(ByteArrayAttachment attachment) {
        if (this.attachments == null) {
            this.attachments = new ArrayList<>();
        }
        this.attachments.add(attachment);
        return this;
    }

    public EmailMessage setFileAttachments(List<File> attachments) throws Exception {
        this.attachments = MessageConverter.fileListToByteArrayAttachmentList((attachments));
        return this;
    }

    public EmailMessage setAttachments(List<ByteArrayAttachment> attachments) {
        this.attachments = attachments;
        return this;
    }

    public String getBase64MessageStr(boolean compress) {
        return "";
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getBcc() {
        return bcc;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isHtml() {
        return isHtml;
    }

    public void setHtml(boolean html) {
        isHtml = html;
    }

    public List<ByteArrayAttachment> getAttachments() {
        return attachments;
    }
}
