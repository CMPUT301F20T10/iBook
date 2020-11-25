package com.example.ibook;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.ViewBookActivity;
import com.example.ibook.entities.Book;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.core.content.ContextCompat.startActivity;


/**
 * The Adapter class for the book list
 */
public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.ViewHolder> {

    // private variable
    private ArrayList<Book> books;
    private Context context;

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
     * Creates the view holder when the list gets created to manage all the books
     * and keep scrolling as an on need basis.
     *
     * @param parent
     * @param viewType
     *
     * @return
     */
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_content, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Updates data for a list item when needed. Works better than using notifydatasetchange all the time.
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Book book = books.get(position);
        if (book != null) {
            holder.setData(book);
        }
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

    @Override
    public int getItemCount() {
        return books.size();
    }

    /**
     * Class ViewHolder manages the view for a certain list item.
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView authors;
        TextView date;
        TextView description;
        TextView status;
        ImageView imageView;

        /**
         * When the viewHolder is created we get its attributes from xml.
         *
         * @param convertView
         */
        public ViewHolder(@NonNull View convertView) {
            super(convertView);
            //Get the xml attributes
            title = convertView.findViewById(R.id.listBookTitle);
            authors = convertView.findViewById(R.id.listBookAuthors);
            date = convertView.findViewById(R.id.listBookDate);
            description = convertView.findViewById(R.id.listBookDescription);
            status = convertView.findViewById(R.id.listBookStatus);
            imageView = convertView.findViewById(R.id.listImageView);
        }

        /**
         * Sets the attributes for a specific book in the list view.
         * Also downloads and sets the image from the database which works even for
         * asynchronous tasks.
         *
         * @param book
         */
        void setData(final Book book) {
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

            //Set the image icon
            if ((book != null) && (book.getBookID() != null) && (imageView != null)) {
                MainActivity.database.downloadImage(imageView, book.getBookID(), false);
            }

            //Set the click listener for the list item and call the view book activity
            //Removes the need for an on click listener in the other classes.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context.getApplicationContext(), ViewBookActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    intent.putExtra("BOOK_ID", book.getBookID());
                    intent.putExtra("OWNER", book.getOwner());
                    intent.putExtra("STATUS", book.getStatus().toString());
                    startActivity(context.getApplicationContext(), intent, null);

                }
            });
        }
    }

}
