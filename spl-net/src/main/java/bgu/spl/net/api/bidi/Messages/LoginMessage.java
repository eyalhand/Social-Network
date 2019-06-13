package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.Popm;
import bgu.spl.net.api.bidi.User;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class LoginMessage implements Message {

    private String userName;
    private String password;
    private byte[] bytes = new byte[1024];
    private int len;
    private int counter;

    public LoginMessage() {
        this.userName = null;
        this.password = null;
        this.len = 0;
        this.counter = 0;
    }

    @Override
    public void process(BidiMessagingProtocolImpl protocol) {
        ConcurrentHashMap<String, User> registeredUsers = protocol.getDataBase1().getRegisteredUsers();
        int connId = protocol.getConnectionId();
        ConcurrentHashMap<Integer,User> loggedInUsers = protocol.getDataBase1().getLoggedInUsers();
        ConnectionsImpl connections = protocol.getConnections1();
        User user = loggedInUsers.get(connId);
        User dataBaseUser = registeredUsers.get(userName);

        if (!registeredUsers.containsKey(userName) || user != null || !dataBaseUser.getPassword().equals(password) || dataBaseUser.getConnId() != -1)
            connections.send(connId , new ErrorMessage((short)2));
        else {
            synchronized (dataBaseUser) { //to avoid a case where more than one client will be able to login to one user
                if (dataBaseUser.getConnId()==-1 && loggedInUsers.putIfAbsent(connId, dataBaseUser) == null) {
                    dataBaseUser.setConnId(connId);
                    connections.send(connId, new AckMessage((short) 2));
                    for (Popm p : dataBaseUser.getUnreadPosts())
                        connections.send(connId, new NotificationMessage('1', p.getUserName(), p.getContent()));
                    dataBaseUser.getUnreadPosts().clear();
                    for (Popm pm : dataBaseUser.getUnreadPrivateMessages())
                        connections.send(connId, new NotificationMessage('0', pm.getUserName(), pm.getContent()));
                    dataBaseUser.getUnreadPrivateMessages().clear();

                } else
                    connections.send(connId, new ErrorMessage((short) 2));
            }
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
            len = Message.pushByte(nextByte,len,bytes);
        return null;
    }
}
