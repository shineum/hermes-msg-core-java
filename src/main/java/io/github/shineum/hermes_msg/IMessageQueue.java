package io.github.shineum.hermes_msg;

import io.github.shineum.hermes_msg.entity.MessageResult;

public interface IMessageQueue {
    MessageResult addMessage(String name, String msg);
}
