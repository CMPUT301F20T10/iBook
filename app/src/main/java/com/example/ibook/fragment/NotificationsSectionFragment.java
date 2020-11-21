package com.example.ibook.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ibook.R;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.entities.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class NotificationsSectionFragment extends Fragment {

    private String title;
    private ListView listView;
    private ArrayList<String> notificationList;
    private String userID;
    private ArrayAdapter adapter;

    public NotificationsSectionFragment(String title) {
        this.title = title;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notification_section, container, false);
        listView = root.findViewById(R.id.notificationList);
        userID = MainActivity.database.getCurrentUserUID();
        getNotificationList();
        adapter = new ArrayAdapter<>(getContext(), R.layout.notification_list_content,
                R.id.textView, notificationList);
        setListener();
        return root;
    }

    private void setListener() {
        if (title.equals("Response")) {
        }
        if (title.equals("Request")) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                }
            });
        }
    }

    private void getNotificationList() {
        if (title.equals("Response")) {
            getResponseList();
        }
        if (title.equals("Request")) {
            getRequestList();
        }
        Collections.reverse(notificationList);
        adapter.notifyDataSetChanged();
    }

    private void getRequestList() {
        MainActivity.database
                .getDb()
                .collection("users")
                .document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        MainActivity.user = documentSnapshot.toObject(User.class);
                        notificationList = MainActivity.user.getNotificationList();
                    }
                });
    }

    private void getResponseList() {

    }
}
