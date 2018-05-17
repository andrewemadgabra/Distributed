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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class LocalNewsFragment extends Fragment {

    public final String URL = "jdbc:mysql://192.168.1.4/news";
    public final String USERNAME = "username";
    public final String PASSWORD = "password";

    private ArrayList<News> newsArrayList = new ArrayList<>();
    private RecyclerView newsRecyclerView;
    private LocalNewsAdapter newsAdapter;

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


        @Override
        protected Void doInBackground(Void... voids) {

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

                    newsArrayList.add(news);

                }

                con.close();
                stm.close();

            } catch (Exception exception) {
                System.out.println("Where is your MySQL JDBC Driver?");
                exception.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            newsAdapter = new LocalNewsAdapter(newsArrayList, getContext());
            newsRecyclerView.setAdapter(newsAdapter);
            Toast.makeText(getContext(), String.valueOf(newsArrayList.size()), Toast.LENGTH_SHORT).show();
            super.onPostExecute(result);
        }
    }
}