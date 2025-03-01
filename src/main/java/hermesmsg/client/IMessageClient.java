package hermesmsg.client;

import org.json.JSONObject;

import java.util.Properties;

public interface IMessageClient {
    IMessageClient initClient(Properties props);

    void send(String queueMsgContentStr);

    void send(JSONObject queueMsgContentJO);
}
