package com.example.ibook;

import android.content.Context;

import com.example.ibook.entities.Book;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
class BookListAdapterUnitTest {

    @Mock
    Context mockContext;


    private BookListAdapter mockBookListAdapter() {
        ArrayList<Book> books = new ArrayList<>();
        books.add(mockBook());
        BookListAdapter bookListAdapter = new BookListAdapter(books, mockContext);
        return bookListAdapter;
    }
    private Book mockBook() { return new Book("Top Girls", "Caryl Churchill",
            "2014", "", Book.Status.Available, "9781408106037",
            "SOMEFANCYSTRING", "SOMEFANCYSTRING1");
    }

    private Book mockBook2() {
        return new Book("The Amazing Absorbing Boy", "Rabindranath Maharaj",
                "2010", "Something", Book.Status.Borrowed, "9780307397287",
                "SOMERANDOMGENERATEDSTRING", "SOMERANDOMGENERATEDSTRING2");
    }

    @Test
    void onCreateViewHolder() {

    }

    @Test
    void onBindViewHolder() {
    }

    @Test
    void getItemId() {
        assertEquals(0, mockBookListAdapter().getItemId(0), "Wrong ID");
    }

    @Test
    void getItemCount() {
        assertEquals(1,mockBookListAdapter().getItemCount());
    }

    @Test
    void testAdd() {
        ArrayList<Book> books = new ArrayList<>();
        books.add(mockBook());
        books.add(mockBook2());
        BookListAdapter bookListAdapter = new BookListAdapter(books, mockContext);
        assertEquals(2,bookListAdapter.getItemCount());
    }




}