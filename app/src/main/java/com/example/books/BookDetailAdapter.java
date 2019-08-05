package com.example.books;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class BookDetailAdapter extends ArrayAdapter<BookDetail> {


    public BookDetailAdapter(Activity context, ArrayList<BookDetail> bookDetail) {
        super(context, 0, bookDetail);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View listitem = convertView;
        if (listitem == null) {
            listitem = LayoutInflater.from(getContext()).inflate(R.layout.layout_list, parent, false);
        }

        BookDetail bookDetail = getItem(position);

        TextView titletext = listitem.findViewById(R.id.title);
        titletext.setText(bookDetail.getTitle());

        TextView authortext = listitem.findViewById(R.id.author);
        authortext.setText(bookDetail.getAuthor());

        ImageView preImage=listitem.findViewById(R.id.image);
        Picasso.get().load(bookDetail.getImage()).error(R.drawable.ic_launcher_foreground).into(preImage);
        Log.i("Image Url",bookDetail.getPreviewLink());

        return listitem;
    }
}
