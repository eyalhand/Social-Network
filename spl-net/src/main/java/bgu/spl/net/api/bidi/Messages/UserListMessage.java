package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.User;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UserListMessage implements Message {

    @Override
    public void process(BidiMessagingProtocolImpl protocol) {
        int connId = protocol.getConnectionId();
        ConnectionsImpl connections = protocol.getConnections1();
        User user = protocol.getDataBase1().getLoggedInUsers().get(connId);
        ConcurrentLinkedQueue sortedRegisteredUsers = protocol.getDataBase1().getSortedRegisteredUsers();

        if (user != null) {
            connections.send(connId, new AckMessage((short)7, sortedRegisteredUsers.size(), sortedRegisteredUsers));
        } else
            connections.send(connId, new ErrorMessage((short)7));
    }

    @Override
    public Message decode(byte nextByte) {
        return this; }
}
