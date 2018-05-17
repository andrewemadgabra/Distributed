package com.example.andrew.newsapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

public class GlobalNewsFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<News>> {

    private ArrayList<News> newsArrayList = new ArrayList<>();

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private GlobalNewsAdapter newsAdapter;

    public GlobalNewsFragment() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_global_news, container, false);

        setHasOptionsMenu(true);

        recyclerView = (RecyclerView) view.findViewById(R.id.brand_nearby_recyclerview);
        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        getActivity().getSupportLoaderManager().initLoader(1, null, this).forceLoad();

        return view;
    }

    @Override
    public Loader<ArrayList<News>> onCreateLoader(int id, Bundle args) {
        return new NewsLoaderTask(getActivity(), newsArrayList);
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<News>> loader, ArrayList<News> data) {

        if (data != null || data.size() != 0) {
            newsAdapter = new GlobalNewsAdapter(data, getActivity().getApplicationContext());
            recyclerView.setAdapter(newsAdapter);
            Toast.makeText(getActivity().getApplicationContext(), "News Found !", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "No News Found !", Toast.LENGTH_LONG).show();
            return;
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<News>> loader) {
    }
}