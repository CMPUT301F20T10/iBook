package com.example.ibook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ibook.R;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.interfaces.NotificationsSectionInterface;

import java.util.ArrayList;

public class ResponseSectionFragment extends Fragment implements NotificationsSectionInterface {
    private ListView listView;
    private String userID;
    private ArrayList<String> notificationList;
    private ArrayAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notification_section, container, false);
        listView = root.findViewById(R.id.notificationList);
        userID = MainActivity.database.getCurrentUserUID();
        getMessageList();
        adapter = new ArrayAdapter<>(getContext(), R.layout.notification_list_content, R.id.textView, notificationList);
        listView.setAdapter(adapter);
        setListener();
        return root;
    }

    @Override
    public void getMessageList() {

    }

    @Override
    public void setListener() {

    }
}
