package hermesmsg.handler;

import java.util.List;
import java.util.logging.Logger;

public interface IMessageQueueHandler {
    Logger logger = Logger.getLogger(IMessageQueueHandler.class.getName());

    public void addMessage(String jsonMsgStr);
}
