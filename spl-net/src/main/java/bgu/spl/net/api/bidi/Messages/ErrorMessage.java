package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;

public class ErrorMessage implements StcMessage {

    private short ansToOpCode;

    public ErrorMessage(short ansToOpCode) {
        this.ansToOpCode = ansToOpCode;
    }

    @Override
    public byte[] encode(Message message) {
        byte [] bytes = new byte[4];
        int index = StcMessage.shortToBytes((short)11,bytes,0);
        StcMessage.shortToBytes(ansToOpCode,bytes,index);
        return bytes;
    }

    @Override
    public void process(BidiMessagingProtocolImpl protocol) {}

    @Override
    public Message decode(byte nextByte) {
        return null;
    }
}
