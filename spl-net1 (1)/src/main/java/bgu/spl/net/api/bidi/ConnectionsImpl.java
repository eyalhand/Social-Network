package bgu.spl.net.api.bidi;

import bgu.spl.net.api.bidi.Messages.AckMessage;
import bgu.spl.net.api.bidi.Messages.Message;
import bgu.spl.net.srv.ConnectionHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl implements Connections<Message> {

    private  ConcurrentHashMap<Integer,ConnectionHandler> connections;

    public ConnectionsImpl() {
        this.connections = new ConcurrentHashMap<>(); }

    @Override
    public boolean send(int connectionId, Message msg) {
        if (connections.get(connectionId) != null) {
            connections.get(connectionId).send(msg);
            if (msg instanceof AckMessage || ((AckMessage)msg).getAnsToOpCode() == 3) {
                ConnectionHandler connectionHandler = connections.get(connectionId);
                connections.remove(connectionId,connectionHandler);
            }
            return true;
        }
        return false;
    }

    @Override
    public void broadcast(Message msg) {
        for (Map.Entry<Integer,ConnectionHandler> me  : connections.entrySet()) {
            me.getValue().send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {

    }

    public ConcurrentHashMap<Integer,ConnectionHandler> getConnections() {
        return connections; }
}
