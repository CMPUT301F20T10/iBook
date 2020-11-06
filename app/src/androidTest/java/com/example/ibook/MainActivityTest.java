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
    public void checkSuccessfulLogin(){
        solo.assertCurrentActivity("Wrong activity",MainActivity.class);
        solo.enterText((EditText) solo.getView(R.id.usernameEditText),"yzhang24@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText),"123456");

        solo.clickOnButton("Sign in");
        solo.waitForActivity("Page Activity");
        solo.assertCurrentActivity("Should be page activity",PageActivity.class);

    }
    @Test
    public void checkUnsuccessfulLogin() {
        solo.enterText((EditText) solo.getView(R.id.usernameEditText), "yzhang24@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText), "1");
        solo.clickOnButton("Sign in");
        solo.assertCurrentActivity("Stays in main activity",MainActivity.class);
    }
    @Test
    public void checkEmptyFields(){
        solo.clickOnButton("Sign in");
        solo.assertCurrentActivity("Stays in main activity",MainActivity.class);
    }
    @Test
    public void emailFormat() {
        solo.enterText((EditText) solo.getView(R.id.usernameEditText),"yzhang24");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText),"123456");
        solo.clickOnButton("Sign in");
        solo.assertCurrentActivity("Stays in main activity",MainActivity.class);

    }
    @Test
    public void checkInvalidUser(){
        solo.enterText((EditText) solo.getView(R.id.usernameEditText),"smriti@gmail.com");
        solo.enterText((EditText) solo.getView(R.id.passwordEditText),"123456");
        solo.clickOnButton("Sign in");
        solo.assertCurrentActivity("Stays in main activity",MainActivity.class);

    }
    @Test
    //check if cancel button in sign up activity returns back to main activity
    public void checkCancelButton(){
        solo.clickOnButton("Sign up");
        solo.assertCurrentActivity("Should be sign up activity",SignUpActivity.class);
        solo.clickOnButton("Cancel");
        solo.waitForActivity("Main Activity");
        solo.assertCurrentActivity("Main activity",MainActivity.class);
    }
    @After
    public void tearDown(){
        solo.finishOpenedActivities();
    }


}
