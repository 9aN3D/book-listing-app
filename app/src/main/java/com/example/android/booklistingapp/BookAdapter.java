package com.example.android.booklistingapp;

import android.app.Activity;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.List;

public class BookAdapter extends ArrayAdapter<Book>{


    public BookAdapter(Activity context,List<Book> books) {
        super(context, 0, books);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }

        Book currentBook = getItem(position);

        ImageView bookImageView = (ImageView) listItemView.findViewById(R.id.bookImage);
        Picasso.get().load(currentBook.getImageLink()).into(bookImageView);


        TextView titleView = (TextView) listItemView.findViewById(R.id.title);
        titleView.setText(currentBook.getTItle());


        TextView authorsView = (TextView) listItemView.findViewById(R.id.authors);
        authorsView.setText(currentBook.getAuthors());


        TextView publishedDateView = (TextView) listItemView.findViewById(R.id.publishedDate);
        publishedDateView.setText(currentBook.getPublishedDate());


        TextView averageRatingView = (TextView) listItemView.findViewById(R.id.averageRating);
        String formatAverageRating = formatAverageRating(currentBook.getAverageRating());
        averageRatingView.setText(formatAverageRating);


        GradientDrawable averageRatingCircle = (GradientDrawable) averageRatingView.getBackground();
        int averageRatingColor = getAverageRatingColor(currentBook.getAverageRating());
        averageRatingCircle.setColor(averageRatingColor);

        return listItemView;
    }

    private int getAverageRatingColor(double averageRating) {
        int averageRatingColorResourceId;
        int averageRatingFloor = (int) Math.floor(averageRating);
        switch (averageRatingFloor) {
            case 0:
                averageRatingColorResourceId = R.color.average_rating0;
                break;
            case 1:
                averageRatingColorResourceId = R.color.average_rating1;
                break;
            case 2:
                averageRatingColorResourceId = R.color.average_rating2;
                break;
            case 3:
                averageRatingColorResourceId = R.color.average_rating3;
                break;
            case 4:
                averageRatingColorResourceId = R.color.average_rating4;
                break;
            case 5:
                averageRatingColorResourceId = R.color.average_rating5;
                break;
            default:
                averageRatingColorResourceId = R.color.average_rating0;
                break;
        }
        return ContextCompat.getColor(getContext(), averageRatingColorResourceId);
    }

    private String formatAverageRating(double averageRating) {
        DecimalFormat averageRatingFormat = new DecimalFormat("0.0");
        return averageRatingFormat.format(averageRating);
    }
}
