package com.thomastriplett.newsgateway;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Thomas on 4/19/2018.
 */

public class Source implements Parcelable {

    private String id;
    private String name;
    private String url;
    private String category;

    public Source() {
        id = "thomas-triplett-english";
        name = "Thomas Triplett English";
        url = "www.thomastriplett.com";
        category = "general";
    }

    public Source(String i, String n, String u, String c) {
        this.id = i;
        this.name = n;
        this.url = u;
        this.category = c;
    }

    protected Source(Parcel in) {
        id = in.readString();
        name = in.readString();
        url = in.readString();
        category = in.readString();
    }

    public static final Creator<Source> CREATOR = new Creator<Source>() {
        @Override
        public Source createFromParcel(Parcel in) {
            return new Source(in);
        }

        @Override
        public Source[] newArray(int size) {
            return new Source[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(url);
        parcel.writeString(category);
    }
}
