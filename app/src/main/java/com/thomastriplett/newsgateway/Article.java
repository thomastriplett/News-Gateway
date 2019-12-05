package com.thomastriplett.newsgateway;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Thomas on 4/19/2018.
 */

public class Article implements Parcelable{

    private String author;
    private String title;
    private String description;
    private String imageURL;
    private String date;
    private String url;

    public Article(){
        author = "Thomas Triplett";
        title = "Carl Jung";
        description = "One of the most influential psychologists of the 20th century";
        imageURL = "http://www.thomastriplett.com";
        date = "05/19/98";
        url = "http://www.thomastriplett.com";
    }

    public Article(String author, String title, String description, String imageURL, String date, String url) {
        this.author = author;
        this.title = title;
        this.description = description;
        this.imageURL = imageURL;
        this.date = date;
        this.url = url;
    }

    protected Article(Parcel in) {
        author = in.readString();
        title = in.readString();
        description = in.readString();
        imageURL = in.readString();
        date = in.readString();
        url = in.readString();
    }

    public static final Creator<Article> CREATOR = new Creator<Article>() {
        @Override
        public Article createFromParcel(Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(author);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(imageURL);
        parcel.writeString(date);
        parcel.writeString(url);
    }
}
