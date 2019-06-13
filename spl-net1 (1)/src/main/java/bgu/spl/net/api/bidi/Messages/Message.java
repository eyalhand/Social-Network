package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.DataBase;

import java.util.Arrays;

public interface Message {

    void process(int connId, ConnectionsImpl connections, DataBase dataBase);

    Message decode(byte nextByte);

    static void pushByte(byte nextByte, int len, byte[] bytes) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
    }
}
