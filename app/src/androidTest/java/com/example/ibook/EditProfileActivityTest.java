package com.example.ibook;

import android.os.SystemClock;
import android.widget.EditText;
import android.widget.TextView;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.ibook.activities.EditBookActivity;
import com.example.ibook.activities.EditProfile;
import com.example.ibook.activities.MainActivity;
import com.example.ibook.fragment.HomeFragment;
import com.example.ibook.fragment.UserFragment;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

public class EditProfileActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class,true, true);

    @Before
    public void setUp(){
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "sim@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "password");
        solo.clickOnButton("Login In");


    }
    @Test
    public void testEditProfile(){
        solo.clickOnView(solo.getView(R.id.navigation_user));
        solo.clickOnView(solo.getView(R.id.editButton));
        solo.assertCurrentActivity("Wrong activity",EditProfile.class);
        //clear text already there
        solo.clearEditText((EditText) solo.getView(R.id.usernameEditText));
        solo.clearEditText((EditText) solo.getView(R.id.emailEditText));
        solo.clearEditText((EditText) solo.getView(R.id.phoneEditText));
        //fill out new info
        solo.enterText((EditText) solo.getView(R.id.usernameEditText),"smriti");
        solo.enterText((EditText) solo.getView(R.id.emailEditText),"sim@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.phoneEditText),"7801113333");

        solo.clickOnButton("SAve");
        //user fragment should be updated with new username and new phone number
        assertTrue(solo.searchText("smriti"));

        TextView phoneTextView = (TextView) solo.getView(R.id.phoneNumber);
        assertEquals("7801113333", phoneTextView.getText().toString());



    }

    @Test
    public void testEmptyField(){
        solo.clickOnView(solo.getView(R.id.navigation_user));
        solo.clickOnView(solo.getView(R.id.editButton));
        solo.assertCurrentActivity("Wrong activity",EditProfile.class);
        //clear text already there
        solo.clearEditText((EditText) solo.getView(R.id.usernameEditText));
        solo.clearEditText((EditText) solo.getView(R.id.emailEditText));
        solo.clearEditText((EditText) solo.getView(R.id.phoneEditText));
        //leave some fields blank
        solo.enterText((EditText) solo.getView(R.id.usernameEditText),"sdhiman");

        solo.clickOnButton("SAve");
        //activity should not change because all fields were'nt filled out
        solo.assertCurrentActivity("Should stay in edit profile activity",EditProfile.class);


    }
    @After
    public void finish() {
        solo.goBack();
        solo.clickOnView(solo.getView(R.id.navigation_user));
        solo.clickOnButton("Sign out");
    }

}
