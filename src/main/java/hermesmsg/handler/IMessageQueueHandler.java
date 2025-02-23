package hermesmsg.handler;

import java.util.List;

public interface IMessageQueueHandler {
    public void addMessage(String jsonMsgStr);
}
