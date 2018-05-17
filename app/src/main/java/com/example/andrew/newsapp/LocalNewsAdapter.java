package com.example.andrew.newsapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by miche on 5/14/2018.
 */

public class LocalNewsAdapter extends RecyclerView.Adapter<LocalNewsAdapter.ViewHolder> {

    private ArrayList<News> newsArrayList = new ArrayList<>();
    private Context context;

    public LocalNewsAdapter(ArrayList<News> newsArrayList, Context context) {
        this.newsArrayList = newsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        News news = newsArrayList.get(position);

        holder.newsTitle.setText(news.getTitle());
        holder.newsCategory.setText(news.getCategory());
        holder.newsDate.setText(news.getDare());
        holder.newsImage.setImageBitmap(StringToBitMap(news.getImage()));
    }

    @Override
    public int getItemCount() {
        return newsArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView newsImage;
        public TextView newsTitle;
        public TextView newsCategory;
        public TextView newsDate;


        public ViewHolder(View itemView) {
            super(itemView);

            newsImage = itemView.findViewById(R.id.news_image);
            newsTitle = itemView.findViewById(R.id.news_title);
            newsCategory = itemView.findViewById(R.id.news_category);
            newsDate = itemView.findViewById(R.id.news_date);
        }
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte[] encodeByte = Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }
}