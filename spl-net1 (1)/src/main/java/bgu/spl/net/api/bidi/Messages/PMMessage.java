package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.DataBase;
import bgu.spl.net.api.bidi.Popm;
import bgu.spl.net.api.bidi.User;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;

public class PMMessage implements Message {

    private byte[] bytes = new byte[1024];
    private int len;
    private int counter;
    private String userName;
    private String content;

    public PMMessage() {
        this.len = 0;
        this.counter = 0;
        this.userName = null;
        this.content = null;
    }

    @Override
    public void process(int connId, ConnectionsImpl connections, DataBase data) {
        ConcurrentHashMap<String,User> dataBase = data.getDataBase();
        User user = data.getLoggedInUsers().get(connId);
        if (user == null || dataBase.get(userName) == null)
            connections.send(connId,new ErrorMessage((short)6));
        else {
            Popm pm = new Popm(user.getUsername(), content);
            data.getPm().add(pm);
            synchronized (data.getDataBase().get(userName)) {
                int tempUserConnId = data.getDataBase().get(userName).getConnId();
                if (tempUserConnId != -1)
                    connections.send(tempUserConnId, new NotificationMessage('1', content, user.getUsername()));
                else
                    data.getDataBase().get(userName).getUnreadPrivateMessages().add(pm);
                connections.send(connId, new AckMessage((short) 6));
            }
        }
    }

    @Override
    public Message decode(byte nextByte) {
        if (nextByte == '\0' && counter == 0) {
            userName = new String(bytes, 0, len, StandardCharsets.UTF_8);
            counter++;
            len = 0;
        }
        else if (nextByte == '\0' && counter == 1) {
            content = new String(bytes, 0, len, StandardCharsets.UTF_8);
            return this;
        }
        else
            Message.pushByte(nextByte,len,bytes);
        return null;
    }
}
