package hermesmsg.client;

import java.util.Properties;
import java.util.logging.Logger;

public interface IMessageClient {
    Logger logger = Logger.getLogger(IMessageClient.class.getName());

    IMessageClient initClient(Properties props);

    void send(String msg);
}
