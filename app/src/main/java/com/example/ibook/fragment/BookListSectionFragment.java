package com.example.ibook.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ibook.BookListAdapter;
import com.example.ibook.R;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.ViewBookActivity;
import com.example.ibook.entities.Book;
import com.example.ibook.entities.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.logging.Logger;

public class BookListSectionFragment extends Fragment {

    private ArrayList<Book> bookList;
    private ListView listView;
    private BookListAdapter adapter;
    private String title;
//    private String userID;

    public BookListSectionFragment(String title) {
        this.title = title;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_booklist_section, container, false);
        listView = root.findViewById(R.id.bookList);
        bookList = new ArrayList<>();
        adapter = new BookListAdapter(bookList, getContext());
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        getBookList();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), ViewBookActivity.class);
                intent.putExtra("USER_ID", MainActivity.database.getCurrentUserUID());
                intent.putExtra("BOOK_NUMBER", position);
                startActivityForResult(intent, 0);
            }
        });
        return root;
    }

    public void getBookList() {
        if (title.equals("Own")) {
            getOwnBookList();
        }
        if (title.equals("Borrow")) {
            getBorrowBookList();
        }
        if (title.equals("Accept")) {
            getAcceptBookList();
        }
        if (title.equals("Request")) {
            getRequestBookList();
        }
        adapter.notifyDataSetChanged();
        adapter = new BookListAdapter(bookList, getContext());
        listView.setAdapter(adapter);
    }

    private void getOwnBookList() {
        String userID = MainActivity.database.getCurrentUserUID();
        DocumentReference docRf =
                MainActivity.database.getDb().collection("users").document(userID);
        docRf.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                bookList = user.getBookList();
                if (bookList == null) {
                    bookList = new ArrayList<>();
                }
                adapter = new BookListAdapter(bookList, getContext());
                listView.setAdapter(adapter);
            }
        });
        docRf.get().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "got an error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getRequestBookList() {
        String userID = MainActivity.database.getCurrentUserUID();
        ArrayList<String> bookIDList = new ArrayList<>();
        MainActivity.database
                .getDb()
                .collection("bookRequest")
                .whereEqualTo("requestSenderID", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                bookIDList.add((String) document.get("requestedBookID"));
                            }
                        } else {
                            Toast.makeText(getContext(), "got an error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        bookList.clear();
        for (String ID : bookIDList) {
            MainActivity.database
                    .getDb()
                    .collection("books")
                    .document(ID)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            bookList.add(documentSnapshot.toObject(Book.class));
                        }
                    });
        }
    }

    private void getBorrowBookList() {
        //TODO:implement
    }

    private void getAcceptBookList() {
        //TODO:implement
    }

    public boolean isRunning() {
        if (adapter == null || listView == null) {
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        getBookList();
        adapter.notifyDataSetChanged();
        adapter = new BookListAdapter(bookList, getContext());
        listView.setAdapter(adapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("", "section result");
        Log.d("", resultCode + "");
    }
}