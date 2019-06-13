package bgu.spl.net.api.bidi.Messages;

public interface StcMessage extends Message {

    byte[] encode(Message message);

    static int shortToBytes(short num, byte [] bytesArr, int index) {
        bytesArr[index++] = (byte)((num >> 8) & 0xFF);
        bytesArr[index++] = (byte)(num & 0xFF);
        return index;
    }

    static int encodeUnionWithZeroes(byte[] bytes, byte[] temp, int index) {
        for (int i = 0; i < temp.length; i++) {
            bytes[index]=temp[i];
            index++;
        }
        bytes[index++]='\0';
        return index;
    }
}
