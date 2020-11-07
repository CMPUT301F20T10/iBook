package com.example.ibook;

import android.graphics.pdf.PdfDocument;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.ibook.activities.AddBookActivity;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.PageActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AddBookActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<MainActivity>(MainActivity.class,true, true);

    @Before
    public void setUp(){
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "sim@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "password");
        solo.clickOnButton("Login In");

    }


    @Test
    public void testAddBook(){
        solo.clickOnView(solo.getView(R.id.navigation_booklist));
        solo.clickOnView(solo.getView(R.id.button_add));

        solo.assertCurrentActivity("Wrong activity", AddBookActivity.class);
        //add new book
        solo.enterText((EditText) solo.getView(R.id.titleEditor), "Paper Towns");
        solo.enterText((EditText) solo.getView(R.id.authorEditor), "John Green");
        solo.enterText((EditText) solo.getView(R.id.dateEditor), "2020-11-06");
        solo.enterText((EditText) solo.getView(R.id.isbnEditor), "9780525555735");
        solo.clickOnButton("Complete");

        solo.assertCurrentActivity("Should be page activity", PageActivity.class);
        //new book is in listview
        assertTrue(solo.searchText("Paper Towns"));


    }
    @Test
    public void testLeavingEmptyFields(){
        solo.clickOnView(solo.getView(R.id.navigation_booklist));
        solo.clickOnView(solo.getView(R.id.button_add));

        solo.assertCurrentActivity("Wrong activity", AddBookActivity.class);
        //leave most fields empty
        solo.enterText((EditText) solo.getView(R.id.titleEditor), "Looking for Alaska");

        solo.clickOnButton("Complete");
        //Activity should not change since all book info wasn't filled out
        solo.assertCurrentActivity("Should be add book activity", AddBookActivity.class);

    }

    @After
    public void tearDown(){
        solo.finishOpenedActivities();
    }

}
