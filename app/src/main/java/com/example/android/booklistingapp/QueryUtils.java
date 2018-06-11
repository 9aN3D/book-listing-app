package com.example.android.booklistingapp;

import android.text.TextUtils;
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
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {
    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing a JSON response.
     */
    public static List<Book> fetchBookData (String requestUrl) {
        Log.i(LOG_TAG, "TEST: fetchBookData() called ...");
        // Create URL object
        URL url = createUrl(requestUrl);
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }
        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        List<Book> books = extractFeatureFromJson(jsonResponse);

        // Return the list of books
        return books;
    }

    /**
     * Returns new URL object from the given string URL.
     * * */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the book JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
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

    /**
     * Return a list of {@link Book} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Book> extractFeatureFromJson(String earthquakeJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(earthquakeJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding books to
        List<Book> books = new ArrayList<>();
        try {

            JSONObject baseJsonResponse = new JSONObject(earthquakeJSON);

            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            for (int i = 0; i < bookArray.length(); i++) {
                // Get a single book at position i within the list of books
                JSONObject currentBook = bookArray.getJSONObject(i);

                JSONObject volumeInfo = currentBook.getJSONObject("volumeInfo");
                String title = volumeInfo.getString("title");

                String author = "";
                if (volumeInfo.has("authors")) {
                    JSONArray authors = volumeInfo.getJSONArray("authors");
                    for (int j = 0; j < authors.length(); ++j ) {
                        if (j == authors.length()-1) {
                            author += authors.getString(j);
                        } else {
                            author += authors.getString(j)+" ,";
                        }
                    }
                } else {
                    author = "Missing info of authors";
                }
                String publishedDate;
                if (volumeInfo.has("publishedDate")) {
                    publishedDate = volumeInfo.getString("publishedDate");
                } else {
                    publishedDate = "Missing info of published date";
                }
                double averageRating = 0.0;
                if (volumeInfo.has("averageRating")){
                    averageRating = volumeInfo.getDouble("averageRating");
                } else {
                    averageRating = 0.0;
                }
                String imageLink;
                if (volumeInfo.has("imageLinks")) {
                    JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                    imageLink = imageLinks.getString("smallThumbnail");
                } else {
                    imageLink = "http://leeford.in/wp-content/uploads/2017/09/image-not-found.jpg";
                }

                String url = volumeInfo.getString("infoLink");

                Book book = new Book(imageLink,title,author,publishedDate,
                        averageRating,url);

                books.add(book);
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }
        // Return the list of earthquakes
        return books;
    }
}
