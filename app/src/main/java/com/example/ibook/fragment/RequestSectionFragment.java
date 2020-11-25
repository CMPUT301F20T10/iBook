package com.example.ibook.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.example.ibook.activities.MapsActivity;
import com.example.ibook.entities.Book;
import com.example.ibook.entities.Request;
import com.example.ibook.entities.User;
import com.example.ibook.interfaces.NotificationsSectionInterface;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

public class RequestSectionFragment extends Fragment implements NotificationsSectionInterface {
    private ListView listView;
    private String userID;
    private String requestSenderID;
    private String requestBookID;
    private ArrayList<Request> requests;
    private ArrayList<String> messages;// For displaying
    private ArrayAdapter adapter;

    //Maps
    private Marker marker;
    public static LatLng markerLoc = null;
    public static String markerText;
    public static final int ADD_EDIT_LOCATION_REQUEST_CODE = 455;
    public static final int VIEW_LOCATION_REQUEST_CODE = 456;
    public static final int ADD_EDIT_LOCATION_RESULT_CODE = 457;
    public static final int VIEW_LOCATION_RESULT_CODE = 458;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_notification_section, container, false);
        listView = root.findViewById(R.id.notificationList);
        userID = MainActivity.database.getCurrentUserUID();
        requests = new ArrayList<>();
        messages = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(), R.layout.notification_list_content, R.id.textView, messages);
        listView.setAdapter(adapter);
        getMessageList();
        setListener();
        return root;
    }

    @Override
    public void getMessageList() {
        MainActivity.database
                .getDb()
                .collection("users")
                .document(userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        MainActivity.user = documentSnapshot.toObject(User.class);
                        requests = MainActivity.user.getRequests();
                        for (Request request : requests) {
                            messages.add(request.getMessage());
                        }
                        Collections.reverse(messages);
                        Collections.reverse(requests);
                        adapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void setListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("Would you like to accept or decline this request?")
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent mapsIntent = new Intent(getContext(), MapsActivity.class);
                                mapsIntent.putExtra(MapsActivity.MAP_TYPE, MapsActivity.ADD_EDIT_LOCATION);
                                if (markerLoc != null) {
                                    mapsIntent.putExtra("locationIncluded", true);
                                    mapsIntent.putExtra("markerLoc", markerLoc);
                                    mapsIntent.putExtra("markerText", markerText);
                                } else {
                                    mapsIntent.putExtra("locationIncluded", false);
                                }
                                startActivityForResult(mapsIntent, ADD_EDIT_LOCATION_REQUEST_CODE);
                            }
                        })
                        .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestSenderID = requests.get(position).getSenderID();
                                requestBookID = requests.get(position).getBookID();

                                // delete the request in the book request
                                MainActivity.database
                                        .getDb()
                                        .collection("bookRequest")
                                        .whereEqualTo("requestedBookID", requestBookID)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.getResult().isEmpty()) {




                                                }
                                            }
                                        });


                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }


}
