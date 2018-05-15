package com.example.andrew.newsapp;

/**
 * Created by miche on 5/14/2018.
 */

public class News {

    public String title;
    public String category;
    public String date;
    public String body;
    public String image;
    public String author;

    public News(String title, String category, String date, String body, String image, String author) {
        this.title = title;
        this.category = category;
        this.date = date;
        this.body = body;
        this.image = image;
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDate() {
        return date;
    }

    public String getBody() {
        return body;
    }

    public String getImage() {
        return image;
    }

    public String getAuthor() {
        return author;
    }
}
