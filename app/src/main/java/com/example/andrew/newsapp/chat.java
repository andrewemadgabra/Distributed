package com.example.andrew.newsapp;

import android.os.RemoteException;
import java.*;


public class chat implements RMI{

    private String message;

    public chat(String msg) throws RemoteException {
        message = msg;
    }

    public String say() throws RemoteException {
        return message;
    }

}
