package hermesmsg.client;

import org.json.JSONObject;

import java.util.Properties;
import java.util.logging.Logger;

public interface IMessageClient {
    Logger logger = Logger.getLogger(IMessageClient.class.getName());

    IMessageClient initClient(Properties props);

    void send(String msg, JSONObject options);
}
