package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.DataBase;
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
    public void process(int connId, ConnectionsImpl connections, DataBase data) {
        ConcurrentHashMap<String,User> dataBase = data.getDataBase();
        User user = data.getLoggedInUsers().get(connId);
        if (user == null || dataBase.get(userName) == null)
            connections.send(connId,new ErrorMessage((short)8));
        else {
            connections.send(connId, new AckMessage((short)8,user.getNumOfPosts(),user.getFollowers().size(),user.getFollowing().size()));
        }
    }

    @Override
    public Message decode(byte nextByte) {
        if (nextByte == '\0') {
            userName = new String(bytes, 0, len, StandardCharsets.UTF_8);
            return this;
        }
        Message.pushByte(nextByte,len,bytes);
        return null;
    }
}
