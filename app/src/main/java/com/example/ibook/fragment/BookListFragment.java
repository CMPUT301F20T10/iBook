package com.example.ibook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.ibook.BookListAdapter;
import com.example.ibook.R;
import com.example.ibook.SectionPageAdapter;
import com.example.ibook.activities.AddBookActivity;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.ViewBookActivity;
import com.example.ibook.entities.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

public class BookListFragment extends Fragment {

    //Private variables
    private Button btn_addBook;
    private FirebaseFirestore db;
    private String userID;
    private String userName;

    private ViewPager viewPager;
    private TabLayout tabLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_booklist, container, false);
        btn_addBook = root.findViewById(R.id.button_add);
        viewPager = root.findViewById(R.id.viewPager);
        tabLayout = root.findViewById(R.id.tabLayout);


        return root;

//        datalist = new ArrayList<>();
//        db = FirebaseFirestore.getInstance();
//        uAuth = FirebaseAuth.getInstance();
//        adapter = new BookListAdapter(datalist, getActivity());
//        bookListView.setAdapter(adapter);
//        adapter.notifyDataSetChanged();


        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String matchID = document.getId();
                                if (matchID.equals(userID)) {
                                    Map<String, Object> convertMap;
                                    ArrayList<Book> hashList = (ArrayList<Book>) document.get("bookList");
                                    for (int i = 0; i < hashList.size(); i += 1) {
                                        convertMap = (Map<String, Object>) hashList.get(i);

                                        datalist.add(new Book(
                                                String.valueOf(convertMap.get("title")),
                                                String.valueOf(convertMap.get("author")),
                                                String.valueOf(convertMap.get("date")),
                                                (String.valueOf(convertMap.get("description"))),
                                                from_string_to_enum(String.valueOf(convertMap.get("status"))),
                                                String.valueOf(convertMap.get("isbn")),
                                                String.valueOf(convertMap.get("owner"))
                                        ));
                                    }
                                    if (datalist == null) {
                                        datalist = new ArrayList<>();
                                    } else {
                                        adapter.notifyDataSetChanged();
                                    }

                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "got an error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


        // set radioButtons for book filter
//        radioGroup = root.findViewById(R.id.selectState);
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton radioButton = group.findViewById(checkedId);
//                if (radioButton.getText().toString().equals("Own")) {
//                    adapter = new BookListAdapter(datalist, getActivity());
//                    bookListView.setAdapter(adapter);
//                } else {
//                    // TODO: deal with other three filters
//                    // empty now for other three
//                    adapter = new BookListAdapter(new ArrayList<Book>(), getActivity());
//                    bookListView.setAdapter(adapter);
//                }
//            }
//        });

        // view book on the list
//        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(getContext(), ViewBookActivity.class);
//                intent.putExtra("USER_ID", userID);
//                intent.putExtra("BOOK_NUMBER", position);
//                startActivityForResult(intent, 0);
//            }
//        });
//
//        // add book button
//        btn_addBook.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getContext(), AddBookActivity.class);
//                intent.putExtra("USER_ID", userID);
//                startActivityForResult(intent, 0);
//            }
//        });


    }

//    @Override // if add/edit/delete books, update changes
//    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
//        // TODO Auto-generated method stub
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (resultCode == 1) { // if data changed, update
//            /*
//            if(data.getExtras().containsKey("PHOTO_CHANGE")){
//                Bitmap new_pic = (Bitmap)data.getExtras().get("PHOTO_CHANGE");
//                //Toast.makeText(getContext(),new_pic.toString(),Toast.LENGTH_SHORT).show();
//            }
//            */
//
//            // update the change
//            // Toast.makeText(getContext(), "updated", Toast.LENGTH_SHORT).show();
//            DocumentReference docRef = db.collection("users").document(userID);
//            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            Map<String, Object> convertMap;
//
//                            ArrayList<Book> hashList = (ArrayList<Book>) document.get("bookList");
//                            datalist = new ArrayList<>();
//                            for (int i = 0; i < hashList.size(); i += 1) {
//                                convertMap = (Map<String, Object>) hashList.get(i);
//
//
//                                datalist.add(new Book(
//                                        String.valueOf(convertMap.get("title")),
//                                        String.valueOf(convertMap.get("author")),
//                                        String.valueOf(convertMap.get("date")),
//                                        (String.valueOf(convertMap.get("description"))),
//                                        from_string_to_enum(String.valueOf(convertMap.get("status"))),
//                                        String.valueOf(convertMap.get("isbn")),
//                                        String.valueOf(convertMap.get("owner"))
//                                ));
//
//                            }
//
//                            if (datalist == null) {
//                                datalist = new ArrayList<>();
//                            } else {
//                                adapter = new BookListAdapter(datalist, getActivity());
//                                bookListView.setAdapter(adapter);
//                            }
//
//                        } else {
//                            Toast.makeText(getContext(), "No such document", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
//                        Toast.makeText(getContext(), "got an error", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
//
//        }
//    }

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

        adapter.addFragment(new OwnBookFragment(), "Own");
        adapter.addFragment(new BorrowBookFragment(), "Borrow");
        adapter.addFragment(new RequestBookFragment(), "Request");
        adapter.addFragment(new AcceptBookFragment(), "Accept");

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
