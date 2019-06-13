package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.DataBase;
import bgu.spl.net.api.bidi.User;
import java.util.concurrent.ConcurrentLinkedQueue;

public class UserListMessage implements Message {

    @Override
    public void process(int connId, ConnectionsImpl connections, DataBase data) {
        User user = data.getLoggedInUsers().get(connId);
        ConcurrentLinkedQueue dataBase = data.getSortedRegisteredUsers();
        if (user != null) {
            connections.send(connId, new AckMessage((short)7, dataBase.size(), dataBase));
        } else
            connections.send(connId, new ErrorMessage((short)7));
    }

    @Override
    public Message decode(byte nextByte) {
        return this; }
}
