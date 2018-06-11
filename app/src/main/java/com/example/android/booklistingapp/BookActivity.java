package com.example.android.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class BookActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final String LOG_TAG = BookActivity.class.getName();
    private SearchView searchView;
    private static final int BOOK_LOADER_ID = 1;
    private BookAdapter adapter;
    private TextView emptyStateTextView;
    private static final String USGS_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private String url;
    private View circleProgressBar;
    boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(LOG_TAG, "TEST: Book Activity onCreate() called");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_activity);

        // Declaration and initialization ConnectivityManager for checking internet connection
        final ConnectivityManager cm =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        checkConnection(cm);

        searchView = (SearchView) findViewById(R.id.bookSearchView);
        searchView.setQueryHint("Find a book");
        searchView.onActionViewExpanded();
        searchView.setIconified(true);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Check connection status
                checkConnection(cm);

                if (isConnected) {
                    // Update URL and restart loader to displaying new result of searching
                    updateQueryUrl(query);
                    restartLoader();
                    Log.i(LOG_TAG, "Search value: " + searchView.getQuery().toString());
                } else {
                    // Clear the adapter of previous book data
                    adapter.clear();
                    // Set mEmptyStateTextView visible
                    emptyStateTextView.setVisibility(View.VISIBLE);
                    // ...and display message: "No internet connection."
                    emptyStateTextView.setText(R.string.no_internet_connection);
                }
                String formatUserInput = query.trim().replaceAll("\\s+","+");
                url = USGS_REQUEST_URL + query +formatUserInput;
                //Toast.makeText(getBaseContext(),getUrl(),Toast.LENGTH_SHORT).show();

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //
                return false;
            }
        });

        // Find a reference to the {@link ListView} in the layout.
        ListView booksListView = (ListView) findViewById(R.id.list);
        adapter = new BookAdapter(this, new ArrayList<Book>());
        booksListView.setAdapter(adapter);

        // Find a reference to the empty view
        emptyStateTextView = (TextView) findViewById(R.id.empty_view);
        booksListView.setEmptyView(emptyStateTextView);
        // Circle progress
        circleProgressBar = findViewById(R.id.loading_indicator);

        if (isConnected) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader.
            loaderManager.initLoader(BOOK_LOADER_ID, null, this);
        } else {
            // Progress bar mapping
            Log.i(LOG_TAG, "INTERNET connection status: "
                    + String.valueOf(isConnected) +
                    ". Sorry dude, no internet - no data :(");


            circleProgressBar.setVisibility(GONE);
            // Set empty state text to display "No internet connection."
            emptyStateTextView.setText(R.string.no_internet_connection);
        }

        booksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Book currentBook = adapter.getItem(position);
                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getUrl());
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);
                startActivity(websiteIntent);
            }
        });
    }

    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "TEST: onCreateLoader() called ...");
        // Create a new loader for the given URL
        updateQueryUrl(searchView.getQuery().toString());
        return new BookLoader(this, url);
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        Log.i(LOG_TAG, "TEST: onLoadFinished() called ...");
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(GONE);
        // Set empty state text to display "No earthquakes found."
        emptyStateTextView.setText(R.string.no_books);
        Log.i(LOG_TAG, ": Books has been moved to adapter's data set. " +
                "This will trigger the ListView to update!");
        // Clear the adapter of previous earthquake data
        adapter.clear();

        if (books != null && !books.isEmpty()) {
            adapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        Log.i(LOG_TAG, "TEST: onLoaderReset() called ...");
        // Loader reset, so we can clear out our existing data.
        adapter.clear();
    }

    private void restartLoader() {
        emptyStateTextView.setVisibility(GONE);
        circleProgressBar.setVisibility(View.VISIBLE);
        getLoaderManager().restartLoader(BOOK_LOADER_ID, null, BookActivity.this);
    }


    private String updateQueryUrl(String newText) {
        String formatUserInput = newText.trim().replaceAll("\\s+","+");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(USGS_REQUEST_URL).append(formatUserInput).append("&maxResults=40");
        return url = stringBuilder.toString();
    }

    private void checkConnection(ConnectivityManager connectivityManager) {
        // Status of internet connection
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            isConnected = true;

            Log.i(LOG_TAG, "INTERNET connection status: " + String.valueOf(isConnected) +
                    ". It's time to play with LoaderManager :)");

        } else {
            isConnected = false;

        }
    }
}
