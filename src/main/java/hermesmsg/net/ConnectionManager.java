package hermesmsg.net;

import hermesmsg.handler.IMessageQueueHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConnectionManager {
    private static Map<String, Connection> connectionMap = new HashMap<>();
    private static Map<String, IMessageQueueHandler> queueHandlerMap = new HashMap<>();

    public static void setConnection(String name, EmailServiceType type, Properties props) {
        connectionMap.put(name, ConnectionBuilder.createConnection(type, props));
    }

    public static void setConnection(String name, EmailServiceType type, Properties props, IMessageQueueHandler queueHandler) {
        setConnection(name, type, props);
        setMessageQueueHandler(name, queueHandler);
    }

    public static void setMessageQueueHandler(String name, IMessageQueueHandler queueHandler) {
        queueHandlerMap.put(name, queueHandler);
    }

    public static Connection getConnectionProp(String name) {
        return connectionMap.get(name);
    }

    public static IMessageQueueHandler getQueueHandler(String name) {
        return queueHandlerMap.get(name);
    }
}
