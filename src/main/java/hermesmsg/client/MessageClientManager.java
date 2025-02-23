package hermesmsg.client;

import java.util.HashMap;
import java.util.Map;

public class MessageClientManager {
    private static Map<String, IMessageClient> clientMap = new HashMap<>();

    public static IMessageClient getMessageClient(String clientName) {
        return clientMap.get(clientName);
    }
}
