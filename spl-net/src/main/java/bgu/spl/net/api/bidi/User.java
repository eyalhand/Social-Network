package bgu.spl.net.api.bidi;

import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {

    private String username;
    private String password;
    private LinkedList<String> following;
    private ConcurrentLinkedQueue<String> followers;
    private int numOfPosts;
    private ConcurrentLinkedQueue<Popm> unreadPosts;
    private ConcurrentLinkedQueue<Popm> unreadPrivateMessages;
    private int connId;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.following = new LinkedList();
        this.followers = new ConcurrentLinkedQueue();
        this.unreadPosts = new ConcurrentLinkedQueue();
        this.unreadPrivateMessages = new ConcurrentLinkedQueue<>();
        this.connId = -1;
        this.numOfPosts = 0;
    }
    public String getUsername() {
        return username; }

    public String getPassword() {
        return password; }

    public LinkedList<String> getFollowing() {
        return following; }

    public ConcurrentLinkedQueue<String> getFollowers() {
        return followers; }

    public ConcurrentLinkedQueue<Popm> getUnreadPosts() {
        return unreadPosts; }

    public ConcurrentLinkedQueue<Popm> getUnreadPrivateMessages() {
        return unreadPrivateMessages; }

    public int getConnId() {
        return connId; }

    public void setConnId(int num) {
        connId = num; }

    public int getNumOfPosts() {
        return numOfPosts; }

    public void setNumOfPosts() {
        numOfPosts++; }
}
