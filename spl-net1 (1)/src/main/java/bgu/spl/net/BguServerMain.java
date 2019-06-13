package bgu.spl.net;

import bgu.spl.net.api.bidi.BidiMessagingProtocolImpl;
import bgu.spl.net.api.bidi.DataBase;
import bgu.spl.net.api.bidi.EncoderDecoderImpl;
import bgu.spl.net.srv.Server;

public class BguServerMain {
    public static void main(String [] args) {
        DataBase dataBase = new DataBase();
        Server.threadPerClient(7777, ()-> new BidiMessagingProtocolImpl(dataBase){}, ()->new EncoderDecoderImpl()).serve();
       // Server.reactor(Runtime.getRuntime().availableProcessors(),7777, ()-> new BidiMessagingProtocolImpl(dataBase){}, ()->new EncoderDecoderImpl()).serve();
    }

}
