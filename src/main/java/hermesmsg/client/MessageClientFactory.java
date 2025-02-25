package hermesmsg.client;

import hermesmsg.net.EmailServiceType;

import java.lang.reflect.Method;
import java.util.Properties;
import java.util.logging.Logger;

public class MessageClientFactory {
    static Logger logger = Logger.getLogger(MessageClientFactory.class.getName());

    @SuppressWarnings("unchecked")
    public static IMessageClient create(EmailServiceType type, Properties props) {
        try {
            String className = String.format("hermesmsg.client.MessageClient_%s", type.toString());
            Class clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod("initClient", Properties.class);
            return (IMessageClient) method.invoke(clazz.getConstructor().newInstance(), props);
        } catch (Exception e) {
            logger.severe(e.toString());
        }
        return null;
    }
}
