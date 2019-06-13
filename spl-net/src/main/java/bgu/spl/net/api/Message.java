package bgu.spl.net.api;

import bgu.spl.net.api.bidi.Connections;

public interface Message {

    void process(int id, Connections connections);
}
