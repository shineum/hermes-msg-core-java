package hermesmsg.net;

import hermesmsg.client.IMessageClient;

import java.util.Properties;
import java.util.logging.Logger;

public class Connection {
    private EmailServiceType type = null;
    private Properties properties = null;

    Logger logger = Logger.getLogger(Connection.class.getName());

    public Connection(EmailServiceType type, Properties properties) {
        this.type = type;
        this.properties = properties;
    }

    public IMessageClient getMessageClient() {
        return null;
    }
}
