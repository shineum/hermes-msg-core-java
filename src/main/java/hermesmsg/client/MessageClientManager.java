package hermesmsg.client;

import hermesmsg.util.MessageConverter;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.logging.Logger;

public class MessageClientManager {
    static Logger logger = Logger.getLogger(MessageClientManager.class.getName());

    private static Map<String, IMessageClient> clientMap = new HashMap<>();

    public static IMessageClient getMessageClient(String name) {
        return clientMap.get(name);
    }

    public static void setMessageClient(String name, String className, Properties props) {
        logger.info(String.format("[SET] [%s] [%s]", name, className));
        clientMap.put(name, MessageClientFactory.create(className, props));
    }

    public static void setMessageClient(String name, EmailServiceType type, Properties props) {
        String defaultClassName = String.format("%s.impl.%s", MessageClientFactory.class.getPackageName(), type.toString());
        setMessageClient(name, defaultClassName, props);
    }

    public static void setMessageClient(String name, EmailServiceType type, String propertyStr) {
        try {
            Properties props = new Properties();
            props.load(new StringReader(propertyStr));
            setMessageClient(name, type, props);
        } catch (Exception e) {
            logger.severe(e.toString());
        }
    }

    public static void setMessageClient(String name, EmailServiceType type, Map<String, String> propertyMap) {
        Optional.ofNullable(propertyMap)
                .ifPresent(m -> {
                    setMessageClient(name, type, MessageConverter.mapToProp(m));
                });
    }

}
