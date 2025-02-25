package hermesmsg.net;

import hermesmsg.client.MessageClientFactory;
import hermesmsg.client.MessageClientManager;
import hermesmsg.handler.IMessageQueueHandler;
import hermesmsg.util.MessageConverter;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

public class ConnectionManager {
    static Logger logger = Logger.getLogger(ConnectionManager.class.getName());

    private static Map<String, Connection> connectionMap = new HashMap<>();
    private static Map<String, IMessageQueueHandler> queueHandlerMap = new HashMap<>();

    public static void setConnection(String name, EmailServiceType type, Properties props) {
        logger.info(String.format("[SET] [%s] [%s]", name, type));
        connectionMap.put(name, new Connection(type, props));
        MessageClientManager.setMessageClient(name, MessageClientFactory.create(type, props));
    }

    public static void setConnection(String name, EmailServiceType type, String propertyStr) {
        try {
            Properties props = new Properties();
            props.load(new StringReader(propertyStr));
            setConnection(name, type, props);
        } catch (Exception e) {
            logger.severe(e.toString());
        }
    }

    public static void setConnection(String name, EmailServiceType type, Map<String, String> propertyMap) {
        Optional.ofNullable(propertyMap)
                .ifPresent(m -> {
                    setConnection(name, type, MessageConverter.mapToProp(m));
                });
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
