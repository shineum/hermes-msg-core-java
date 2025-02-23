package hermesmsg.net;

import java.util.Properties;

public class ConnectionBuilder {
    public static Connection createConnection(EmailServiceType type, Properties props) {
        return new Connection(type, props);
    }
}
