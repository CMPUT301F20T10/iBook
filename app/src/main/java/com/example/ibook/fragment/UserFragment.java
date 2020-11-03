package com.example.ibook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ibook.R;
import com.example.ibook.entities.User;

public class UserFragment extends Fragment {
    private TextView tv_userName;
    private TextView tv_email;
    private TextView tv_phoneNumber;

    private User user;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user, container, false);

        tv_userName = root.findViewById(R.id.tv_userName);
        tv_email = root.findViewById(R.id.tv_email);
        tv_phoneNumber = root.findViewById(R.id.tv_phoneNumber);

        user = new User();

        tv_userName.setText(user.getUserName());
        tv_email.setText(user.getEmail());
        tv_phoneNumber.setText(user.getPhoneNumber());

        return root;
    }
}
