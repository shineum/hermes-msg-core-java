package hermesmsg.net;

import hermesmsg.client.IMessageClient;
import lombok.Data;

import java.util.Properties;

@Data
public class Connection {
    private EmailServiceType type = null;
    private Properties properties = null;

    public Connection(EmailServiceType type, Properties properties) {
        this.type = type;
        this.properties = properties;
    }

    public IMessageClient getMessageClient() {
        return null;
    }
}
