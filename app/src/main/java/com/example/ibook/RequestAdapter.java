package com.example.ibook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ibook.entities.Book;
import com.example.ibook.entities.BookRequest;
import com.example.ibook.entities.User;

import java.util.ArrayList;

public class RequestAdapter extends BaseAdapter {
    // private variable
    private ArrayList<BookRequest> requests;
    private Context context;
    private TextView message;
    private ImageView imageView;

    public RequestAdapter(ArrayList<BookRequest> requests, Context context) {
        this.requests = requests;
        this.context = context;
    }

    /**
     * The method to get the amount of books in the user list
     *
     * @return the number of users
     */
    @Override
    public int getCount() {
        return requests.size();
    }

    /**
     * Get the user object with given position
     *
     * @param position The index of the chosen user
     *
     * @return The user object with given position
     */
    @Override
    public Object getItem(int position) {
        return requests.get(position);
    }

    /**
     * The method to get the id of user in the list with given position
     *
     * @param position the given position of the user
     *
     * @return the id of the user, also the position of the user
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
            convertView = LayoutInflater.from(context).inflate(R.layout.request_list_content, parent,
                    false);
        }
        //Get the current user
        BookRequest request = requests.get(position);
        //Get the xml attributes
        message = convertView.findViewById(R.id.request_content);

        //Set the values for the xml attributes
        message.setText(request.getRequestSenderUsername());

        //Set the image if there is one
        return convertView;
    }
}
