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
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.ibook.R;
import com.example.ibook.SectionPageAdapter;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.MapsActivity;
import com.example.ibook.entities.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The class for the notification fragment
 */
public class NotificationsFragment extends Fragment {

    //    User currentUser;
//    private FirebaseFirestore db;
//    private ArrayList<String> notificationList;
//    private ListView listView;
//    private ArrayList<String> requestsList;
//    private ArrayList<String> responseList;
//    private RadioGroup radioGroup;
    private ViewPager viewPager;
    private TabLayout tabLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        db = FirebaseFirestore.getInstance();
        View root = inflater.inflate(R.layout.fragment_notification, container, false);
        viewPager = root.findViewById(R.id.viewPager);
        tabLayout = root.findViewById(R.id.tabLayout);
//        listView = root.findViewById(R.id.listView);
//        requestsList = new ArrayList<>();
//        responseList = new ArrayList<>();
//        final DocumentReference docRef = db.collection("users").document(MainActivity.database.getCurrentUserUID());
//
//        final ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.notification_list_content, R.id.textView, requestsList);
//        listView.setAdapter(adapter);
//
//        //get users request list
//        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if (documentSnapshot.exists()) {
//                    //user object intialized
//                    currentUser = documentSnapshot.toObject(User.class);
//                    notificationList = currentUser.getNotificationList();
//                    for (String notif : notificationList) {
//                        requestsList.add(notif);
//
//
//                    }
//                    Collections.reverse(requestsList);
//                    adapter.notifyDataSetChanged();
//
//                }
//            }
//
//        });
//
//
//        //alert dialog on click item
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setMessage("Would you like to accept or decline this request?")
//                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                Intent intent = new Intent(getContext(), MapsActivity.class);
//                                startActivity(intent);
//                            }
//                        })
//                        .setNegativeButton("Decline", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                requestsList.remove(position);
//                                adapter.notifyDataSetChanged();
//                            }
//                        });
//                AlertDialog alert = builder.create();
//                alert.show();
//            }
//        });

        // set toggle buttons
//        radioGroup = root.findViewById(R.id.selectState);
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton radioButton = group.findViewById(checkedId);
//                if (radioButton.getText().toString().equals("Requests")) {
//                    ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.notification_list_content, R.id.textView, requestsList);
//                    listView.setAdapter(adapter);
//                } else {
//                    // TODO: display responses listview
//                    // empty for now
//                    ArrayAdapter adapter = new ArrayAdapter<>(getContext(), R.layout.notification_list_content, R.id.textView, new ArrayList());
//                    listView.setAdapter(adapter);
//                }
//            }
//        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setUpViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter(getChildFragmentManager());

        adapter.addFragment(new RequestNotificationsFragment(), "Request");
        adapter.addFragment(new ResponsesNotificationFragment(), "Response");

        viewPager.setAdapter(adapter);
    }
}
