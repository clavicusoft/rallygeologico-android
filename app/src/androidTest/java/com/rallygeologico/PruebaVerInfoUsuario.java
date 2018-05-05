package com.rallygeologico;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PruebaVerInfoUsuario {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void pruebaVerInfoUsuario() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.btn_inicio), withText("INICIAR"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.RelativeLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction loginButton = onView(
                allOf(withId(R.id.loginButton), withText("INGRESAR CON FACEBOOK"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        0),
                                2),
                        isDisplayed()));
        loginButton.perform(click());

        ViewInteraction button = onView(
                allOf(withId(R.id.btn_cont_login),
                        childAtPosition(
                                childAtPosition(
                                        IsInstanceOf.<View>instanceOf(android.widget.LinearLayout.class),
                                        1),
                                0),
                        isDisplayed()));
        button.check(matches(isDisplayed()));

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.btn_cont_login), withText("CONTINUAR"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.LinearLayout")),
                                        1),
                                0),
                        isDisplayed()));
        appCompatButton2.perform(click());

        ViewInteraction appCompatImageButton = onView(
                allOf(withContentDescription("Navigate up"),
                        childAtPosition(
                                allOf(withId(R.id.appbar),
                                        childAtPosition(
                                                withId(R.id.content),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatImageButton.perform(click());

        ViewInteraction checkedTextView = onView(
                allOf(withId(R.id.design_menu_item_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.design_navigation_view),
                                        1),
                                0),
                        isDisplayed()));
        checkedTextView.check(matches(isDisplayed()));

        ViewInteraction navigationMenuItemView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.navview),
                                        0)),
                        1),
                        isDisplayed()));
        navigationMenuItemView.perform(click());

        ViewInteraction imageView = onView(
                allOf(withId(R.id.profile),
                        childAtPosition(
                                allOf(withId(R.id.layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                                0)),
                                2),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

        ViewInteraction relativeLayout = onView(
                allOf(withId(R.id.profile_layout),
                        childAtPosition(
                                allOf(withId(R.id.layout),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.widget.ScrollView.class),
                                                0)),
                                1),
                        isDisplayed()));
        relativeLayout.check(matches(isDisplayed()));

        ViewInteraction viewPager = onView(
                allOf(withId(R.id.pagerProfile),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.layout),
                                        3),
                                0),
                        isDisplayed()));
        viewPager.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withText("Logros"),
                        childAtPosition(
                                withParent(withId(R.id.pagerProfile)),
                                2),
                        isDisplayed()));
        textView.perform(click());

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
