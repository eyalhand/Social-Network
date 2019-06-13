package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import java.nio.charset.StandardCharsets;

public class NotificationMessage implements StcMessage {

    private char pmOrPost;
    private String content;
    private String userName;

    public NotificationMessage(char pmOrPost, String userName, String content) {
        this.pmOrPost = pmOrPost;
        this.content = content;
        this.userName = userName;
    }

    public byte[] encode(Message message) {
        byte [] encodedPostingUser = userName.getBytes(StandardCharsets.UTF_8);
        byte [] encodedContent = content.getBytes(StandardCharsets.UTF_8);
        byte [] bytes = new byte[encodedContent.length +encodedPostingUser.length + 5];
        int index = StcMessage.shortToBytes((short)9,bytes,0);
        bytes[index++] = (byte)pmOrPost;
        index = StcMessage.encodeUnionWithZeroes(bytes,encodedPostingUser,index);
        StcMessage.encodeUnionWithZeroes(bytes,encodedContent,index);
        return bytes;
    }

    @Override
    public void process(BidiMessagingProtocolImpl protocol) {}

    @Override
    public Message decode(byte nextByte) {
        return null;
    }
}
