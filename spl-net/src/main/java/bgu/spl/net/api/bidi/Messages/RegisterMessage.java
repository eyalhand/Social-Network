package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.User;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class RegisterMessage implements Message {

    private byte[] bytes = new byte[1024];
    private int len;
    private String userName;
    private String password;
    private int counter;

    public RegisterMessage() {
        this.userName = null;
        this.password = null;
        this.len = 0;
        this.counter = 0;
    }

    @Override
    public void process(BidiMessagingProtocolImpl protocol) {
        ConcurrentHashMap registeredUsers = protocol.getDataBase1().getRegisteredUsers();
        ConnectionsImpl connections = protocol.getConnections1();
        int connId = protocol.getConnectionId();

        if (registeredUsers.get(userName)!=null || protocol.getDataBase1().getLoggedInUsers().get(connId)!=null)
            connections.send(connId, new ErrorMessage((short) 1));
        else {
            if (registeredUsers.putIfAbsent(userName, new User(userName, password)) == null) {
                protocol.getDataBase1().getSortedRegisteredUsers().add(userName);
                connections.send(connId, new AckMessage((short) 1));
            }
            else
                connections.send(connId, new ErrorMessage((short) 1));
        }
    }

    @Override
    public Message decode(byte nextByte) {
        if (nextByte == '\0' && counter == 0 && userName == null) {
            userName = new String(bytes, 0, len, StandardCharsets.UTF_8);
            len = 0;
            counter++;
        }
        else if (nextByte == '\0' && counter == 1 && password == null) {
            password = new String(bytes, 0, len, StandardCharsets.UTF_8);
            return this;
        }
        else
            len = Message.pushByte(nextByte, len, bytes);

        return null;
    }
}
