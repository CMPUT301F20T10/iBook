package com.example.ibook.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.ibook.Book;
import com.example.ibook.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class EditBookFragment extends DialogFragment {

    private EditText bookNameEditText;
    private EditText authorEditText;
    private EditText dateEditText;
    private EditText isbnEditText;
    private EditText descriptionEditText;
    private EditText photoEditText;
    private FirebaseFirestore db;
    private String userID;
    private final int bookNumber;

    public EditBookFragment(String userID, int bookNumber) {
        this.userID = userID;
        this.bookNumber = bookNumber;
    }

    private OnFragmentInteractionListener listener;

    // Fragment parts are adapted from lab
    public interface OnFragmentInteractionListener {
        void onOkPressed(boolean isChanged, Book book);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_edit_book, null);

        bookNameEditText = view.findViewById(R.id.editTextBookName);
        authorEditText = view.findViewById(R.id.editTextAuthor);
        dateEditText = view.findViewById(R.id.editTextDate);
        isbnEditText = view.findViewById(R.id.editTextISBN);
        descriptionEditText = view.findViewById(R.id.editTextDescription);
        photoEditText = view.findViewById(R.id.editTextPhoto);
        db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection("users").document(userID);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {

                        ArrayList<Book> hashList = (ArrayList<Book>) document.get("BookList");
                        Map<String, Object> convertMap = (Map<String, Object>) hashList.get(bookNumber);
                        Book book = new Book(
                                String.valueOf(convertMap.get("title")),
                                String.valueOf(convertMap.get("author")),
                                String.valueOf(convertMap.get("date")),
                                String.valueOf(convertMap.get("description")),
                                //from_string_to_enum(String.valueOf(convertMap.get("status"))),
                                Book.Status.Available,
                                String.valueOf(convertMap.get("isbn"))
                        );

                        bookNameEditText.setText(book.getTitle());
                        authorEditText.setText(book.getAuthor());
                        dateEditText.setText(book.getDate());
                        isbnEditText.setText(book.getIsbn());
                        // descriptionEditText.setText(book.getDescription());

                        // photoEditText todo: photo format path

                    } else {
                        //Log.d(TAG, "No such document");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder
                .setView(view)
                .setTitle("Edit Book")
                .setNegativeButton("Cancel", null)
                .setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                            }
                        });

        /*
        prevent Fragment going back when info is improper
        citation:
            from: Braiam
            date: edited Jan 22 '18 at 15:43
            URL:https://stackoverflow.com/questions/21192386/android-fragment-onclick-button-method/21192511
        */
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Get the inputs and check if it's valid
                    the fragment will hold if invalid and denote the user what's improper as well
                */
                Boolean wantToCloseDialog = false;

                final String bookName = bookNameEditText.getText().toString();
                final String authorName = authorEditText.getText().toString();
                final String date = dateEditText.getText().toString();
                final String isbn = isbnEditText.getText().toString();
                final String description = descriptionEditText.getText().toString();

                if (bookName.length() > 0
                        && authorName.length() > 0
                        && date.length() > 0
                        && isbn.length() > 0) {

                    DocumentReference docRef = db.collection("users").document(userID);
                    docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Map<String, Object> data = new HashMap();
                                    data = document.getData();
                                    ArrayList<Book> books = (ArrayList<Book>) document.getData().get("BookList");
                                    books.set(bookNumber, new Book(bookName, authorName, date, description, Book.Status.Available, isbn));
                                    data.put("BookList", books);
                                    db.collection("users")
                                            .document(userID).set(data);
                                } else {
                                    Toast.makeText(getContext(), "No such document", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "got an error", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    listener.onOkPressed(true, new Book(bookName, authorName, date, description, Book.Status.Available, isbn));
                    wantToCloseDialog = true;
                } else {
                    Toast.makeText(getContext(), "Please input full information", Toast.LENGTH_SHORT).show();
                }


                // if the book values are inproper, hold on the fragment
                if (wantToCloseDialog)
                    dialog.dismiss();

            }
        });
        return dialog;


    }

}
