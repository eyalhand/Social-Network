package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.User;

import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class FollowMessage implements Message {

    private byte[] bytes = new byte[1024];
    private int len;
    private boolean follow;
    private short numOfUsers;
    private String[] usersList;
    private int index;

    public FollowMessage() {
        this.len = 0;
        this.follow = true;
        this.numOfUsers = -1;
        this.usersList = null;
        this.index = 0;
    }

    @Override
    public void process(BidiMessagingProtocolImpl protocol) {
        ConcurrentHashMap<String, User> registeredUsers = protocol.getDataBase1().getRegisteredUsers();
        int connId = protocol.getConnectionId();
        ConnectionsImpl connections = protocol.getConnections1();
        User user = protocol.getDataBase1().getLoggedInUsers().get(connId);
        LinkedList<String> listOfSuccessfulFollowUnfollow = new LinkedList<>();
        int successfulFollowsUnfollows = 0;

        if (user != null) {
            if (follow) {
                for (String s : usersList) {
                    LinkedList userFollowingList = user.getFollowing();
                    if (!userFollowingList.contains(s) && registeredUsers.get(s) != null) {
                        userFollowingList.add(s);
                        registeredUsers.get(s).getFollowers().add(user.getUsername());
                        listOfSuccessfulFollowUnfollow.add(s);
                        successfulFollowsUnfollows++;
                    }
                }
            } else {
                for (String s : usersList) {
                    LinkedList userFollowingList = user.getFollowing();
                    if (userFollowingList.contains(s)) {
                        registeredUsers.get(s).getFollowers().remove(user.getUsername());
                        userFollowingList.remove(s);
                        listOfSuccessfulFollowUnfollow.add(s);
                        successfulFollowsUnfollows++;
                    }
                }
            }
        }
        if (successfulFollowsUnfollows == 0 || user == null)
            connections.send(connId,new ErrorMessage((short)4));
        else
            connections.send(connId,new AckMessage((short)4,successfulFollowsUnfollows,listOfSuccessfulFollowUnfollow));
    }

    @Override
    public Message decode(byte nextByte) {
        if (numOfUsers == -1 && usersList == null) {
            bytes[len] = nextByte;
            if (nextByte == '1')
                follow = false;
            numOfUsers++;
        }
        else if (numOfUsers == 0 && usersList == null) {
            len = Message.pushByte(nextByte,len,bytes);
            if (len == 2) {
                numOfUsers = bytesToShort(bytes);
                usersList = new String[numOfUsers];
                len = 0;
            }
        }
        else {
            if (nextByte == '\0') {
                numOfUsers--;
                usersList[index++] = new String(bytes, 0, len, StandardCharsets.UTF_8);
                len = 0;
                if (numOfUsers == 0)
                    return this;
                else
                    return null;
            }
            len = Message.pushByte(nextByte,len,bytes);
        }
        return null;
    }

    public short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }
}
