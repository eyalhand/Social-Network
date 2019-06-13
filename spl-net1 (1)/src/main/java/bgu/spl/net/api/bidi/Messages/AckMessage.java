package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.DataBase;
import sun.nio.cs.UTF_8;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AckMessage implements StcMessage {

    private short ansToOpCode;
    private LinkedList <String> follows;
    private ConcurrentLinkedQueue <String> registeredUsers;
    private int numOfSuccessfulFollowsUnfollows;
    private int numOfRegisteredUsers;
    private int numOfPosts;
    private int numOfFollowers;
    private int numOfFollowings;

    public AckMessage (short ansToOpCode) {// initialize ack message for register/login/logout/pm/post messages
        this.ansToOpCode = ansToOpCode;
    }

    public AckMessage (short ansToOpCode, int numOfSuccessfulFollowsUnfollows , LinkedList<String> follows) { // initialize ack message for follow message
        this.ansToOpCode = ansToOpCode;
        this.numOfSuccessfulFollowsUnfollows = numOfSuccessfulFollowsUnfollows;
        this.follows = follows;
    }

    public AckMessage (short ansToOpCode, int numOfRegisteredUsers , ConcurrentLinkedQueue <String> registeredUsers) { // initialize ack message for userList message
        this.ansToOpCode = ansToOpCode;
        this.numOfRegisteredUsers = numOfRegisteredUsers;
        this.registeredUsers = registeredUsers;
    }
     public AckMessage (short ansToOpCode, int numOfPosts, int numOfFollowers , int numOfFollowings) { // initialize ack message for stat message
        this.ansToOpCode = ansToOpCode;
        this.numOfPosts = numOfPosts;
        this.numOfFollowers = numOfFollowers;
        this.numOfFollowings = numOfFollowings;
     }

    @Override
    public byte[] encode(Message message) {
        int index = 0;
        switch (ansToOpCode) {
            case 1: {
                byte [] bytes = new byte[4];
                index = StcMessage.shortToBytes((short)10,bytes,index);
                StcMessage.shortToBytes(ansToOpCode,bytes,index);
                return bytes;
            }
            case 2: {
                byte [] bytes = new byte[4];
                index = StcMessage.shortToBytes((short)10,bytes,index);
                StcMessage.shortToBytes(ansToOpCode,bytes,index);
                return bytes;
            }
            case 3: {
                byte [] bytes = new byte[4];
                index = StcMessage.shortToBytes((short)10,bytes,index);
                StcMessage.shortToBytes(ansToOpCode,bytes,index);
                return bytes;
            }
            case 4: {
                byte [] temp = listToString(follows).getBytes(StandardCharsets.UTF_8);
                byte [] bytes = new byte[temp.length+numOfSuccessfulFollowsUnfollows+6];
                index = StcMessage.shortToBytes((short)10,bytes,index);
                index = StcMessage.shortToBytes(ansToOpCode,bytes,index);
                index = StcMessage.shortToBytes((short)numOfSuccessfulFollowsUnfollows, bytes,index);
                for (String s: follows) {
                    index = StcMessage.encodeUnionWithZeroes(bytes, s.getBytes(), index);
                }
                return bytes;
            }
            case 5: {
                byte [] bytes = new byte[4];
                index = StcMessage.shortToBytes((short)10,bytes,index);
                StcMessage.shortToBytes(ansToOpCode,bytes,index);
                return bytes;
            }
            case 6: {
                byte [] bytes = new byte[4];
                index = StcMessage.shortToBytes((short)10,bytes,index);
                StcMessage.shortToBytes(ansToOpCode,bytes,index);
                return bytes;
            }
            case 7: {
                byte [] temp = linkedQueueToString(registeredUsers).getBytes(StandardCharsets.UTF_8);
                byte [] bytes = new byte[temp.length+numOfRegisteredUsers+6];
                index = StcMessage.shortToBytes((short)10,bytes,index);
                index = StcMessage.shortToBytes(ansToOpCode,bytes,index);
                index = StcMessage.shortToBytes((short) numOfRegisteredUsers, bytes, index);
                for (String s: registeredUsers) {
                    index = StcMessage.encodeUnionWithZeroes(bytes, s.getBytes(), index);
                }
                return bytes;
            }
            case 8: {
                byte [] bytes = new byte[10];
                index = StcMessage.shortToBytes((short)10,bytes,index);
                index = StcMessage.shortToBytes(ansToOpCode,bytes,index);
                index = StcMessage.shortToBytes((short) numOfPosts, bytes, index);
                index = StcMessage.shortToBytes((short) numOfFollowers, bytes, index);
                StcMessage.shortToBytes((short) numOfFollowings, bytes, index);
                return bytes;
            }
        }
        return new byte[0];
    }

    private String listToString (LinkedList <String> list) {
        String str = "";
        for (String s : list)
            str = str + s;
        return str;
    }

    private String linkedQueueToString (ConcurrentLinkedQueue <String> list) {
        String str = "";
        for (String s : list)
            str = str + s;
        return str;
    }

    public short getAnsToOpCode() {
        return ansToOpCode; }

    @Override
    public void process(int connId, ConnectionsImpl connections, DataBase dataBase) {}

    @Override
    public Message decode(byte nextByte) {
        return null;
    }
}
