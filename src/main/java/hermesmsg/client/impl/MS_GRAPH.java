package hermesmsg.client.impl;

import hermesmsg.client.IMessageClient;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class MS_GRAPH implements IMessageClient {
    static Logger logger = LoggerFactory.getLogger(SMTP.class);

    @Override
    public IMessageClient initClient(Properties props) {
        // TODO implement initClient
        return this;
    }

    @Override
    public void send(String queueMsgContentStr) {

    }

    @Override
    public void send(JSONObject queueMsgContentJO) {

    }
}
