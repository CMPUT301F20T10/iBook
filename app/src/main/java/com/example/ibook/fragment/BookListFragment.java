package com.example.ibook.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ibook.BookListAdapter;
import com.example.ibook.R;
import com.example.ibook.activities.AddBookActivity;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.entities.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BookListFragment extends Fragment {

    //Private variables
    private RecyclerView bookListView;
    private BookListAdapter adapter;
    private ArrayList<Book> datalist;

    private RadioGroup radioGroup;

    private FloatingActionButton addButton;
    private FloatingActionButton menuButton;
    private FloatingActionButton filterAllButton;
    private FloatingActionButton filterBorrowButton;
    private FloatingActionButton filterRequestButton;
    private FloatingActionButton filterAcceptButton;
    private FloatingActionButton filterAvailableButton;

    private String filterStatus = "All";

    private RadioButton ownBookButton;
    private RadioButton borrowBookButton;
    private RadioButton requestBookButton;
    private RadioButton acceptBookButton;
    private boolean isMenuOpen = false;

    private TextView filterAllText;
    private TextView filterBorrowText;
    private TextView filterAcceptText;
    private TextView filterRequestText;
    private TextView filterAvailableText;
    private TextView stateText;

    private int isOnToggle = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_booklist, container, false);
        bookListView = root.findViewById(R.id.bookList);

        addButton = root.findViewById(R.id.add_button);
        menuButton = root.findViewById(R.id.menu_button);
        filterAllButton = root.findViewById(R.id.filter_all_button);
        filterBorrowButton = root.findViewById(R.id.filter_borrowed_button);
        filterRequestButton = root.findViewById(R.id.filter_request_button);
        filterAcceptButton = root.findViewById(R.id.filter_accepted_button);
        filterAvailableButton = root.findViewById(R.id.filter_available_button);

        ownBookButton = root.findViewById(R.id.ownButton);
        borrowBookButton = root.findViewById(R.id.borrowButton);
        requestBookButton = root.findViewById(R.id.requestButton);
        acceptBookButton = root.findViewById(R.id.acceptButton);

        radioGroup = root.findViewById(R.id.selectState);

        filterAcceptText = root.findViewById(R.id.acceptedTextView);
        filterAllText = root.findViewById(R.id.allTextView);
        filterBorrowText = root.findViewById(R.id.borrowTextView);
        filterAvailableText = root.findViewById(R.id.availableTextView);
        filterRequestText = root.findViewById(R.id.requestTextView);
        stateText = root.findViewById(R.id.stateTextView);

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        bookListView.setLayoutManager(manager);
        bookListView.setHasFixedSize(true);

        datalist = new ArrayList<>();
        adapter = new BookListAdapter(datalist, getActivity());
        bookListView.setAdapter(adapter);
        
        setUpListener();
        isButtonVisible(true, false);

        // add book button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddBookActivity.class);
                intent.putExtra("USER_ID", MainActivity.user.getUserID());
                startActivity(intent);
            }
        });


        // when click filter button
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isMenuOpen) {
                    showMenu();
                } else {
                    closeMenu();
                }
            }
        });
        return root;
    }

    private void closeMenu() {
        isMenuOpen = false;
        isButtonVisible(true, false);
    }

    private void showMenu() {
        isMenuOpen = true;
        isButtonVisible(true, true);
    }

    private void getBorrowBookList() {
        datalist.clear();
        adapter.notifyDataSetChanged();
        String userID = MainActivity.user.getUserID();
        MainActivity.database
                .getDb()
                .collection("bookRequest")
                .whereEqualTo("requestSenderID", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            // only for ignoring old data
                            if(!documentSnapshot.contains("requestStatus"))
                                continue;
                            String bookStatus = (String) documentSnapshot.get("requestStatus");

                            if(!bookStatus.equals("Borrowed") && !bookStatus.equals("Returning")){
                                continue;
                            }
                            String bookID = (String) documentSnapshot.get("requestedBookID");
                            MainActivity.database
                                    .getDb()
                                    .collection("books")
                                    .document(bookID)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            datalist.add(documentSnapshot.toObject(Book.class));
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                });
    }

    private void getAcceptedBookList() {
        //TODO:implement
        datalist.clear();
        adapter.notifyDataSetChanged();
        String userID = MainActivity.user.getUserID();
        MainActivity.database
                .getDb()
                .collection("bookRequest")
                .whereEqualTo("requestSenderID", userID)
                .whereEqualTo("requestStatus", "Accepted")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            // only for ignoring old data
                            if(!documentSnapshot.contains("requestStatus"))
                                continue;

                            String bookID = (String) documentSnapshot.get("requestedBookID");
                            Toast.makeText(getContext(),bookID,Toast.LENGTH_SHORT).show();
                            MainActivity.database
                                    .getDb()
                                    .collection("books")
                                    .document(bookID)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            datalist.add(documentSnapshot.toObject(Book.class));
                                            adapter.notifyDataSetChanged();

                                        }
                                    });
                        }
                    }
                });
    }

    private void getRequestBookList() {
        datalist.clear();
        adapter.notifyDataSetChanged();
        String userID = MainActivity.user.getUserID();
        MainActivity.database
                .getDb()
                .collection("bookRequest")
                .whereEqualTo("requestSenderID", userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                            // only for ignoring old data
                            if(!documentSnapshot.contains("requestStatus"))
                                continue;
                            if(documentSnapshot.contains("requestStatus")){
                                // if the status is not requested, ignore it
                                if(!((String) documentSnapshot.get("requestStatus")).equals("Requested")){
                                    continue;
                                }
                            }
                            String bookID = (String) documentSnapshot.get("requestedBookID");
                            MainActivity.database
                                    .getDb()
                                    .collection("books")
                                    .document(bookID)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            datalist.add(documentSnapshot.toObject(Book.class));
                                            adapter.notifyDataSetChanged();
                                        }
                                    });
                        }
                    }
                });
    }

    private void getOwnBookList() {
        datalist.clear();
        MainActivity.database.getDb().collection("books")
                .whereEqualTo("owner", MainActivity.database.getCurrentUserUID())
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                            Book book = documentSnapshot.toObject(Book.class);
                            datalist.add(book);
                        }
                        Log.d("", datalist.size() + "");
                        adapter = new BookListAdapter(datalist, getContext());
                        bookListView.setAdapter(adapter);
                    }
                });
    }

    private void isButtonVisible(boolean isVisible, boolean isFilterVisible) {
        if (isVisible) {
            addButton.setVisibility(View.VISIBLE);
            menuButton.setVisibility(View.VISIBLE);

        } else {
            addButton.setVisibility(View.GONE);
            menuButton.setVisibility(View.GONE);
        }
        if (isFilterVisible) {
            filterAllButton.setVisibility(View.VISIBLE);
            filterBorrowButton.setVisibility(View.VISIBLE);
            filterRequestButton.setVisibility(View.VISIBLE);
            filterAcceptButton.setVisibility(View.VISIBLE);
            filterAvailableButton.setVisibility(View.VISIBLE);

            filterAcceptText.setVisibility(View.VISIBLE);
            filterAllText.setVisibility(View.VISIBLE);
            filterBorrowText.setVisibility(View.VISIBLE);
            filterAvailableText.setVisibility(View.VISIBLE);
            filterRequestText.setVisibility(View.VISIBLE);
        } else {
            filterAllButton.setVisibility(View.GONE);
            filterBorrowButton.setVisibility(View.GONE);
            filterRequestButton.setVisibility(View.GONE);
            filterAcceptButton.setVisibility(View.GONE);
            filterAvailableButton.setVisibility(View.GONE);

            filterAcceptText.setVisibility(View.GONE);
            filterAllText.setVisibility(View.GONE);
            filterBorrowText.setVisibility(View.GONE);
            filterAvailableText.setVisibility(View.GONE);
            filterRequestText.setVisibility(View.GONE);
        }
    }

    public void updateBookList() {
        ArrayList<Book> filtered_book = new ArrayList<>();
        if (filterStatus.equals("All")) {
            stateText.setText("");
            filtered_book = datalist;
        } else {
            stateText.setText(filterStatus);
            for (Book book : datalist) {
                if (book.getStatus().toString().equals(filterStatus)) {
                    filtered_book.add(book);
                }
            }

        }
        adapter = new BookListAdapter(filtered_book, getActivity());
        bookListView.setAdapter(adapter);
    }

    private void setUpListener() {
        // for the text color change
        acceptBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptBookButton.setTextColor(Color.WHITE);
                requestBookButton.setTextColor(Color.parseColor("#FF9900"));
                borrowBookButton.setTextColor(Color.parseColor("#FF9900"));
                ownBookButton.setTextColor(Color.parseColor("#FF9900"));
                addButton.setVisibility(View.INVISIBLE);
                menuButton.setVisibility(View.INVISIBLE);
                isOnToggle = 3;

                isButtonVisible(false, false);
                stateText.setText("");
                checkToggle();

            }
        });

        ownBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ownBookButton.setTextColor(Color.WHITE);
                requestBookButton.setTextColor(Color.parseColor("#FF9900"));
                borrowBookButton.setTextColor(Color.parseColor("#FF9900"));
                acceptBookButton.setTextColor(Color.parseColor("#FF9900"));
                addButton.setVisibility(View.VISIBLE);
                menuButton.setVisibility(View.VISIBLE);
                isOnToggle = 0;

                isButtonVisible(true, false);
                getOwnBookList();
                stateText.setText("");
            }
        });

        requestBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestBookButton.setTextColor(Color.WHITE);
                ownBookButton.setTextColor(Color.parseColor("#FF9900"));
                borrowBookButton.setTextColor(Color.parseColor("#FF9900"));
                acceptBookButton.setTextColor(Color.parseColor("#FF9900"));
                addButton.setVisibility(View.INVISIBLE);
                menuButton.setVisibility(View.INVISIBLE);
                isOnToggle = 2;

                isButtonVisible(false, false);
                getRequestBookList();
                stateText.setText("");
            }
        });

        borrowBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                borrowBookButton.setTextColor(Color.WHITE);
                ownBookButton.setTextColor(Color.parseColor("#FF9900"));
                requestBookButton.setTextColor(Color.parseColor("#FF9900"));
                acceptBookButton.setTextColor(Color.parseColor("#FF9900"));
                addButton.setVisibility(View.INVISIBLE);
                menuButton.setVisibility(View.INVISIBLE);
                isOnToggle = 1;

                isButtonVisible(false, false);
                getBorrowBookList();
                stateText.setText("");
            }
        });

        // The listener of the filter button
        filterAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Filter All", Toast.LENGTH_SHORT).show();
                filterStatus = "All";
                closeMenu();
                updateBookList();
            }
        });

        filterBorrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "filter Borrowed", Toast.LENGTH_SHORT).show();
                filterStatus = "Borrowed";
                closeMenu();
                updateBookList();
            }
        });

        filterRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "filter Requested", Toast.LENGTH_SHORT).show();
                filterStatus = "Requested";
                closeMenu();
                updateBookList();
            }
        });

        filterAcceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "filter Accepted", Toast.LENGTH_SHORT).show();
                filterStatus = "Accepted";
                closeMenu();
                updateBookList();
            }
        });

        filterAvailableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Filter Available", Toast.LENGTH_SHORT).show();
                filterStatus = "Available";
                closeMenu();
                updateBookList();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // load booklist
        checkToggle();
    }// onResume

    private void checkToggle(){
        if(isOnToggle == 0){
            getOwnBookList();
        }
        if(isOnToggle == 1){
            getBorrowBookList();
        }
        if(isOnToggle == 2){
            getRequestBookList();
        }
        if(isOnToggle == 3){
            getAcceptedBookList();
        }
    }// checkToggle
}
