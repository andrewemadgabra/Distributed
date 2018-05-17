package com.example.andrew.newsapp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by miche on 5/15/2018.
 */


public class RegisterServer extends AsyncTask<Void, Void, Void> {

    private static final String URL = "jdbc:mysql://192.168.1.5/news";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    static Context context;

    private static String name, email, mobile, password;
    private static int type = 0;

    private static boolean user;

    public RegisterServer(Context context) {
        this.context = context;
    }

    @Override
    protected Void doInBackground(Void... voids) {


        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);


            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM user WHERE email = '" + "mikel@yahoo.com" + "'";
            ResultSet resultSet = statement.executeQuery(sql);

            user = resultSet.next();

            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

        if (!user) {
            RegistrationTask registrationTask = new RegistrationTask();
            registrationTask.execute();
        } else {
            Toast.makeText(context, "User Exists", Toast.LENGTH_SHORT).show();
        }
    }

    public static class RegistrationTask extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            try {
                Class.forName("com.mysql.jdbc.Driver");
                Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

                String query = "INSERT INTO `user`(`name`, `email`, `password`, `type`, `mobile`) VALUES (?, ?, ?, ?, ?)";

                PreparedStatement preparedStmt = connection.prepareStatement(query);
                preparedStmt.setString(1, name);
                preparedStmt.setString(2, email);
                preparedStmt.setString(3, password);
                preparedStmt.setInt(4, type);
                preparedStmt.setString(5, mobile);

                preparedStmt.execute();
                connection.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
//                String query = "INSERT INTO `user`(`name`, `email`, `password`, `type`, `mobile`) VALUES (" +
//                        "'" + name + "', '" + username + "', '" + email + "', " + password + ", '" + type  + "', '" + mobile +  "');";
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Toast.makeText(context, "User Registered Successfully!", Toast.LENGTH_SHORT).show();
        }
    }

}