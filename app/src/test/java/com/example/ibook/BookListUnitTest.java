package com.example.ibook;

import com.example.ibook.entities.Book;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class BookListUnitTest {


    private Book mockBook() {
        return new Book("Top Girls", "Caryl Churchill",
            "2014", "", Book.Status.Available, "9781408106037",
            "SOMEFANCYSTRING", "SOMEFANCYSTRING1");
    }
    private Book mockBook2() {
        return new Book("The Amazing Absorbing Boy", "Rabindranath Maharaj",
                "2010", "Something", Book.Status.Borrowed, "9780307397287",
                "SOMERANDOMGENERATEDSTRING", "SOMERANDOMGENERATEDSTRING2");
    }

    @Test
    void testAdd() {
        ArrayList<Book> books = new ArrayList<Book>();
        assertEquals(0,books.size());
        books.add(mockBook());
        assertEquals(1,books.size());
        books.add(mockBook2());
        assertEquals(2, books.size());
    }

    @Test
    void testGetCities() {
        ArrayList<Book> books = new ArrayList<Book>();
        books.add(mockBook());
        assertEquals(0, mockBook().compareTo(books.get(0)));
        books.add(mockBook2());
        assertEquals(0, mockBook2().compareTo(books.get(1)));
    }

    @Test
    void deleteCities() {
        ArrayList<Book> books = new ArrayList<Book>();
        assertEquals(0,books.size());
        books.add(mockBook());
        assertEquals(1,books.size());
        books.add(mockBook2());
        books.remove(mockBook2());
        assertEquals(false, books.contains(mockBook2()));
        books.remove(mockBook());
        assertEquals(false, books.contains(mockBook()));
    }



}
