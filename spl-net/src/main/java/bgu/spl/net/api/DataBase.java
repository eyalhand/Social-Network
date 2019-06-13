package bgu.spl.net.api;


import bgu.spl.net.api.bidi.User;

import java.util.HashMap;

public class DataBase {

    private static DataBase ourInstance = new DataBase();
    private HashMap<String, User> dataBase;


    public static DataBase getInstance() {
        return ourInstance;
    }

    private DataBase() {
        dataBase = new HashMap<>();
    }

    public HashMap getDataBase() {
        return dataBase;
    }
}
