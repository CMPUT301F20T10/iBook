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

import com.example.ibook.activities.AddMyBookActivity;
import com.example.ibook.entities.Book;
import com.example.ibook.BookListAdapter;
import com.example.ibook.R;
import com.example.ibook.entities.User;
import com.example.ibook.activities.ViewBookActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class BookListFragment extends Fragment {

    //Private variables
    private ListView bookListView;
    private BookListAdapter adapter;
    private ArrayList<Book> datalist;
    private Button btn_addBook;
    private String username;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_booklist, container, false);
        // Set up the view
        bookListView = root.findViewById(R.id.bookList);
        btn_addBook = root.findViewById(R.id.button_add);
        datalist = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        // TODO: transfer into the database
        adapter = new BookListAdapter(datalist, getActivity());
        bookListView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


        username = "yzhang24@gmail.com";
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // todo: change email key word to username
                                if(document.getData().get("email").equals(username)){
                                    Map<String, Object> map = document.getData();


                                    Map<String,Object> convertMap = new HashMap();
                                    //ArrayList<Book> convertMap = (ArrayList<Book>) map.get("BookList");
                                    ArrayList<Book> hashList = (ArrayList<Book>) document.get("BookList");


                                    for(int i=0;i<hashList.size();i+=1){
                                        convertMap = (Map<String,Object>) hashList.get(i);

                                        datalist.add(new Book(
                                                String.valueOf(convertMap.get("title")),
                                                String.valueOf(convertMap.get("author")),
                                                String.valueOf(convertMap.get("date")),
                                                (String.valueOf(convertMap.get("description")))+"fweoihfohwfjoiwojifojwojfjowjfjskdlfljksdjlfksldjkfsljfwefwefwefwfwefwefwefwef", // make it long
                                                from_string_to_enum(String.valueOf(convertMap.get("status"))),
                                                String.valueOf(convertMap.get("isbn"))
                                        ));
                                    }
                                    if(datalist==null){
                                        datalist = new ArrayList<>();
                                    }
                                    else{
                                        adapter.notifyDataSetChanged();
                                    }
                                }

                            }
                        } else {
                            Toast.makeText(getContext(), "got an error", Toast.LENGTH_SHORT).show();

                        }
                    }
                });



        // view book on the list
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                User user = new User("ztan4", "a password", "something@things.ca", "123456");
                Intent intent = new Intent(getContext(), ViewBookActivity.class);
                Book book = (Book) parent.getItemAtPosition(position);
                intent.putExtra("BOOK", (Book) parent.getItemAtPosition(position));
                intent.putExtra("USER", user);
                startActivity(intent);
            }
        });

        // add book button
        btn_addBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = new User("ivan", "123456", "ivan@gmail.com", "123456");
                Intent intent = new Intent(getContext(), AddMyBookActivity.class);
                //intent.putExtra("USER", user);
                intent.putExtra("curr_username", "1@gmail.com");
                // todo: I don't know how can this fragment have access to username from PageActivity
                startActivity(intent);
            }
        });


        return root;
    }
    public Book.Status from_string_to_enum(String input){
        if(input.equals("Available"))
            return Book.Status.Available;

        if(input.equals("Available"))
            return Book.Status.Available;

        if(input.equals("Available"))
            return Book.Status.Available;

        if(input.equals("Available"))
            return Book.Status.Available;
        // todo: change later
        return Book.Status.Available;
    }
}
