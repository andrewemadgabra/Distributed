package com.example.andrew.newsapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LocalNewsFragment extends Fragment {

    public final String URL = "jdbc:mysql://192.168.1.6/news";
    public final String USERNAME = "username";
    public final String PASSWORD = "password";

    private ArrayList<News> newsArrayList = new ArrayList<>();
    private RecyclerView newsRecyclerView;
    private LocalNewsAdapter newsAdapter;

    private static ArrayList<News> newsList = new ArrayList<>();


    FloatingActionButton floatingActionButton;

    public LocalNewsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_local_news, container, false);

        newsRecyclerView = view.findViewById(R.id.rv_news_recycler_view);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        newsAdapter = new LocalNewsAdapter(newsArrayList, getContext());

        DataBaseConnectionTask dataBaseConnectionTask = new DataBaseConnectionTask();
        dataBaseConnectionTask.execute();

        return view;
    }

    public class DataBaseConnectionTask extends AsyncTask<Void, Void, Void> {


        String news;

        @Override
        protected Void doInBackground(Void... voids) {

            getNews();

            try {
                Socket socket = new Socket("10.0.2.2", 500);

                ObjectOutputStream os = new ObjectOutputStream(socket.getOutputStream());

                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());

                os.writeObject("");
                os.flush();

                news = (String) ois.readObject();

//                os.close();
//                ois.close();
//
//                socket.close();

            } catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            newsAdapter = new LocalNewsAdapter(newsList, getContext());
            newsRecyclerView.setAdapter(newsAdapter);
            Toast.makeText(getContext(), news, Toast.LENGTH_SHORT).show();
            super.onPostExecute(result);
        }
    }

    public void getNews() {

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            Statement stm = con.createStatement();

            String sql = "SELECT * FROM news";

            final ResultSet resultSet = stm.executeQuery(sql);

            while (resultSet.next()) {

                String title;
                String category;
                String date;
                String image;

                title = resultSet.getString(4);
                category = resultSet.getString(1);
                date = resultSet.getString(3);
                image = resultSet.getString(5);

                News news = new News(title, category, date, image);

                newsList.add(news);

            }

            con.close();
            stm.close();

        } catch (Exception exception) {
            System.out.println("Where is your MySQL JDBC Driver?");
            exception.printStackTrace();
        }
    }
}