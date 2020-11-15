package com.example.ibook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * The class for the notification fragment
 */
public class NotificationsFragment extends Fragment {

    User currentUser;
    private FirebaseFirestore db;
    ArrayList<String> notificationList;

    //TODO: implement other method
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        db = FirebaseFirestore.getInstance();
        View root = inflater.inflate(R.layout.fragment_notification, container, false);
        final DocumentReference docRef = db.collection("users").document(MainActivity.database.getCurrentUserUID());

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    //user object intialized
                    currentUser = documentSnapshot.toObject(User.class);
                    notificationList = currentUser.getNotificationList();
                    System.out.println("CHEEKU IS DUMB");
                    for(String book : notificationList){
                        Toast.makeText(getActivity(), book, Toast.LENGTH_SHORT).show();
                    }// for
                }
            }

        });
        return root;
    }
}
