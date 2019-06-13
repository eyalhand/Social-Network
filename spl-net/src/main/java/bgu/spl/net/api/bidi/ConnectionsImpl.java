package bgu.spl.net.api.bidi;

import bgu.spl.net.srv.ConnectionHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionsImpl<T> implements Connections<T> {

    private  ConcurrentHashMap<Integer, ConnectionHandler> connections;

    public ConnectionsImpl() {
        this.connections = new ConcurrentHashMap<>(); }

    @Override
    public boolean send(int connectionId, T msg) {
        if (connections.get(connectionId) != null) {
            connections.get(connectionId).send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void broadcast(T msg) {
        for (Map.Entry<Integer,ConnectionHandler> me  : connections.entrySet()) {
            me.getValue().send(msg);
        }
    }

    @Override
    public void disconnect(int connectionId) {
        connections.remove(connectionId); }

    public ConcurrentHashMap<Integer,ConnectionHandler> getConnections() {
        return connections; }
}
