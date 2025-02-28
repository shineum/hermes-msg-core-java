package hermesmsg.handler;

import hermesmsg.client.MessageClientManager;
import hermesmsg.entity.ByteArrayAttachment;
import hermesmsg.entity.EmailMessage;
import hermesmsg.util.Constant;
import hermesmsg.util.MessageConverter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

public class MessageHandler implements Constant {

    static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public static void addMessage(String messageClientName, String to, String subject, String body) {
        MessageHandler.addMessage(messageClientName, new EmailMessage(to, subject, body));
    }

    public static void addMessage(String messageClientName, String from, String to, String cc, String bcc, String subject, String body, boolean isHtml) {
        MessageHandler.addMessage(messageClientName, new EmailMessage(from, to, cc, bcc, subject, body, isHtml));
    }

    public static void addMessage(String messageClientName, String from, String to, String cc, String bcc, String subject, String body, boolean isHtml, List<File> attachments) {
        MessageHandler.addMessage(messageClientName, new EmailMessage(from, to, cc, bcc, subject, body, isHtml).setFileAttachments(attachments));
    }

    public static void addMessageWithByteArrayAttachments(String messageClientName, String from, String to, String cc, String bcc, String subject, String body, boolean isHtml, List<ByteArrayAttachment> attachments) {
        MessageHandler.addMessage(messageClientName, new EmailMessage(from, to, cc, bcc, subject, body, isHtml).setByteArrayAttachments(attachments));
    }

    public static void addMessage(String messageClientName, EmailMessage msg) {
        String jsonMsgStr = MessageConverter.getJsonMsgStr(messageClientName, msg, new JSONObject().put(OPTION_USE_COMPRESS, true));
        IMessageQueueHandler messageQueueHandler = MessageQueueManager.getQueueHandler(messageClientName);
        if (messageQueueHandler == null) {
            postMessage(jsonMsgStr);
        } else {
            messageQueueHandler.addMessage(jsonMsgStr);
        }
    }

    public static void postMessage(String jsonMsgStr) {
        try {
            logger.info(String.format("Send Message:\n%s", jsonMsgStr));
            JSONObject jsonObject = new JSONObject(jsonMsgStr);
            String name = jsonObject.getString("connection");
            String msg = jsonObject.getString("msg");
            JSONObject options = jsonObject.getJSONObject("options");
            MessageClientManager.getMessageClient(name).send(msg, options);
        } catch (Exception e) {
            logger.error("[POST]\n", e);
        }
    }
}
