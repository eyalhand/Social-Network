package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.DataBase;
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
    public void process(int connId, ConnectionsImpl connections, DataBase data) {
        ConcurrentHashMap<String,User> dataBase = data.getDataBase();
        User user = data.getLoggedInUsers().get(connId);
        if (!dataBase.containsKey(userName) || user != null || !user.getPassword().equals(password) || dataBase.get(userName).getConnId() != -1)
            connections.send(connId , new ErrorMessage((short)2));
        else {
            if (data.getLoggedInUsers().putIfAbsent(connId, dataBase.get(userName)) == null) {
                user.setConnId(connId);
                connections.send(connId, new AckMessage((short) 2));
                synchronized (dataBase.get(userName)) {
                    for (Popm p : dataBase.get(userName).getUnreadPosts()) {
                        connections.send(connId, new NotificationMessage('0', p.getUserName(), p.getContent()));
                    }
                    dataBase.get(userName).getUnreadPosts().clear();
                    for (Popm pm : dataBase.get(userName).getUnreadPrivateMessages()) {
                        connections.send(connId, new NotificationMessage('1', pm.getUserName(), pm.getContent()));
                    }
                    dataBase.get(userName).getUnreadPrivateMessages().clear();
                } // sync ends here
            }
            else
                connections.send(connId, new ErrorMessage((short) 2));
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
            Message.pushByte(nextByte,len,bytes);
        return null;
    }
}
