package com.example.ibook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class BookList extends ArrayAdapter<Book> {

    private ArrayList<Book> books;
    private Context context;
    private TextView title;
    private TextView authors;
    private TextView date;
    private TextView description;
    private TextView status;
    //Get image view


    public BookList(@NonNull Context context, ArrayList<Book> books) {
        super(context, 0, books);
        this.books = books;
        this.context = context;
    }


    //Create the view for the books list
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        //Create a view if it doesn't exist
        if(view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.book_list_content, parent, false);
        }
        //Get the current book
        Book book = books.get(position);
        //Get the xml attributes
        title = view.findViewById(R.id.listBookTitle);
        authors = view.findViewById(R.id.listBookAuthors);
        date = view.findViewById(R.id.listBookDate);
        description = view.findViewById(R.id.listBookDescription);
        status = view.findViewById(R.id.listBookStatus);
        //Get the image attribute

        //Set the values for the xml attributes
        title.setText(book.getTitle());
        authors.setText(book.getAuthor());
        date.setText(book.getDate());
        //Set part of the description up to ~30 characters
        description.setText(book.getDescription().substring(0,30) + "...");
        if(book.isAvailable()){
            status.setText("Status: Available");
            status.setTextColor(0xFF1E9F01);
        }else{
            status.setText("Status: " + book.getState());
            status.setTextColor(0xFFFF0000);
        }

        //Set the image if there is one

        return view;
    }


}
