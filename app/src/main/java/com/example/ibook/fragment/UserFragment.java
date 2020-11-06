package com.example.ibook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.ibook.R;
import com.example.ibook.activities.ResetPasswordActivity;

import com.example.ibook.activities.EditProfile;
import com.example.ibook.activities.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Activity for the current user profile
 * Displays username, phone-number, email-id
 * Displays image(will add the functionalities in future)
 */
public class UserFragment extends Fragment {
    private TextView email;
    private TextView username;
    private TextView phoneNumber;
    private Button resetPasswordButton;
    private Button logOutButton;

    private FirebaseAuth uAuth; // to get user id
    private FirebaseFirestore db; // to get database/ retireval
    private String userId;

    private View root;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         root = inflater.inflate(R.layout.fragment_user, container, false);

        displayUserInfo(root);

        resetPasswordButton = root.findViewById(R.id.resetPasswordButton);
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Go to reset password activity
                Intent intent = new Intent(getActivity(), ResetPasswordActivity.class);
                startActivity(intent);

            }
        });

        logOutButton = root.findViewById(R.id.logOutbutton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                logout(root);
            }
        });


        final TextView editButton = root.findViewById(R.id.editButton);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editButton();
            }//onClick
        });//setOnClickListener

        return root;
    }//onCreateView

    @Override
    public void onResume() {
        super.onResume();
        displayUserInfo(root);
    }// onResume

    /**
     * onclick of the editButton,this method gets triggered which lets the user go to another
     * activity where they can update/edit their contact information
     */
    public void editButton(){
        Intent intent = new Intent(getActivity(), EditProfile.class);
        intent.putExtra("username", username.getText().toString());
        intent.putExtra("email",email.getText().toString());
        intent.putExtra("phone",phoneNumber.getText().toString());
        startActivity(intent);
    }//editButton

    /**
     * This method is invoked when the user edits information and returns to the profile activity
     * updates the user profile activity whenever the user edits their information
     * @param root
     */
    public void displayUserInfo(View root){
        email = root.findViewById(R.id.emailID);
        username = root.findViewById(R.id.username);
        phoneNumber = root.findViewById(R.id.phoneNumber);
        uAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userId = uAuth.getCurrentUser().getUid();

        DocumentReference documentReference = db.collection("users").document(userId);
        documentReference.addSnapshotListener(getActivity(), new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error){
                phoneNumber.setText(value.getString("phoneNumber"));
                username.setText(value.getString("userName"));
                email.setText(value.getString("email"));

            }
        });
    }//displayUserInfo

    /**
     * Method invoked when logout button is clicked
     * Logs the current user out of the app
     * @param root
     */
    public void logout(View root){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(), MainActivity.class));
    }// logout

}// class
