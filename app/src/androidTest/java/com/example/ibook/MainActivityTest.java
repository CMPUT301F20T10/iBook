package com.example.ibook;

import android.app.Activity;
import android.graphics.pdf.PdfDocument;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.ibook.activities.MainActivity;
import com.example.ibook.activities.PageActivity;
import com.example.ibook.activities.SignUpActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MainActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule = new ActivityTestRule<MainActivity>(MainActivity.class,true, true);

    @Before
    public void setUp(){
        solo = new Solo(InstrumentationRegistry.getInstrumentation(),rule.getActivity());

    }
    @Test
    //after a successful login the user clicks on sign in and goes to homepage
    public void testSuccessfulLogin(){
        solo.assertCurrentActivity("Wrong activity",MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameEditText),"sim@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText),"password");

        solo.clickOnButton("Login In");
        solo.waitForActivity("Page Activity");
        //after successful sign up user should be in home screen
        solo.assertCurrentActivity("Should be page activity",PageActivity.class);

    }
    @Test
    public void testWrongPassword() {
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "sim@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "1");
        solo.clickOnButton("Login In");
        //should not continue to home page
        solo.assertCurrentActivity("Stays in main activity",MainActivity.class);
    }
    @Test
    public void testEmptyFields(){
        solo.clickOnButton("Login In");
        //should not continue to home page
        solo.assertCurrentActivity("Stays in main activity",MainActivity.class);
    }
    @Test
    public void testEmailFormat() {
        //input wrong email format
        solo.enterText((EditText) solo.getView(R.id.usernameEditText),"sim");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText),"password");
        solo.clickOnButton("Login In");
        //should not continue to home page
        solo.assertCurrentActivity("Stays in main activity",MainActivity.class);

    }
    @Test
    public void testInvalidUser(){
        //input user that does ot exist
        solo.enterText((EditText) solo.getView(R.id.usernameEditText),"red@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText),"123456");
        solo.clickOnButton("Login In");
        //should not continue to home page
        solo.assertCurrentActivity("Stays in main activity",MainActivity.class);

    }
    @Test
    public void testSignUpButton(){
        //test sign up button goes to sign up screen
        solo.clickOnView(solo.getView(R.id.signUp));
        solo.waitForActivity("Sign up page",2000);
        solo.assertCurrentActivity("Should be sign up page",SignUpActivity.class);

    }

    @After
    public void tearDown(){
        solo.finishOpenedActivities();
    }


}
