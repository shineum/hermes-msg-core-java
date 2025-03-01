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
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

public class MessageHandler implements Constant {

    static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    public static void addMessage(String messageClientName, String to, String subject, String body) {
        MessageHandler.addMessage(messageClientName, new EmailMessage(to, subject, body));
    }

    public static void addMessage(String messageClientName, String from, String to, String cc, String bcc, String subject, String body, boolean isHtml) {
        MessageHandler.addMessage(messageClientName, new EmailMessage(from, to, cc, bcc, subject, body, isHtml));
    }

    public static void addMessageWithFileAttachments(String messageClientName, String from, String to, String cc, String bcc, String subject, String body, boolean isHtml, List<File> attachments) {
        MessageHandler.addMessage(messageClientName, new EmailMessage(from, to, cc, bcc, subject, body, isHtml).setFileAttachments(attachments));
    }

    public static void addMessageWithByteArrayAttachments(String messageClientName, String from, String to, String cc, String bcc, String subject, String body, boolean isHtml, List<ByteArrayAttachment> attachments) {
        MessageHandler.addMessage(messageClientName, new EmailMessage(from, to, cc, bcc, subject, body, isHtml).setByteArrayAttachments(attachments));
    }

    public static void addMessageWithHashMap(String messageClientName, HashMap<String, String> map) {
        MessageHandler.addMessage(messageClientName, MessageConverter.emailMessageFromHashMap(map));
    }

    public static void addMessageWithJSONStr(String messageClientName, String jsonStr) {
        MessageHandler.addMessage(messageClientName, MessageConverter.emailMessageFromJSONStr(jsonStr));
    }

    public static void addMessageWithJSONObject(String messageClientName, JSONObject jo) {
        MessageHandler.addMessage(messageClientName, MessageConverter.emailMessageFromJSONObject(jo));
    }

    public static void addMessageWithPropertiesStr(String messageClientName, String propStr) {
        MessageHandler.addMessage(messageClientName, MessageConverter.emailMessageFromPropertyStr(propStr));
    }

    public static void addMessageWithProperties(String messageClientName, Properties props) {
        MessageHandler.addMessage(messageClientName, MessageConverter.emailMessageFromProperties(props));
    }

    public static void addMessage(String messageClientName, EmailMessage emailMessage) {
        IMessageQueueHandler messageQueueHandler = MessageQueueManager.getQueueHandler(messageClientName);
        if (messageQueueHandler == null) {
            postMessage(messageClientName, emailMessage);
        } else {
            boolean useCompress = true;
            JSONObject options = new JSONObject().put(OPTION_USE_COMPRESS, useCompress);
            String queueMsgContentStr = MessageConverter.buildQueueMsgContentStr(emailMessage, useCompress);
            messageQueueHandler.addMessage(new JSONObject()
                    .put(TXT_CLIENT_NAME, messageClientName)
                    .put(TXT_QUEUE_MSG_CONTENT, queueMsgContentStr)
                    .put(TXT_OPTIONS, options).toString());
        }
    }

    public static void postMessage(String queueMsgJsonStr) {
        try {
            logger.debug(String.format("[POST_MESSAGE]:\n%s", queueMsgJsonStr));
            JSONObject jo = new JSONObject(queueMsgJsonStr);
            String messageClientName = jo.getString(TXT_CLIENT_NAME);
            String queueMsgContentStr = jo.getString(TXT_QUEUE_MSG_CONTENT);
            boolean useCompress = false;
            if (jo.has(TXT_OPTIONS)) {
                JSONObject options = jo.getJSONObject(TXT_OPTIONS);
                if (options.has(OPTION_USE_COMPRESS)) {
                    useCompress = "true".equals(options.get(OPTION_USE_COMPRESS).toString());
                }
            }
            MessageClientManager.getMessageClient(messageClientName).send(MessageConverter.parseQueueMsgContentJO(queueMsgContentStr, useCompress));
        } catch (Exception e) {
            logger.error("[POST]\n", e);
        }
    }

    private static void postMessage(String messageClientName, EmailMessage emailMessage) {
        try {
            logger.debug(String.format("[POST_MESSAGE]:\n%s", emailMessage.toString()));
            MessageClientManager.getMessageClient(messageClientName).send(MessageConverter.emailMessageToJSONObject(emailMessage));
        } catch (Exception e) {
            logger.error("[POST]\n", e);
        }
    }
}
