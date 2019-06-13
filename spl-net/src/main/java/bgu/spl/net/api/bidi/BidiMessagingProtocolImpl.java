package bgu.spl.net.api.bidi;

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
        message.process(this); }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public ConnectionsImpl getConnections1() {
        return connections; }

    public DataBase getDataBase1() {
        return dataBase; }

    public int getConnectionId() {
        return connectionId; }

    public void setShouldTerminate(){
        shouldTerminate = true; }
}
