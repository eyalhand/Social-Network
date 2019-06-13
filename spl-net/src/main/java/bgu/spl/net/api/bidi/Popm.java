package bgu.spl.net.api.bidi;

public class Popm {// post or private message

    private String userName;
    private String content;

    public Popm (String userName , String content){
        this.userName = userName;
        this.content = content;
    }

    public String getUserName() {
        return userName; }

    public String getContent() {
        return content; }
}
