package com.example.ibook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ibook.R;

public class AcceptBookFragment extends Fragment {
    public AcceptBookFragment() {

    }

    public static AcceptBookFragment getInstance() {
        return new AcceptBookFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_accept_book, container, false);
    }
}
