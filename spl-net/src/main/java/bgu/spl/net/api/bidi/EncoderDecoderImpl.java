package bgu.spl.net.api.bidi;

import bgu.spl.net.api.bidi.Messages.*;
import bgu.spl.net.api.MessageEncoderDecoder;

public class EncoderDecoderImpl implements MessageEncoderDecoder<Message> {

    private byte[] bytes = new byte[2];
    private int len = 0;
    short result;
    private Message message = null;

    @Override
    public Message decodeNextByte(byte nextByte) {
        if (len == 0)
            pushByte(nextByte);
        else if (len == 1) {
            pushByte(nextByte);
            result = bytesToShort(bytes);
            message = decodeHelper();
            if (result == 3|| result == 7) {
                len = 0;
                return message.decode(nextByte);
            }
        }
        else if (len >= 2){
            Message output = message.decode(nextByte);
            if (output != null) {
                len = 0;
                return output;
            }
        }
        return null;//not a full message yet.
    }

    private void pushByte(byte nextByte) {
        bytes[len++] = nextByte; }

    private Message decodeHelper() {
        switch (result) {
            case 1: return new RegisterMessage();
            case 2: return new LoginMessage();
            case 3: return new LogoutMessage();
            case 4: return new FollowMessage();
            case 5: return new PostMessage();
            case 6: return new PMMessage();
            case 7: return new UserListMessage();
            case 8: return new StatMessage();
        }
        return null;
    }

    public short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }

    @Override
    public byte[] encode(Message message) {
        return ((StcMessage)message).encode(message); }
}