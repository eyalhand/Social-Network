package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.User;
import java.util.concurrent.ConcurrentHashMap;

public class LogoutMessage implements Message {

    @Override
    public void process(BidiMessagingProtocolImpl protocol) {
        int connId = protocol.getConnectionId();
        ConcurrentHashMap<Integer, User> loggedInUsers = protocol.getDataBase1().getLoggedInUsers();
        ConnectionsImpl connections = protocol.getConnections1();
        User user = loggedInUsers.get(connId);

        if (user != null) {
            synchronized (user) { //to avoid a case where a user won't be able to logout while sending PM/Post
                loggedInUsers.get(connId).setConnId(-1);
                protocol.setShouldTerminate();
                if (connections.send(connId, new AckMessage((short) 3)))
                    connections.disconnect(connId);
            }
        }
        else
            connections.send(connId,new ErrorMessage((short)3));
    }

    @Override
    public Message decode(byte nextByte) {
        return this; }
}
