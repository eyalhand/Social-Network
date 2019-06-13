package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.ConnectionsImpl;
import bgu.spl.net.api.bidi.DataBase;
import bgu.spl.net.api.bidi.User;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

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
    public void process(int connId, ConnectionsImpl connections, DataBase data) {
        User user = data.getLoggedInUsers().get(connId);
        LinkedList<String> listOfSuccessfulFollowUnfollow = new LinkedList<>();
        int successfulFollowsUnfollows = 0;
        if (user != null) {
            if (follow) {
                for (String s : usersList) {
                    LinkedList userFollowingList = user.getFollowing();
                    if (!userFollowingList.contains(s) && data.getDataBase().get(s) != null) {
                        userFollowingList.add(s);
                        listOfSuccessfulFollowUnfollow.add(s);
                        successfulFollowsUnfollows++;
                    }
                }
            } else {
                for (String s : usersList) {
                    LinkedList userFollowingList = user.getFollowing();
                    if (userFollowingList.contains(s)) {
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
            Message.pushByte(nextByte,len,bytes);
            if (len == 2) {
                numOfUsers = bytesToShort(bytes);
                usersList = new String[numOfUsers];
                len = 0;
            }
        }
        else {
            if (nextByte == '\0') {
                numOfUsers--;
                if (numOfUsers == 0)
                    return this;
                usersList[index++] = new String(bytes, 0, len, StandardCharsets.UTF_8);
                len = 0;
            }
            Message.pushByte(nextByte,len,bytes);
        }
        return null;
    }

    public short bytesToShort(byte[] byteArr) {
        short result = (short) ((byteArr[0] & 0xff) << 8);
        result += (short) (byteArr[1] & 0xff);
        return result;
    }
}
