package com.dialogapp.dialog.ui;

import android.content.ComponentName;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.dialogapp.dialog.R;
import com.dialogapp.dialog.ui.mainscreen.MainActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getTargetContext;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.RootMatchers.withDecorView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;

@RunWith(AndroidJUnit4.class)
public class LoginActivityTest {

    @Rule
    public IntentsTestRule<LoginActivity> mActivityRule = new IntentsTestRule<>(
            LoginActivity.class);

    private String validToken, invalidToken;

    @Before
    public void setUp() throws Exception {
        validToken = "012345678901234567890";
        invalidToken = "01234567890";
    }

    @Test
    public void checkInput_invalidToken() throws Exception {
        onView(withId(R.id.editText_login))
                .perform(typeText(invalidToken), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.button_login)).perform(click());

        onView(withText(R.string.login_invalid_token)).inRoot(withDecorView(
                not(is(mActivityRule.getActivity().
                        getWindow().getDecorView())))).
                check(matches(isDisplayed()));
    }

    @Test
    public void checkInput_validToken() throws Exception {
        onView(withId(R.id.editText_login))
                .perform(typeText(validToken), ViewActions.closeSoftKeyboard());
        onView(withId(R.id.button_login)).perform(click());

        intended(hasComponent(new ComponentName(getTargetContext(), MainActivity.class)));
    }
}
