package bgu.spl.net.api.bidi.Messages;

import bgu.spl.net.api.bidi.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

public class PostMessage implements Message {

    private byte[] bytes = new byte[1024];
    private int len;
    private String content;

    public PostMessage() {
        this.len = 0;
        this.content = null;
    }

    @Override
    public void process(BidiMessagingProtocolImpl protocol) {
        ConcurrentHashMap<String, User> registeredUsers = protocol.getDataBase1().getRegisteredUsers();
        int connId = protocol.getConnectionId();
        ConnectionsImpl connections = protocol.getConnections1();
        User user = protocol.getDataBase1().getLoggedInUsers().get(connId);
        LinkedList<String> sendTo = new LinkedList<>();

        if (user != null) {
            connections.send(connId,new AckMessage((short)5));
            user.setNumOfPosts();
            pushHashTag(user,sendTo);
            Popm post = new Popm(user.getUsername(),content);
            protocol.getDataBase1().getPosts().add(post);
            for (String s: user.getFollowers())
                sendPost(user,connections,s,registeredUsers,post);
            for (String s: sendTo)
                sendPost(user,connections,s,registeredUsers,post);
        }
        else
            connections.send(connId,new ErrorMessage((short)5));
    }

    @Override
    public Message decode(byte nextByte) {
        if (nextByte == '\0') {
            content = new String(bytes, 0, len, StandardCharsets.UTF_8);
            return this;
        }
        len = Message.pushByte(nextByte,len,bytes);
        return null;
    }

    private void pushHashTag(User user, LinkedList list) {
        String tempString = content;
        while (tempString.contains("@")) {
            String taggedUser;
            int hashTagindex = tempString.indexOf('@');
            tempString = tempString.substring(hashTagindex + 1);
            int backSpaceIndex = tempString.indexOf(' ');
            if (backSpaceIndex == -1) {
                taggedUser = tempString;
                tempString = "";
            }
            else {
                taggedUser = tempString.substring(0, backSpaceIndex);
                tempString = tempString.substring(backSpaceIndex + 1);
            }
            if (!user.getFollowers().contains(taggedUser) && !list.contains(taggedUser))
                list.push(taggedUser);

        }
    }

    private void sendPost(User user, Connections connections, String s, ConcurrentHashMap<String,User> registeredUsers, Popm post) {
        if (registeredUsers.containsKey(s)) {
            synchronized (registeredUsers.get(s)) { //to avoid a case where a user won't be able to logout while sending Post
                int tempUserConnId = registeredUsers.get(s).getConnId();
                if (tempUserConnId != -1) // need to sync the user so he won't logout
                    connections.send(tempUserConnId, new NotificationMessage('1', user.getUsername(), content));
                else
                    registeredUsers.get(s).getUnreadPosts().add(post);
            }
        }
    }
}
