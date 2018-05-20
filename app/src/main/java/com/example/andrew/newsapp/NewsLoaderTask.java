package com.example.andrew.newsapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by miche on 5/17/2018.
 */

public class NewsLoaderTask extends AsyncTaskLoader<ArrayList<News>> {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String NEWS_API_URL = "https://newsapi.org/v2/top-headlines?country=us&category=&apiKey=c4d3397c9bf645918c1fc9bf2a91192f";
    private ArrayList<News> newsList = new ArrayList<>();

    public NewsLoaderTask(@NonNull Context context, ArrayList<News> newsList) {
        super(context);
        this.newsList = newsList;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onStopLoading();
    }

    @Override
    public void deliverResult(ArrayList<News> data) {
        super.deliverResult(data);
    }

    @Override
    public ArrayList<News> loadInBackground() {

        URL url = createUrl(NEWS_API_URL);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "HTTP Request Error", e);
        }

        try {
            newsList.clear();

            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray resultsArray = baseJsonResponse.getJSONArray("articles");

            for (int i = 0; i < resultsArray.length(); i++) {

                JSONObject newsJsonObject = resultsArray.getJSONObject(i);
                JSONObject source = newsJsonObject.optJSONObject("source");


                String title;
                if (newsJsonObject.has("title")) {
                    title = newsJsonObject.optString("title");
                } else {
                    title = "Title N/A";
                }

                String sectionName;
                if (source.has("name")) {
                    sectionName = source.optString("name");
                } else {
                    sectionName = "Section N/A";
                }

                String publishDate;
                if (newsJsonObject.has("publishedAt")) {
                    publishDate = newsJsonObject.optString("publishedAt");

                    SimpleDateFormat newsDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date newsDate = newsDateFormat.parse(publishDate);
                        String newsDateString = newsDateFormat.format(newsDate);
                        publishDate = null;
                        publishDate = newsDateString;
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    publishDate = "Published Date N/A";
                }

                String image;
                if (newsJsonObject.has("urlToImage")) {
                    image = newsJsonObject.optString("urlToImage");
                } else {
                    image = "Image N/A";
                }

                News news = new News(title, sectionName, publishDate, image);

                newsList.add(news);
            }
            return newsList;

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error with parsing News JSON results: ", e);
        }
        return null;
    }

    private String makeHttpRequest(URL url) throws IOException {

        String jsonResponse = "";
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.connect();

            inputStream = urlConnection.getInputStream();
            jsonResponse = readFromStream(inputStream);

        } catch (IOException e) {
            Log.e(LOG_TAG, "HTTP URL Problem", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private String readFromStream(InputStream inputStream) throws IOException {

        StringBuilder output = new StringBuilder();

        if (inputStream != null) {

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);

            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    private URL createUrl(String stringUrl) {

        URL url = null;

        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException exception) {
            Log.e(LOG_TAG, "Error with creating URL", exception);
            return null;
        }
        return url;
    }
}