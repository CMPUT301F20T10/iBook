package com.example.ibook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

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
    private FirebaseFirestore db;
    private String userID;
    private String userName;

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private BookListSectionFragment[] bookListSectionFragments = new BookListSectionFragment[4];

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_booklist, container, false);
        btn_addBook = root.findViewById(R.id.button_add);
        viewPager = root.findViewById(R.id.viewPager);
        tabLayout = root.findViewById(R.id.tabLayout);

        bookListSectionFragments[0] = new BookListSectionFragment("Own");
        bookListSectionFragments[1] = new BookListSectionFragment("Borrow");
        bookListSectionFragments[2] = new BookListSectionFragment("Accept");
        bookListSectionFragments[3] = new BookListSectionFragment("Request");

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

        adapter.addFragment(bookListSectionFragments[0], "Own");
        adapter.addFragment(bookListSectionFragments[1], "Borrow");
        adapter.addFragment(bookListSectionFragments[2], "Request");
        adapter.addFragment(bookListSectionFragments[3], "Accept");
        viewPager.setAdapter(adapter);
    }


    public Book.Status from_string_to_enum(String input) {
        if (input.equals("Available"))
            return Book.Status.Available;

        if (input.equals("Available"))
            return Book.Status.Available;

        if (input.equals("Available"))
            return Book.Status.Available;

        if (input.equals("Available"))
            return Book.Status.Available;
        // todo: change later
        return Book.Status.Available;
    }
}
