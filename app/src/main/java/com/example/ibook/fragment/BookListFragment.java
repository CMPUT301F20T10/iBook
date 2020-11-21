package com.example.ibook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.ibook.R;
import com.example.ibook.SectionPageAdapter;
import com.example.ibook.activities.AddBookActivity;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.ViewBookActivity;
import com.example.ibook.entities.Book;
import com.example.ibook.entities.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

public class BookListFragment extends Fragment {

    //Private variables
    private Button btn_addBook;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private BookListSectionFragment ownSection;
    private BookListSectionFragment borrowSection;
    private BookListSectionFragment acceptSection;
    private BookListSectionFragment requestSection;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_booklist, container, false);
        btn_addBook = root.findViewById(R.id.button_add);
        viewPager = root.findViewById(R.id.viewPager);
        tabLayout = root.findViewById(R.id.tabLayout);

        ownSection = new BookListSectionFragment("Own");
        borrowSection = new BookListSectionFragment("Borrow");
        acceptSection = new BookListSectionFragment("Accept");
        requestSection = new BookListSectionFragment("Request");

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

        // add book button
        btn_addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddBookActivity.class);
                startActivity(intent);
            }
        });
        return root;
    }

    private void setUpViewPager(ViewPager viewPager) {
        SectionPageAdapter adapter = new SectionPageAdapter(getChildFragmentManager());

        adapter.addFragment(ownSection, "Own");
        adapter.addFragment(borrowSection, "Borrow");
        adapter.addFragment(requestSection, "Request");
        adapter.addFragment(acceptSection, "Accept");
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (ownSection.running()) {
            SystemClock.sleep(500);
            ownSection.getBookList();
        }

    }

}
