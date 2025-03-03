# Hermes Message Core

This is core module for Hermes Message project.

### MessageManager
- manages IMessageClient and IMessageQueue instances.
- add messages to IMessageQueue if queue is set.
- post messages to IMessageClient

### IMessageQueue
- interface
- implement addMessage method that will handle the message with queue when it called
- implement a worker that will post message from queue

### IMessageClient
- interface
- implement initClient method to initialize configuration
- implement send method to handle messages
