package com.example.ibook.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ibook.BookListAdapter;
import com.example.ibook.R;
import com.example.ibook.activities.AddBookActivity;
import com.example.ibook.activities.EditBookActivity;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.ViewBookActivity;
import com.example.ibook.entities.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BookListFragment extends Fragment {

    //Private variables
    //private ListView bookListView;
    private RecyclerView bookListView;
    private BookListAdapter adapter;
    private ArrayList<Book> datalist;

    private FirebaseFirestore db;
    private String userID;
    private String userName;
    private FirebaseAuth uAuth;
    private RadioGroup radioGroup;
    private Spinner mSpinner;
    private ArrayAdapter<CharSequence> spinner_adapter;

    private Button sortButton;
    private Button addButton;
    private FloatingActionButton filterButton;
    private FloatingActionButton filter1;
    private FloatingActionButton filter2;
    private FloatingActionButton filter3;
    private FloatingActionButton filter4;
    private String filterStatus = "All";


    private RadioButton ownBookButton;
    private RadioButton borrowBookButton;
    private RadioButton requestBookButton;
    private RadioButton acceptBookButton;

    // set up spinner
    final private String[] all_filter_status = {"All", "Available", "Requested", "Accepted", "Borrowed"};

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_booklist, container, false);
        bookListView = root.findViewById(R.id.bookList);
//        mSpinner = root.findViewById(R.id.spinner);

        addButton = root.findViewById(R.id.add_button);
//        sortButton = root.findViewById(R.id.sortBook);
        filterButton = root.findViewById(R.id.button_filter);
        filter1 = root.findViewById(R.id.fab1);
        filter2 = root.findViewById(R.id.fab2);
        filter3 = root.findViewById(R.id.fab3);
        filter4 = root.findViewById(R.id.fab4);


        ownBookButton = root.findViewById(R.id.ownButton);
        borrowBookButton = root.findViewById(R.id.borrowButton);
        requestBookButton = root.findViewById(R.id.requestButton);
        acceptBookButton = root.findViewById(R.id.acceptButton);

        radioGroup = root.findViewById(R.id.selectState);

        datalist = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        uAuth = FirebaseAuth.getInstance();

        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        bookListView.setLayoutManager(manager);
        bookListView.setHasFixedSize(true);

        adapter = new BookListAdapter(datalist, getActivity());
        bookListView.setAdapter(adapter);

        //default username = "yzhang24@gmail.com";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // try to get the userID
            userID = user.getUid();
            //Toast.makeText(getContext(), userID, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "invalid user", Toast.LENGTH_SHORT).show();
            return root;
        }

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // todo: change email key word to username
                                String matchID = document.getId();
                                //Toast.makeText(getContext(), id, Toast.LENGTH_SHORT).show();
                                if (matchID.equals(userID)) {
                                    //Toast.makeText(getContext(), "match", Toast.LENGTH_SHORT).show();

                                    Map<String, Object> convertMap;
                                    ArrayList<Book> hashList = (ArrayList<Book>) document.get("bookList");

                                    for (int i = 0; i < hashList.size(); i += 1) {
                                        convertMap = (Map<String, Object>) hashList.get(i);

                                        datalist.add(new Book(
                                                String.valueOf(convertMap.get("title")),
                                                String.valueOf(convertMap.get("authors")),
                                                String.valueOf(convertMap.get("date")),
                                                String.valueOf(convertMap.get("description")),
                                                from_string_to_enum(String.valueOf(convertMap.get("status"))),
                                                String.valueOf(convertMap.get("isbn")),
                                                String.valueOf(convertMap.get("owner")),
                                                String.valueOf(convertMap.get("bookID"))
                                        ));
                                        //Toast.makeText(getContext(), String.valueOf(convertMap.get("description")), Toast.LENGTH_SHORT).show();
                                    }
                                    if (datalist == null) {
                                        datalist = new ArrayList<>();
                                    } else {
                                        adapter = new BookListAdapter(datalist, getActivity());
                                        bookListView.setAdapter(adapter);
                                        //Toast.makeText(getContext(), String.valueOf(datalist.size()), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "got an error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        // set radioButtons for book filter

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = group.findViewById(checkedId);
                if (radioButton.getText().toString().equals("Own")) {

                    // when click "own"
                    isFilterVisible(1); // filter visible
                    adapter = new BookListAdapter(datalist, getActivity());
                    bookListView.setAdapter(adapter);
                }
                //if clicks on request booklist toggle button
                else if (radioButton.getText().toString().equals("Request")) {

                    isFilterVisible(0);

                    MainActivity.database.getDb().collection("bookRequest")
                            .whereEqualTo("requestSenderID", userID)
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    String bookID;
                                    //don't need the for loop since username will be unique in our app, so only 1 result with the match.
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        bookID = (String) document.get("requestedBookID");
                                        MainActivity.database.getDb().collection("books").document(bookID)
                                                .get()
                                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                        ArrayList<Book> requestedBookList = new ArrayList<>();
                                                        DocumentSnapshot document = task.getResult();
                                                        Book book = document.toObject(Book.class);
                                                        System.out.println("Object " + book);
                                                        System.out.println("Title " + book.getTitle());
                                                        requestedBookList.add(book);
                                                        adapter = new BookListAdapter(requestedBookList, getActivity());
                                                        bookListView.setAdapter(adapter);
                                                    }// onComplete
                                                });
                                    }//for loop


                                }// outer onComplete
                            });// outer addOnCompleteListener
                    ;
                } else {

                    isFilterVisible(0);
                    adapter = new BookListAdapter(new ArrayList<Book>(), getActivity());
                    bookListView.setAdapter(adapter);
                }//else
            }
        });


        spinner_adapter = new ArrayAdapter<CharSequence>(getContext(), android.R.layout.simple_spinner_item, all_filter_status);
        mSpinner.setAdapter(spinner_adapter);

        // set spinner listener
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(getContext(), "click " + arr[position], Toast.LENGTH_SHORT).show();
                ArrayList<Book> filtered_book = new ArrayList<>();
                if (all_filter_status[position].equals("All status")) {
                    filtered_book = datalist;
                } else {
                    for (Book book : datalist) {
                        if (book.getStatus().toString().equals(all_filter_status[position])) {
                            filtered_book.add(book);
                        }
                    }
                }
                adapter = new BookListAdapter(filtered_book, getActivity());
                bookListView.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // sort books by title
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "sort", Toast.LENGTH_SHORT).show();
                Collections.sort(datalist);
                adapter.notifyDataSetChanged();
            }
        });


        //onItemClick is inside of BookListAdapter now.
//        // view book on the list
//        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getContext(), ViewBookActivity.class);
//                intent.putExtra("BOOK_ID", datalist.get(position).getBookID());
//                intent.putExtra("OWNER", datalist.get(position).getOwner());
//                intent.putExtra("STATUS", datalist.get(position).getStatus().toString());
//                startActivityForResult(intent, 0);
//            }
//        });

        // add book button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), AddBookActivity.class);
                intent.putExtra("USER_ID", userID);
                startActivity(intent);
                //startActivityForResult(intent, 0);
            }
        });

        // for the text color change
        acceptBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptBookButton.setTextColor(Color.WHITE);
                requestBookButton.setTextColor(Color.parseColor("#FF9900"));
                borrowBookButton.setTextColor(Color.parseColor("#FF9900"));
                ownBookButton.setTextColor(Color.parseColor("#FF9900"));
                addButton.setVisibility(View.INVISIBLE);
                sortButton.setVisibility(View.INVISIBLE);
                cameraButton.setVisibility(View.VISIBLE);
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
                sortButton.setVisibility(View.VISIBLE);
                cameraButton.setVisibility(View.INVISIBLE);
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
                sortButton.setVisibility(View.INVISIBLE);
                cameraButton.setVisibility(View.VISIBLE);
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
                sortButton.setVisibility(View.INVISIBLE);
                cameraButton.setVisibility(View.VISIBLE);
            }
        });

        // when click filter button
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "filter clicked", Toast.LENGTH_SHORT).show();
                // todo: hide filter buttons AND change filter color when click
            }
        });

        // status: "Available", "Requested", "Accepted", "Borrowed"
        filter1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "filter Borrowed", Toast.LENGTH_SHORT).show();
                if (filterStatus.equals("Borrowed"))
                    filterStatus = "All";
                else
                    filterStatus = "Borrowed";
                updateBookList();

            }
        });
        filter2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "filter Accepted", Toast.LENGTH_SHORT).show();
                if (filterStatus.equals("Accepted"))
                    filterStatus = "All";
                else
                    filterStatus = "Accepted";
                updateBookList();
            }
        });
        filter3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "filter Requested", Toast.LENGTH_SHORT).show();
                if (filterStatus.equals("Requested"))
                    filterStatus = "All";
                else
                    filterStatus = "Requested";
                updateBookList();
            }
        });
        filter4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "filter Available", Toast.LENGTH_SHORT).show();
                if (filterStatus.equals("Available"))
                    filterStatus = "All";
                else
                    filterStatus = "Available";
                updateBookList();
            }
        });


        return root;
    }

    private void isFilterVisible(int isVisible) {
        if (isVisible == 1) {
            filterButton.setVisibility(View.VISIBLE);
            filter1.setVisibility(View.VISIBLE);
            filter2.setVisibility(View.VISIBLE);
            filter3.setVisibility(View.VISIBLE);
            filter4.setVisibility(View.VISIBLE);
        } else {
            //Toast.makeText(getContext(), "filter hide", Toast.LENGTH_SHORT).show();
            filterButton.setVisibility(View.GONE);
            filter1.setVisibility(View.GONE);
            filter2.setVisibility(View.GONE);
            filter3.setVisibility(View.GONE);
            filter4.setVisibility(View.GONE);
        }
    }

    public void updateBookList() {
        ArrayList<Book> filtered_book = new ArrayList<>();
        if (filterStatus.equals("All")) {
            filtered_book = datalist;
        } else {
            for (Book book : datalist) {
                if (book.getStatus().toString().equals(filterStatus)) {
                    filtered_book.add(book);
                }
            }
        }
        adapter = new BookListAdapter(filtered_book, getActivity());
        bookListView.setAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        // load the owned book list
        datalist.clear();
        db.collection("books")
                .whereEqualTo("owner", userID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Book book = documentSnapshot.toObject(Book.class);
                            datalist.add(book);
                        }
                        adapter = new BookListAdapter(datalist, getActivity());
                        bookListView.setAdapter(adapter);
                        //Toast.makeText(getContext(), String.valueOf(datalist.size()), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // We don't need this anymore, now we will get the data whenever we get back to BookListFragment, see onResume().
    /*
    @Override // if add/edit/delete books, update changes
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 1) { // if data changed, update

            if(data.getExtras()!=null){
                try {
                    //String tempFileName = data.getStringExtra("CHANGED_IMAGE");
                    //FileInputStream is = new FileInputStream(tempFileName);
                    //Bitmap new_pic = BitmapFactory.decodeStream(is);
                    //is.close();
                    //imageView = findViewById(R.id.imageView);
                    //imageView = EditBookActivity.scaleAndSetImage(new_pic, imageView, true);
                    //imageChanged = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    * */


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
