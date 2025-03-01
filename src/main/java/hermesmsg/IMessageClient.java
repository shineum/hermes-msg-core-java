package hermesmsg;

import hermesmsg.entity.MessageResult;

import java.util.Properties;

public interface IMessageClient {
    IMessageClient initClient(Properties props);

    MessageResult send(String msg);
}
