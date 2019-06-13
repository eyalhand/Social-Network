package bgu.spl.net.api.bidi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataBase {

    private ConcurrentHashMap<Integer, User> loggedInUsers;
    private ConcurrentHashMap<String, User> registeredUsers;
    private ConcurrentLinkedQueue <String> sortedRegisteredUsers;
    private ConcurrentLinkedQueue <Popm> posts;
    private ConcurrentLinkedQueue <Popm> pm;

    public DataBase() {
        this.loggedInUsers = new ConcurrentHashMap<>();
        this.registeredUsers = new ConcurrentHashMap<>();
        this.sortedRegisteredUsers = new ConcurrentLinkedQueue<>();
        this.posts = new ConcurrentLinkedQueue<>();
        this.pm = new ConcurrentLinkedQueue<>();
    }

    public ConcurrentHashMap<Integer,User> getLoggedInUsers() {
        return loggedInUsers; }

    public ConcurrentHashMap<String,User> getRegisteredUsers() {
        return registeredUsers; }

    public ConcurrentLinkedQueue <String> getSortedRegisteredUsers () {
        return sortedRegisteredUsers;
    }

    public ConcurrentLinkedQueue<Popm> getPm() {
        return pm;
    }

    public ConcurrentLinkedQueue<Popm> getPosts() {
        return posts;
    }
}
