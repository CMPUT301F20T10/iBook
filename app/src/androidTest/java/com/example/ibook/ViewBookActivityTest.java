package com.example.ibook;

import android.app.Activity;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.ViewBookActivity;
import com.example.ibook.entities.Book;
import com.robotium.solo.Solo;
import static junit.framework.TestCase.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.lang.ref.SoftReference;


public class ViewBookActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);

    /*
    The user we use to test the MainActivity is:
    4f5g5E162fh4bBnptMFlSS19GJp2
    email : "ztan7@gmail.com"
    password: "123456"
    phoneNumber: "123456"
    userName: "ztan7"
    book:
    author: "ztan7"
    available: true
    date: "12-23"
    description: null
    isbn: "asd"
    status: "Available"
    title: "ztan7_test"
    */
    @Before
    public void SetUp() throws Exception {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "ztan7@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "123456");
        solo.clickOnButton("Login In");
    }

    @After
    public void finish() {
    }

    @Test
    public void start() throws Exception{
        Activity activity = rule.getActivity();
    }

    @Test
    public void testHomePageViewBook() {
        //String expectedTitle = ((TextView) solo.getView(R.id.listBookTitle)).getText().toString();


        ListView bookList = (ListView) solo.getView((R.id.bookList));
        // delay 1 second t avoid failure caused by the network lag
        SystemClock.sleep(1000);
        Book bookClicked = (Book) bookList.getItemAtPosition(0);

        solo.clickInList(1);
        // delay 1 second t avoid failure caused by the network lag
        SystemClock.sleep(1000);
        solo.assertCurrentActivity("Wrong Activity", ViewBookActivity.class);

        String actualTitle = ((TextView) solo.getView(R.id.ViewBookName)).getText().toString();
        String actualAuthor = ((TextView) solo.getView(R.id.ViewAuthor)).getText().toString();
        String actualDate =  ((TextView) solo.getView(R.id.ViewDate)).getText().toString();
        String actualISBN = ((TextView) solo.getView(R.id.ViewISBN)).getText().toString();

        // Test whether the data in the view page is consistent with the data
        // in the corresponding position of the list.
        assertEquals(bookClicked.getTitle(), actualTitle);
        assertEquals(bookClicked.getAuthor(), actualAuthor);
        assertEquals(bookClicked.getDate(), actualDate);
        assertEquals(bookClicked.getIsbn(), actualISBN);

        assertEquals(View.GONE, solo.getView(R.id.editButton).getVisibility());
        assertEquals(View.GONE, solo.getView(R.id.btn_delete_book).getVisibility());
    }

    @Test
    public void testBookListViewBook() {
        solo.clickOnView(solo.getView(R.id.navigation_booklist));

        String expectedTitle = "ztan7_test";
        String expectedAuthor = "ztan7";
        String expectedDate = "12-23";
        String expectedISBN = "asd";

        solo.clickInList(1);

        String actualTitle = ((TextView) solo.getView(R.id.ViewBookName)).getText().toString();
        String actualAuthor = ((TextView) solo.getView(R.id.ViewAuthor)).getText().toString();
        String actualDate =  ((TextView) solo.getView(R.id.ViewDate)).getText().toString();
        String actualISBN = ((TextView) solo.getView(R.id.ViewISBN)).getText().toString();

        // Test whether the data in the view page is consistent with the data
        // in the corresponding position of the list.
        assertEquals(expectedTitle, actualTitle);
        assertEquals(expectedAuthor, actualAuthor);
        assertEquals(expectedDate, actualDate);
        assertEquals(expectedISBN, actualISBN);
        assertEquals(View.GONE, solo.getView(R.id.btn_request_book).getVisibility());

    }

    @Test
    public void testRequestBook() {
        //TODO: finish this one once the request functionality is finished.
    }

    @Test
    public void testEditBook() {
        solo.clickOnView(solo.getView(R.id.navigation_booklist));
        solo.clickInList(1);

        // Edit Book Name/Title
        assertEquals("ztan7_test", ((TextView) solo.getView(R.id.ViewBookName)).getText().toString());
        solo.clickOnView(solo.getView(R.id.editButton));
        solo.enterText((EditText) solo.getView(R.id.titleEditor), "_new");
        solo.clickOnButton("Complete");
        // delay 1 second t avoid failure caused by the network lag
        SystemClock.sleep(1000);
        assertEquals("ztan7_test_new", ((TextView) solo.getView(R.id.ViewBookName)).getText().toString());

        // Edit Author
        assertEquals("ztan7", ((TextView) solo.getView(R.id.ViewAuthor)).getText().toString());
        solo.clickOnView(solo.getView(R.id.editButton));
        solo.enterText((EditText) solo.getView(R.id.authorEditor), "_new");
        solo.clickOnButton("Complete");
        // delay 1 second t avoid failure caused by the network lag
        SystemClock.sleep(1000);
        assertEquals("ztan7_new", ((TextView) solo.getView(R.id.ViewAuthor)).getText().toString());

        // Edit Date
        assertEquals("12-23", ((TextView) solo.getView(R.id.ViewDate)).getText().toString());
        solo.clickOnView(solo.getView(R.id.editButton));
        solo.enterText((EditText) solo.getView(R.id.dateEditor), ", 20:23");
        solo.clickOnButton("Complete");
        // delay 1 second t avoid failure caused by the network lag
        SystemClock.sleep(1000);
        assertEquals("12-23, 20:23", ((TextView) solo.getView(R.id.ViewDate)).getText().toString());

        // Edit Date
        assertEquals("asd", ((TextView) solo.getView(R.id.ViewISBN)).getText().toString());
        solo.clickOnView(solo.getView(R.id.editButton));
        solo.enterText((EditText) solo.getView(R.id.isbnEditor), "fghjkl");
        solo.clickOnButton("Complete");
        // delay 1 second t avoid failure caused by the network lag
        SystemClock.sleep(1000);
        assertEquals("asdfghjkl", ((TextView) solo.getView(R.id.ViewISBN)).getText().toString());


        // recover
        solo.clickOnView(solo.getView(R.id.editButton));
        ((EditText) solo.getView(R.id.titleEditor)).setText("ztan7_test");
        ((EditText) solo.getView(R.id.authorEditor)).setText("ztan7");
        ((EditText) solo.getView(R.id.dateEditor)).setText("12-23");
        ((EditText) solo.getView(R.id.isbnEditor)).setText("asd");
        solo.clickOnButton("Complete");

        // TODO: test editing description after it is finished.
        // TODO: test changing image after it is finished.
    }

    @Test
    public void testDeleteBook() {
        solo.clickOnView(solo.getView(R.id.navigation_booklist));

        // create the new book
        Book newBook = new Book("A new Book", "Tester", "2020-11-06", "a random sequence");

        // add a new book to test
        solo.clickOnView((Button) solo.getView(R.id.button_add));
        solo.enterText((EditText) solo.getView(R.id.titleEditor), newBook.getTitle());
        solo.enterText((EditText) solo.getView(R.id.authorEditor), newBook.getAuthor());
        solo.enterText((EditText) solo.getView(R.id.dateEditor), newBook.getDate());
        solo.enterText((EditText) solo.getView(R.id.isbnEditor), newBook.getIsbn());
        solo.clickOnButton("Complete");

        // try to delete the book
        solo.clickInList(2);
        String actualTitle = ((TextView) solo.getView(R.id.ViewBookName)).getText().toString();
        String actualAuthor = ((TextView) solo.getView(R.id.ViewAuthor)).getText().toString();
        String actualDate =  ((TextView) solo.getView(R.id.ViewDate)).getText().toString();
        String actualISBN = ((TextView) solo.getView(R.id.ViewISBN)).getText().toString();
        assertEquals(newBook.getTitle(), actualTitle);
        assertEquals(newBook.getAuthor(), actualAuthor);
        assertEquals(newBook.getDate(), actualDate);
        assertEquals(newBook.getIsbn(), actualISBN);
        solo.clickOnButton("Delete");

        // check the rest book in the list to see if we delete successfully
        solo.clickInList(1);
        actualTitle = ((TextView) solo.getView(R.id.ViewBookName)).getText().toString();
        actualAuthor = ((TextView) solo.getView(R.id.ViewAuthor)).getText().toString();
        actualDate =  ((TextView) solo.getView(R.id.ViewDate)).getText().toString();
        actualISBN = ((TextView) solo.getView(R.id.ViewISBN)).getText().toString();
        // check if we deleted the right one
        assertEquals(false, newBook.getTitle() == actualTitle);
        assertEquals(false, newBook.getAuthor() == actualAuthor);
        assertEquals(false, newBook.getDate() == actualDate);
        assertEquals(false, newBook.getIsbn() == actualISBN);
    }

}