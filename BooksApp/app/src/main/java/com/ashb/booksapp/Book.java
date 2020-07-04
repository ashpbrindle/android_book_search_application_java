package com.ashb.booksapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/* BOOK OBJECT TO HOLD THE ATTRIBUTES OF EACH BOOK STORED */
public class Book implements Parcelable {
    /* Book Attributes */
    private String title, publisher, year;
    // authors are stored in an array list as there could be more than one
    private List<String> authors = new ArrayList<>();
    private Double price;

    public Book() {}

    /* Book get Methods */
    public String getTitle() { return this.title; }
    public List<String> getAuthors() { return this.authors; }
    public String getPublisher() { return this.publisher; }
    public String getYear() { return this.year; }
    public Double getPrice() { return this.price; }

    /* Book set Methods */
    public void setTitle(String title) { this.title = title; }
    public void addAuthor(String author) { this.authors.add(author); }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setYear(String year) { this.year = year; }
    public void setPrice(Double price) { this.price = price; }

    /* Constructor for the parcel data */
    private Book(Parcel rec) {
        // assigns all of the parceled data to the books attributes
        this.title = rec.readString();
        this.publisher = rec.readString();
        this.year = rec.readString();
        this.authors = rec.readArrayList(Book.class.getClassLoader());
        this.price = rec.readDouble();
    }

    @Override
    public int describeContents() { return 0; }

    /* Writes the data in this current Book to the parcel */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(publisher);
        dest.writeString(year);
        dest.writeList(authors);
        dest.writeDouble(price);
    }

    public static final Parcelable.Creator<Book> CREATOR = new Parcelable.Creator<Book>() {
        /* Method is used to create a Book object with teh parcel data */
        @Override
        public Book createFromParcel(Parcel in) { return new Book(in); }

        /* Creates a new array of the parcelable class */
        @Override
        public Book[] newArray(int size) { return new Book[size]; }
    };

}
