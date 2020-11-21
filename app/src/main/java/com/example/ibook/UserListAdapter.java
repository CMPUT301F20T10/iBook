package com.example.ibook;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.ibook.entities.User;

import java.util.ArrayList;


/**
 * The Adapter class for the book list
 */
public class UserListAdapter extends BaseAdapter {

    // private variable
    private ArrayList<User> users;
    private Context context;
    private TextView userName;
    // TODO: Get image view


    public UserListAdapter(ArrayList<User> users, Context context) {
        this.users = users;
        this.context = context;
    }

    /**
     * The method to get the amount of books in the user list
     *
     * @return the number of users
     */
    @Override
    public int getCount() {
        return users.size();
    }

    /**
     * Get the user object with given position
     *
     * @param position The index of the chosen user
     * @return The user object with given position
     */
    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    /**
     * The method to get the id of user in the list with given position
     *
     * @param position the given position of the user
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
            convertView = LayoutInflater.from(context).inflate(R.layout.user_list_content, parent, false);
        }
        //Get the current user
        User user = users.get(position);
        //Get the xml attributes
        userName = convertView.findViewById(R.id.userNameTextView);
        //TODO:Get the image attribute

        //Set the values for the xml attributes
        userName.setText(user.getUserName());

        //Set the image if there is one
        return convertView;
    }
}
