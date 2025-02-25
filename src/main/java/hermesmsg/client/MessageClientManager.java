package hermesmsg.client;

import java.util.HashMap;
import java.util.Map;

public class MessageClientManager {
    private static Map<String, IMessageClient> clientMap = new HashMap<>();

    public static IMessageClient getMessageClient(String name) {
        return clientMap.get(name);
    }

    public static void setMessageClient(String name, IMessageClient messageClient) {
        clientMap.put(name, messageClient);
    }
}
