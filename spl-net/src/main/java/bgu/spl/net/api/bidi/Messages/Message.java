package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import java.util.Arrays;

public interface Message {

    void process(BidiMessagingProtocolImpl protocol);

    Message decode(byte nextByte);

    static int pushByte(byte nextByte, int len, byte[] bytes) {
        if (len >= bytes.length) {
            bytes = Arrays.copyOf(bytes, len * 2);
        }
        bytes[len++] = nextByte;
        return len;
    }
}
