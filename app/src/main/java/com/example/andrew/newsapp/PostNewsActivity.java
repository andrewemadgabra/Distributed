package com.example.andrew.newsapp;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class PostNewsActivity extends AppCompatActivity {
    private ArrayList<News> newsArrayList = new ArrayList<>();
    News test = new News("test","test","test","test","test","test");
    String FILENAME = "cach.ser";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_news);

        //save data in file
        newsArrayList.add(test);
        try{

            FileOutputStream fileOut = new FileOutputStream(FILENAME);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(test);
            Toast.makeText(this, (CharSequence) out, Toast.LENGTH_SHORT);
            System.out.println(out);
            out.close();
            fileOut.close();
        }catch(IOException i){
            i.printStackTrace();
        }
        ////////////////////////////////////////******************\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

        //fetch data if no connection
        News e = null;
        try{
            FileInputStream fileIn = new FileInputStream (FILENAME);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            e = (News) in.readObject();
            in.close();
            fileIn.close();
        }catch(IOException i){
            i.printStackTrace();
            return;
        }catch(ClassNotFoundException c){
            System.out.println("Employee class not found");
            c.printStackTrace();
            return;
        }

        System.out.println("Deserialized News...");
        System.out.println("Title: "+e.getTitle());
        System.out.println("Category: "+e.getCategory());
        System.out.println("Body: "+e.getBody());
        System.out.println("date: "+e.getDate());
    }
}
