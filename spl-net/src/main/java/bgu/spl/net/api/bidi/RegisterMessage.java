package bgu.spl.net.api.bidi;

import bgu.spl.net.api.Message;

public class RegisterMessage implements Message {

    private String userName;
    private int password;

    public RegisterMessage(String userName, int password) {
        this.userName = userName;
        this.password = password;
    }

    @Override
    public void process(int id,Connections connections) {
        
    }
}
