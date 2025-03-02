package hermesmsg;

import hermesmsg.entity.MessageResult;

public interface IMessageQueue {
    MessageResult addMessage(String name, String msg);
}
