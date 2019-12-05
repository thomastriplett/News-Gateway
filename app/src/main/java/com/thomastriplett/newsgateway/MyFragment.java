package com.thomastriplett.newsgateway;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentContainer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class MyFragment extends Fragment {
    public static final String AUTHOR = "AUTHOR";
    public static final String TITLE = "TITLE";
    public static final String DESCRIPTION = "DESCRIPTION";
    public static final String IMAGE_URL = "IMAGE_URL";
    public static final String DATE = "DATE";
    public static final String URL = "URL";
    public static final String COUNT = "COUNT";

    private static final String TAG = "MyFragment";

    public static MainActivity mainActivity;

    public static final MyFragment newInstance(Article article, MainActivity ma, int count)
    {
        mainActivity = ma;
        MyFragment f = new MyFragment();
        Bundle bdl = new Bundle(5);
        bdl.putString(AUTHOR, article.getAuthor());
        bdl.putString(TITLE, article.getTitle());
        bdl.putString(DESCRIPTION, article.getDescription());
        bdl.putString(IMAGE_URL, article.getImageURL());
        bdl.putString(DATE, article.getDate());
        bdl.putString(URL, article.getUrl());
        bdl.putInt(COUNT, count);
        f.setArguments(bdl);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        String author = getArguments().getString(AUTHOR);
        String title = getArguments().getString(TITLE);
        String description = getArguments().getString(DESCRIPTION);
        final String imageURL = getArguments().getString(IMAGE_URL);
        String date = getArguments().getString(DATE);
        String url = getArguments().getString(URL);
        int count = getArguments().getInt(COUNT);
        View v = inflater.inflate(R.layout.myfragment_layout, container, false);

        TextView titleView = (TextView)v.findViewById(R.id.title_view);
        titleView.setText(title);
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getArguments().getString(URL);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        TextView authorView = (TextView)v.findViewById(R.id.author_view);
        if(!author.equals("null")) {
            authorView.setText(author);
        }
        else{
            authorView.setText("No Author Provided");
        }

        TextView descriptionView = v.findViewById(R.id.description_view);
        if(!description.equals("null")) {
            if (description.length() > 300) {
                String trimmedDescription = description.substring(0, 300);
                int lastWord = trimmedDescription.lastIndexOf(" ");
                String finalDescription = trimmedDescription.substring(0, lastWord);
                descriptionView.setText(finalDescription + "...");
            } else {
                descriptionView.setText(description);
            }
        }
        else{
            descriptionView.setText("No Description Provided");
        }

        descriptionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getArguments().getString(URL);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        TextView dateView = v.findViewById(R.id.date_view);
        if(!date.equals("null")) {
            String formatDate = date.substring(5, 7) + "/" + date.substring(8, 10) + "/" + date.substring(0, 4) + " " + date.substring(11, 16);
            dateView.setText(formatDate);
        }
        else{
            dateView.setText("No Date Provided");
        }
        final ImageView imageView = v.findViewById(R.id.image_view);

        if (imageURL != null) {
            Picasso picasso = new Picasso.Builder(this.mainActivity).listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                    // Here we try https if the http image attempt failed
                    final String changedUrl = imageURL.replace("http:", "https:");
                    picasso.load(changedUrl)
                            .into(imageView);
                }
            }).build();

            picasso.load(imageURL)
                    .into(imageView);
        } else {
            Picasso.with(this.mainActivity).load(imageURL)
                    .into(imageView);
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = getArguments().getString(URL);
                Intent browserIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        TextView countView = v.findViewById(R.id.count_view);
        int newCount = count+1;
        countView.setText(newCount+" of "+10);

        return v;
    }
}