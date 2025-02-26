package hermesmsg.handler;

import hermesmsg.client.MessageClientManager;
import hermesmsg.entity.ByteArrayAttachment;
import hermesmsg.entity.EmailMessage;
import hermesmsg.util.Constant;
import hermesmsg.util.MessageConverter;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

public class MessageHandler implements Constant {
    static Logger logger = Logger.getLogger(MessageHandler.class.getName());

    public static void addMessage(String messageClientName, String from, String to, String cc, String bcc, String subject, String body, boolean isHtml) throws Exception {
        MessageHandler.addMessage(messageClientName, new EmailMessage(from, to, cc, bcc, subject, body, isHtml));
    }

    public static void addMessage(String messageClientName, String from, String to, String cc, String bcc, String subject, String body, boolean isHtml, List<File> attachments) throws Exception {
        MessageHandler.addMessage(messageClientName, new EmailMessage(from, to, cc, bcc, subject, body, isHtml).setFileAttachments(attachments));
    }

    public static void addMessageWithByteArrayAttachments(String messageClientName, String from, String to, String cc, String bcc, String subject, String body, boolean isHtml, List<ByteArrayAttachment> attachments) throws Exception {
        MessageHandler.addMessage(messageClientName, new EmailMessage(from, to, cc, bcc, subject, body, isHtml).setAttachments(attachments));
    }

    public static void addMessage(String messageClientName, EmailMessage msg) throws Exception {
        String jsonMsgStr = MessageConverter.getJsonMsgStr(messageClientName, msg, new JSONObject().put("useCompress", true));
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
            logger.severe("[POST]\n" + e.toString());
        }
    }
}
