package bgu.spl.net.api.bidi;

import bgu.spl.net.api.bidi.Messages.LogoutMessage;
import bgu.spl.net.api.bidi.Messages.Message;

public class BidiMessagingProtocolImpl implements BidiMessagingProtocol<Message> {
    
    private ConnectionsImpl connections;
    private static DataBase dataBase;
    private int connectionId;
    private boolean shouldTerminate = false;

    public BidiMessagingProtocolImpl (DataBase dataBase) {
        this.dataBase = dataBase;
    }

    @Override
    public void start(int connectionId, Connections connections) {
        this.connectionId = connectionId;
        this.connections = (ConnectionsImpl)connections;
    }

    @Override
    public void process(Message message) {
        message.process(connectionId,connections,dataBase);
        if (message instanceof LogoutMessage) {
            shouldTerminate = true; }
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }
}
