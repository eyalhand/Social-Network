package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.User;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class StatMessage implements Message {

    private byte[] bytes = new byte[1024];
    private int len;
    private String userName;

    public StatMessage() {
        this.len = 0;
        this.userName = null;
    }

    @Override
    public void process(BidiMessagingProtocolImpl protocol) {
        ConcurrentHashMap<String,User> registeredUsers = protocol.getDataBase1().getRegisteredUsers();
        int connId = protocol.getConnectionId();
        ConnectionsImpl connections = protocol.getConnections1();
        User user = protocol.getDataBase1().getLoggedInUsers().get(connId);

        User statUser = registeredUsers.get(userName);
        if (user == null || statUser == null)
            connections.send(connId,new ErrorMessage((short)8));
        else {
            connections.send(connId, new AckMessage((short)8,statUser.getNumOfPosts(),statUser.getFollowers().size(),statUser.getFollowing().size()));
        }
    }

    @Override
    public Message decode(byte nextByte) {
        if (nextByte == '\0') {
            userName = new String(bytes, 0, len, StandardCharsets.UTF_8);
            return this;
        }
        len = Message.pushByte(nextByte,len,bytes);
        return null;
    }
}
