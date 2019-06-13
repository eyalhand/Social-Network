package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.DataBase;
import bgu.spl.net.api.bidi.User;

public class LogoutMessage implements Message {

    @Override
    public void process(int connId, ConnectionsImpl connections, DataBase dataBase) {
        User user = dataBase.getLoggedInUsers().get(connId);
        if(user != null) {
            synchronized (user) {
                dataBase.getLoggedInUsers().get(connId).setConnId(-1);
                connections.send(connId, new AckMessage((short) 3));
            }
        }
        else
            connections.send(connId,new ErrorMessage((short)3));
    }

    @Override
    public Message decode(byte nextByte) {
        return this; }
}
