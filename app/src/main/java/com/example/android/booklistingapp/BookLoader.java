package com.example.android.booklistingapp;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.List;

public class BookLoader extends AsyncTaskLoader<List<Book>> {
    /** Tag for log messages */
    private static final String LOG_TAG = BookLoader.class.getName();
    /** Query URL */
    private String url;

    public BookLoader(Context context, String url) {
        super(context);
        this.url = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "TEST: onStartLoading() called ...");
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        Log.i(LOG_TAG, "TEST: loadInBackground() called ...");
        if (url == null){
            return null;
        }
        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<Book> books = QueryUtils.fetchBookData(url);
        return books;
    }
}
