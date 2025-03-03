package io.github.shineum.hermes_msg;

import io.github.shineum.hermes_msg.entity.MessageResult;
import io.github.shineum.hermes_msg.entity.MessageResultCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class MessageManager {
    static Logger logger = LoggerFactory.getLogger(MessageManager.class);

    private static Map<String, IMessageClient> messageClientMap = new HashMap<>();
    private static Map<String, IMessageQueue> messageQueueMap = new HashMap<>();

    private static IMessageClient getMessageClient(String name) {
        return messageClientMap.get(name);
    }

    public static void setMessageClient(String name, IMessageClient messageClient) {
        messageClientMap.put(name, messageClient);
    }

    private static IMessageQueue getMessageQueue(String name) {
        return messageQueueMap.get(name);
    }

    public static void setMessageQueue(String name, IMessageQueue messageQueue) {
        messageQueueMap.put(name, messageQueue);
    }

    public static MessageResult addMessage(String name, String msg) {
        IMessageQueue messageQueue = getMessageQueue(name);
        if (messageQueue == null) {
            logger.warn(String.format("[ADD_MSG] No queue handler is set for [%s]", name));
            return postMessage(name, msg);
        } else {
            return messageQueue.addMessage(name, msg);
        }
    }

    public static MessageResult postMessage(String name, String msg) {
        IMessageClient messageClient = getMessageClient(name);
        if (messageClient == null) {
            return new MessageResult(MessageResultCode.RET_CODE_ERROR, String.format("No message client is set for [%s]", name));
        } else {
            logger.info(String.format("[POST_MSG] Message will be sent to message client for [%s]", name));
            return messageClient.send(msg);
        }
    }
}
