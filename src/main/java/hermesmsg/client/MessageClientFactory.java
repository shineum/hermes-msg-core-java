package hermesmsg.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Properties;

public class MessageClientFactory {

    static Logger logger = LoggerFactory.getLogger(MessageClientFactory.class);

    @SuppressWarnings("unchecked")
    public static IMessageClient create(String className, Properties props) {
        try {
            Class clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod("initClient", Properties.class);
            return (IMessageClient) method.invoke(clazz.getConstructor().newInstance(), props);
        } catch (Exception e) {
            logger.error("[CREATE]", e);
        }
        return null;
    }
}
