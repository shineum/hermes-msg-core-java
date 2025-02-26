package hermesmsg.client.impl;

import hermesmsg.client.IMessageClient;
import org.json.JSONObject;

import java.util.Properties;

public class MS_GRAPH implements IMessageClient {
    @Override
    public IMessageClient initClient(Properties props) {
        // TODO implement initClient
        return this;
    }

    @Override
    public void send(String msg, JSONObject options) {
        // TODO send
        System.out.println(this.getClass().getName());
    }
}
