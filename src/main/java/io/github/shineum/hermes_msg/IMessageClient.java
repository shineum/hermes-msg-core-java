package io.github.shineum.hermes_msg;

import io.github.shineum.hermes_msg.entity.MessageResult;

import java.util.Properties;

public interface IMessageClient {
    IMessageClient initClient(Properties props);

    MessageResult send(String msg);
}
