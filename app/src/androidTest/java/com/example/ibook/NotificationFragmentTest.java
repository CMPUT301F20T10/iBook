package com.example.ibook;

import android.app.Notification;
import android.graphics.pdf.PdfDocument;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.ibook.activities.AddBookActivity;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.PageActivity;
import com.example.ibook.activities.SearchResultsActivity;
import com.example.ibook.activities.ViewBookActivity;
import com.example.ibook.fragment.NotificationsFragment;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class NotificationFragmentTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<MainActivity>(MainActivity.class,true, true);

    @Before
    public void setUp(){
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "rob@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "123456");
        solo.clickOnButton("Login In");

    }


    @Test
    public void testRequestNotif() {

        solo.assertCurrentActivity("Wrong activity", PageActivity.class);
        solo.clickOnView(solo.getView(R.id.searchButton));
        solo.sleep(2000);
        //search for a book
        solo.enterText(0,"everything");
        solo.sendKey(Solo.ENTER);
        solo.waitForActivity("Searched activity",2000);
        solo.assertCurrentActivity("Should be searched results activity", SearchResultsActivity.class);
        //get activity to access its methods
        SearchResultsActivity activity = (SearchResultsActivity) solo.getCurrentActivity();
        RecyclerView listView = activity.getListView();
        //click on first book in list
        solo.clickOnView(listView.getChildAt(0));
        //view book activity
        solo.assertCurrentActivity("Wrong activity", ViewBookActivity.class);
        //request the book
        solo.clickOnButton("Request");

        solo.scrollUp();
        solo.clickOnView(solo.getView(R.id.cancelButton));
        solo.assertCurrentActivity("Wrong activity", SearchResultsActivity.class);
        //sign out of current user
        solo.clickOnView(solo.getView(R.id.cancelButton));
        solo.clickOnView(solo.getView(R.id.navigation_user));
        solo.clickOnView(solo.getView(R.id.logOutbutton));
        //sign in to owner of book
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "sam@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "123456");
        solo.clickOnButton("Login In");
        solo.clickOnView(solo.getView(R.id.navigation_notifications));
        //check if notification is in notif list
        assertTrue(solo.searchText("rob wants to borrow your book Everything I Thought I Knew"));

    }
    @Test
    public void testResponseNotif() {

        solo.assertCurrentActivity("Wrong activity", PageActivity.class);
        solo.clickOnView(solo.getView(R.id.searchButton));
        solo.sleep(2000);
        //search for a book
        solo.enterText(0,"everything");
        solo.sendKey(Solo.ENTER);
        solo.waitForActivity("Searched activity",2000);
        solo.assertCurrentActivity("Should be searched results activity", SearchResultsActivity.class);
        //get activity to access its methods
        SearchResultsActivity activity = (SearchResultsActivity) solo.getCurrentActivity();
        RecyclerView listView = activity.getListView();
        //click on first book in list
        solo.clickOnView(listView.getChildAt(0));
        //view book activity
        solo.assertCurrentActivity("Wrong activity", ViewBookActivity.class);
        //cancel the request
        solo.clickOnButton("Request");

        solo.scrollUp();
        solo.clickOnView(solo.getView(R.id.cancelButton));
        solo.assertCurrentActivity("Wrong activity", SearchResultsActivity.class);
        //sign out of current user
        solo.clickOnView(solo.getView(R.id.cancelButton));
        solo.clickOnView(solo.getView(R.id.navigation_user));
        solo.clickOnView(solo.getView(R.id.logOutbutton));
        //sign in to owner of book
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "sam@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "123456");
        solo.clickOnButton("Login In");
        solo.clickOnView(solo.getView(R.id.navigation_notifications));
        solo.clickOnView(solo.getView(R.id.responseButton));
        //check if notification is in notif list under responses tab
        assertTrue(solo.searchText("rob Cancelled request on the book called Everything I Thought I Knew"));

    }
    @Test
    public void testDeclineRequest() {


        solo.assertCurrentActivity("Wrong activity", PageActivity.class);
        solo.clickOnView(solo.getView(R.id.searchButton));
        solo.sleep(2000);
        //search for a book
        solo.enterText(0,"secret");
        solo.sendKey(Solo.ENTER);
        solo.waitForActivity("Searched activity",2000);
        solo.assertCurrentActivity("Should be searched results activity", SearchResultsActivity.class);
        //get activity to access its methods
        SearchResultsActivity activity = (SearchResultsActivity) solo.getCurrentActivity();
        RecyclerView listView = activity.getListView();
        //click on first book in list
        solo.clickOnView(listView.getChildAt(0));
        //view book activity
        solo.assertCurrentActivity("Wrong activity", ViewBookActivity.class);
        //cancel the request
        solo.clickOnButton("Request");

        solo.scrollUp();
        solo.clickOnView(solo.getView(R.id.cancelButton));
        solo.assertCurrentActivity("Wrong activity", SearchResultsActivity.class);
        //sign out of current user
        solo.clickOnView(solo.getView(R.id.cancelButton));
        solo.clickOnView(solo.getView(R.id.navigation_user));
        solo.clickOnView(solo.getView(R.id.logOutbutton));

        // login as owner of book
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "sam@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "123456");
        solo.clickOnButton("Login In");

        solo.assertCurrentActivity("Wrong activity", PageActivity.class);
        solo.clickOnView(solo.getView(R.id.navigation_notifications));


        //decline robs request
        solo.clickInList(0);
        solo.clickOnButton("Decline");
        //request should be deleted from list
        assertFalse(solo.searchText("rob wants to borrow your book Two Can Keep a Secret"));

    }



    @After
    public void tearDown(){
        solo.finishOpenedActivities();
    }

}
