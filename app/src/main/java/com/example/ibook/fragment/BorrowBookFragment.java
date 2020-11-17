package com.example.ibook.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ibook.R;

public class BorrowBookFragment extends Fragment {
    public BorrowBookFragment() {
    }

    public static BorrowBookFragment getInstance() {
        return new BorrowBookFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_borrow_book, container, false);
    }
}
