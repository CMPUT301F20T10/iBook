package com.example.ibook.fragment;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ibook.R;

public class OwnBookFragment extends Fragment {


    public OwnBookFragment() {
    }

    public static OwnBookFragment getInstance() {
        return new OwnBookFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_own_book, container, false);
    }
}