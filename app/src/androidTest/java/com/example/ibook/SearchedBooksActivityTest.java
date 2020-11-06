package com.example.ibook;

import android.widget.EditText;
import android.widget.ListView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.ibook.activities.EditProfile;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.PageActivity;
import com.example.ibook.activities.SearchedBooksActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class SearchedBooksActivityTest {

        private Solo solo;

        @Rule
        public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);

        @Before
        public void setUp() {
            solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());

            solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
            solo.enterText((EditText) solo.getView(R.id.usernameEditText), "sim@gmail.com");
            solo.enterText((EditText) solo.getView(R.id.passwordEditText), "password");
            solo.clickOnButton("Login In");


        }

        @Test
        public void testSearching(){
            solo.assertCurrentActivity("Wrong activity", PageActivity.class);
            solo.clickOnView(solo.getView(R.id.searchButton));

            solo.sleep(2000);
            //search for keyword harper
            solo.enterText(0,"harper");
            solo.sendKey(Solo.ENTER);
            solo.waitForActivity("Searched activity",2000);
            solo.assertCurrentActivity("Should be searched results activity", SearchedBooksActivity.class);
            //get activity to access its methods
            SearchedBooksActivity activity = (SearchedBooksActivity)solo.getCurrentActivity();
            ListView listView = activity.getListView();
            int items = listView.getAdapter().getCount();
            // number of items in the listview should be 3, 3 books have keyword 'harper'
            assertEquals(3, items);
            //search for keyword harper on screen
            assertTrue(solo.searchText("harper"));
        }



        @After
        public void tearDown(){
            solo.finishOpenedActivities();
        }

}