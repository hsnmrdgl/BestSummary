package com.example.bestsummary;

import android.media.Image;

public class Books {
    private String BookName, Author;
    private String ImageUrl;

    public Books(String bookName, String author, String imageUrl) {
        BookName = bookName;
        Author = author;
        ImageUrl = imageUrl;
    }

    public Books() {
    }

    public String getBookName() {
        return BookName;
    }

    public void setBookName(String bookName) {
        BookName = bookName;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
