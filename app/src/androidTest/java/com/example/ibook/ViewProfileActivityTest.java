package com.example.ibook;

import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.PageActivity;
import com.example.ibook.activities.SearchResultsActivity;
import com.example.ibook.activities.ViewProfileActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ViewProfileActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<MainActivity>(MainActivity.class,true, true);

    @Before
    public void setUp(){
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "sam@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "123456");
        solo.clickOnButton("Login In");

    }

    //search a user and view their profile
    @Test
    public void testViewProfile() {

        solo.assertCurrentActivity("Wrong activity", PageActivity.class);
        solo.clickOnView(solo.getView(R.id.searchButton));
        solo.sleep(2000);
        //search for a book
        solo.enterText(0,"rob");
        solo.sendKey(Solo.ENTER);
        solo.waitForActivity("Searched activity",2000);
        solo.assertCurrentActivity("Should be searched results activity", SearchResultsActivity.class);
        solo.clickOnView(solo.getView(R.id.search_user));
        //get activity to access its methods
        solo.sleep(1000);
        SearchResultsActivity activity = (SearchResultsActivity) solo.getCurrentActivity();
        ListView listView = activity.getUserListView();
        //click on user in list
        solo.clickOnView(listView.getChildAt(0));
        //view profile activity
        solo.assertCurrentActivity("Wrong activity", ViewProfileActivity.class);
        //robs profile info should be displayed
        assertEquals("rob@gmail.com", ((TextView) solo.getView(R.id.emailID)).getText().toString());
        assertEquals("7809876543", ((TextView) solo.getView(R.id.phoneNumber)).getText().toString());

    }


    @After
    public void tearDown(){
        solo.finishOpenedActivities();
    }

}
