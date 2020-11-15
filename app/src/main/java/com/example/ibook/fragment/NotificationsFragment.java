package com.example.ibook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ibook.R;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.ViewBookActivity;
import com.example.ibook.entities.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The class for the notification fragment
 */
public class NotificationsFragment extends Fragment {

    User currentUser;
    private FirebaseFirestore db;
    private ArrayList<String> notificationList;
    private ListView listView;
    private ArrayList<String> notifications;


    //TODO: implement other method
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        View root = inflater.inflate(R.layout.fragment_notification, container, false);
        listView = root.findViewById(R.id.listView);
        notifications = new ArrayList<>();
        final DocumentReference docRef = db.collection("users").document(MainActivity.database.getCurrentUserUID());

        final ArrayAdapter adapter = new ArrayAdapter<>(getContext(),R.layout.notification_list_content,R.id.textView,notifications);
        listView.setAdapter(adapter);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    //user object intialized
                    currentUser = documentSnapshot.toObject(User.class);
                    notificationList = currentUser.getNotificationList();
                    for (String notif:notificationList){
                        notifications.add(notif);


                    }
                    Collections.reverse(notifications);
                    adapter.notifyDataSetChanged();

                }
            }

        });
        return root;
    }
}
