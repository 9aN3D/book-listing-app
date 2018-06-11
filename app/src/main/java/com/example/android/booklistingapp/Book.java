package com.example.android.booklistingapp;

public class Book {
    private String imageLink;
    private String title;
    private String authors;
    private String publishedDate;
    private double averageRating;
    private String url;

    public Book(String imageLink, String title, String authors,
                String publishedDate, double averageRating, String url) {
        this.imageLink = imageLink;
        this.title = title;
        this.authors = authors;
        this.publishedDate = publishedDate;
        this.averageRating = averageRating;
        this.url = url;
    }

    public String getImageLink() {
        return imageLink;
    }

    public String getTItle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public String getUrl() {
        return url;
    }
}
