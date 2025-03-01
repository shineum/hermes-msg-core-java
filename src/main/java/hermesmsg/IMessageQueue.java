package hermesmsg;

import hermesmsg.entity.MessageResult;

public interface IMessageQueue {
    public MessageResult addMessage(String msg);
}
