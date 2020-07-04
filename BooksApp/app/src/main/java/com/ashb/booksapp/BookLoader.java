package com.ashb.booksapp;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import static com.ashb.booksapp.MainActivity.DECODE_JSON;

/* ASYNC LOADER TASK FOR THE BOOKS FROM THE URL */
public class BookLoader extends AsyncTaskLoader<String> {
    int type;

    /* Constructor for Async BookLoader */
    public BookLoader(Context context, int type) {
        super(context);
        // when this is called the type of the decoding will be passed from MainActivity
        this.type = type;
    }

    /* Called by the worker thread which runs the specified method in the background */
    @Override
    public String loadInBackground() {
        // the type of the decoding will determine if the books should be decoded in JSON or XML
        // runs the method on an another thread
        if (type == DECODE_JSON) return Network.retrieveJSONBooks();
        else return Network.retrieveXMLBooks();
    }

    /* Called when the load in background is initiated which started the loader */
    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        // starts load in background (method belongs to loader library)
        forceLoad();

    }
}
