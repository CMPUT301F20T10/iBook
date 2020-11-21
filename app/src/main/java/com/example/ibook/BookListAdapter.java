package com.example.ibook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ibook.activities.MainActivity;
import com.example.ibook.entities.Book;

import java.util.ArrayList;


/**
 * The Adapter class for the book list
 */
public class BookListAdapter extends BaseAdapter {

    // private variable
    private ArrayList<Book> books;
    private Context context;
    private TextView title;
    private TextView authors;
    private TextView date;
    private TextView description;
    private TextView status;
    private ImageView imageView;
    // TODO: Get image view

    /**
     * The constructor of the BookListAdapter
     *
     * @param books   The data of the books
     * @param context The context of the current activity
     */
    public BookListAdapter(ArrayList<Book> books, Context context) {
        this.books = books;
        this.context = context;
    }

    /**
     * The method to get the amount of books in the book list
     *
     * @return the number of books
     */
    @Override
    public int getCount() {
        return books.size();
    }

    /**
     * Get the book object with given position
     *
     * @param position The index of the chosen book
     *
     * @return The book object with given position
     */
    @Override
    public Object getItem(int position) {
        return books.get(position);
    }

    /**
     * The method to get the id of book in the list with given position
     *
     * @param position the given position of the book
     *
     * @return the id of the book, also the position of the book
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * The method to display the information of items in the list view.
     *
     * @return The view of the each item in the list
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.book_list_content, parent
                    , false);
        }
        //Get the current book
        Book book = books.get(position);
        //Get the xml attributes
        title = convertView.findViewById(R.id.listBookTitle);
        authors = convertView.findViewById(R.id.listBookAuthors);
        date = convertView.findViewById(R.id.listBookDate);
        description = convertView.findViewById(R.id.listBookDescription);
        status = convertView.findViewById(R.id.listBookStatus);
        imageView = convertView.findViewById(R.id.listImageView);

        //Set the values for the xml attributes
        title.setText(book.getTitle());
        authors.setText(book.getAuthors());
        date.setText(book.getDate());

        //Set part of the description up to ~30 characters
        String bookDescription = book.getDescription();
        if (bookDescription.length() > 30) {
            description.setText(bookDescription.substring(0, 30) + "...");
        } else {
            description.setText(bookDescription + "...");
        }
        if (book.isAvailable()) {
            status.setText("Status: Available");
            status.setTextColor(0xFF1E9F01);
        } else {
            status.setText("Status: " + book.getStatus());
            status.setTextColor(0xFFFF0000);
        }
        MainActivity.database.downloadImage(imageView, book.getBookID());

        //Set the image if there is one
        return convertView;
    }
}
