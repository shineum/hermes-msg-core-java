package hermesmsg.net;

import hermesmsg.client.IMessageClient;

import java.util.Properties;
import java.util.logging.Logger;

public class Connection {
    private String className = null;
    private Properties properties = null;

    Logger logger = Logger.getLogger(Connection.class.getName());

    public Connection(String className, Properties properties) {
        this.className = className;
        this.properties = properties;
    }

    public IMessageClient getMessageClient() {
        return null;
    }
}
