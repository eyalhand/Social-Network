package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.Popm;
import bgu.spl.net.api.bidi.User;

import java.nio.charset.StandardCharsets;

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
    public void process(BidiMessagingProtocolImpl protocol) {
        int connId = protocol.getConnectionId();
        ConnectionsImpl connections = protocol.getConnections1();
        User user = protocol.getDataBase1().getLoggedInUsers().get(connId);
        User dataBaseUser = protocol.getDataBase1().getRegisteredUsers().get(userName);

        if (user == null || dataBaseUser == null)
            connections.send(connId,new ErrorMessage((short)6));
        else {
            connections.send(connId, new AckMessage((short) 6));
            Popm pm = new Popm(user.getUsername(), content);
            protocol.getDataBase1().getPm().add(pm);
            synchronized (dataBaseUser) {// to avoid a case where a user won't be able to logout while sending PM
                int tempUserConnId = dataBaseUser.getConnId();
                if (tempUserConnId != -1)
                    connections.send(tempUserConnId, new NotificationMessage('0', user.getUsername(), content));
                else
                    dataBaseUser.getUnreadPrivateMessages().add(pm);
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
            len = Message.pushByte(nextByte,len,bytes);
        return null;
    }
}
