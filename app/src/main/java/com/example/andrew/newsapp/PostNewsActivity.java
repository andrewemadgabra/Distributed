package com.example.andrew.newsapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class PostNewsActivity extends AppCompatActivity {

    public final String URL = "jdbc:mysql://192.168.1.4/news";
    public final String USERNAME = "username";
    public final String PASSWORD = "password";

    private ArrayList<News> newsArrayList = new ArrayList<>();
    private RecyclerView newsRecyclerView;
    private LocalNewsAdapter newsAdapter;

    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_news);

        floatingActionButton = findViewById(R.id.fab);

        newsRecyclerView = findViewById(R.id.rv_news_recycler_view);
        newsRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        newsAdapter = new LocalNewsAdapter(newsArrayList, getApplicationContext());
        // newsRecyclerView.setAdapter(newsAdapter);

        DataBaseConnectionTask dataBaseConnectionTask = new DataBaseConnectionTask();
        dataBaseConnectionTask.execute();

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), AddNewsActivity.class));
            }
        });
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

            newsAdapter = new LocalNewsAdapter(newsArrayList, getApplicationContext());
            newsRecyclerView.setAdapter(newsAdapter);
            Toast.makeText(PostNewsActivity.this, String.valueOf(newsArrayList.size()), Toast.LENGTH_SHORT).show();
            super.onPostExecute(result);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        DataBaseConnectionTask dataBaseConnectionTask = new DataBaseConnectionTask();
        dataBaseConnectionTask.execute();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        DataBaseConnectionTask dataBaseConnectionTask = new DataBaseConnectionTask();
        dataBaseConnectionTask.execute();
    }
}