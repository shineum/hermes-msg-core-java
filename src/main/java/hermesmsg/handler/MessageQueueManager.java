package hermesmsg.handler;

import java.util.HashMap;
import java.util.Map;

public class MessageQueueManager {
    private static Map<String, IMessageQueueHandler> queueHandlerMap = new HashMap<>();

    public static void setMessageQueueHandler(String name, IMessageQueueHandler queueHandler) {
        queueHandlerMap.put(name, queueHandler);
    }

    public static IMessageQueueHandler getQueueHandler(String name) {
        return queueHandlerMap.get(name);
    }
}
