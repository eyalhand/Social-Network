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
    public void process(int connId, ConnectionsImpl connections, DataBase data) {
        User user = data.getLoggedInUsers().get(connId);
        ConcurrentHashMap dataBase = data.getDataBase();
        LinkedList<String> sendTo = new LinkedList<>();
        if (user != null) {
            user.setNumOfPosts();
            pushHashTag(user,sendTo);
            Popm post = new Popm(user.getUsername(),content);
            data.getPosts().add(post);
            for (String s: user.getFollowers())
                sendPost(user,connections,data,s,dataBase,post);
            for (String s: sendTo)
                sendPost(user,connections,data,s,dataBase,post);
            connections.send(connId,new AckMessage((short)5));
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
        Message.pushByte(nextByte,len,bytes);
        return null;
    }

    private void pushHashTag(User user, LinkedList list) {
        if (content.contains("@")) {
            int tempIndex = 0;
            int hashtagIndex = 0;
            while (hashtagIndex != -1) {
                hashtagIndex = content.indexOf('@', tempIndex);
                int backspaceIndex = content.indexOf(' ', hashtagIndex);
                tempIndex = backspaceIndex + 1;
                String taggedUser = content.substring(hashtagIndex + 1, backspaceIndex);
                if (!user.getFollowers().contains(taggedUser))
                    list.push(taggedUser);
            }
        }
    }

    private void sendPost(User user, Connections connections,DataBase data,String s,ConcurrentHashMap dataBase, Popm post) {
        if (dataBase.containsKey(s)) {
            synchronized (dataBase.get(s)) {
                int tempUserConnId = data.getDataBase().get(s).getConnId();
                if (tempUserConnId != -1) // need to sync the user so he won't logout
                    connections.send(tempUserConnId, new NotificationMessage('0', content, user.getUsername()));
                else
                    data.getDataBase().get(s).getUnreadPosts().add(post);
            }
        }
    }
}
