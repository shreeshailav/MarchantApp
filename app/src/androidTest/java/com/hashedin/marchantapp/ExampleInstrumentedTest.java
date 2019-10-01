package com.hashedin.marchantapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.TextView;

import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.hashedin.marchantapp.viewactivity.LoginActivity;

import org.hamcrest.Matcher;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.TestCase.assertEquals;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private static final String PREFS_NAME = "LoginDetails";
    private static final String KEY_PREF = "key";
    private SharedPreferences sharedPreferences;

    @Rule
    public ActivityTestRule<LoginActivity> activityActivityTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);


    @Before
    public void before() {

        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();

        sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

   // @Test
    public void put_and_get_preference(String username, String password) throws Exception {
        String string1 = "test";

        sharedPreferences.edit().putString(KEY_PREF, string1).apply();
        String string2 = sharedPreferences.getString(KEY_PREF, "");

        // Verify that the received data is correct.
        assertEquals(string1, string2);
    }

    @After
    public void after() {
        sharedPreferences.edit().putString(KEY_PREF, null).apply();
    }


    @Test
    public void login_button_click() throws Exception{

        onView(withId(R.id.login_username)).perform(typeText("BBstaff"),closeSoftKeyboard());
        onView(withId(R.id.login_password)).perform(typeText("admin"),closeSoftKeyboard());


        onView(withId(R.id.login_button)).perform(click());

        Thread.sleep(3000);

        put_and_get_preference("BBstaff","admin");


        onView(withId(R.id.scanqrbtn)).perform(click());




       // String successString = InstrumentationRegistry.getInstrumentation().getTargetContext().getString(R.string.login_error);


        //onView(withId(R.id.invalidtext)).check(matches(withText(successString)));



    }

    private static ViewAction setTextViewVisibitity(final boolean value) {
        return new ViewAction() {

            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(TextView.class);
            }

            @Override
            public void perform(UiController uiController, View view) {
                view.setVisibility(value ? View.VISIBLE : View.GONE);
            }

            @Override
            public String getDescription() {
                return "Show / Hide View";
            }
        };
    }
}
