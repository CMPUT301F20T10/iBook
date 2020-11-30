package com.example.ibook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ibook.entities.BookRequest;

import java.util.ArrayList;


/**
 * The Adapter class for the book list
 */
public class NotificationAdapter extends BaseAdapter {

    // private variable
    private ArrayList<BookRequest> bookRequests;
    private Context context;
    // TODO: Get image view


    public NotificationAdapter(ArrayList<BookRequest> bookRequests, Context context) {
        this.bookRequests = bookRequests;
        this.context = context;
    }

    /**
     * The method to get the amount of bookRequests in the notifications
     *
     * @return the number of bookRequests
     */
    @Override
    public int getCount() {
        return bookRequests.size();
    }

    /**
     * Get the bookRequests object with given position
     *
     * @param position The index of the chosen bookRequests
     * @return The bookRequests object with given position
     */
    @Override
    public Object getItem(int position) {
        return bookRequests.get(position);
    }

    /**
     * The method to get the id of bookRequests in the list with given position
     *
     * @param position the given position of the bookRequests
     * @return the id of the user, also the position of the bookRequests
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
            convertView = LayoutInflater.from(context).inflate(R.layout.notification_list_content, parent, false);
        }
        //Get the current bookRequest
        BookRequest bookRequest = bookRequests.get(position);
        //Get the xml attributes
        TextView message = convertView.findViewById(R.id.userNameTextView);
        TextView datetime = convertView.findViewById(R.id.dateTextView);
        //TODO:Get the image attribute

        //Set the values for the xml attributes
        message.setText(bookRequest.getRequestSenderUsername()+" wants to borrow your book "+bookRequest.getRequestedBookTitle());
        datetime.setText(bookRequest.getDatetime());

        //Set the image if there is one
        return convertView;
    }
}
