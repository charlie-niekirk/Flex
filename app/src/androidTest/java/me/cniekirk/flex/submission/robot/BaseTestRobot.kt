package me.cniekirk.flex.submission.robot

import androidx.annotation.LayoutRes
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers

open class BaseTestRobot {

    fun checkIfViewDisplayed(@LayoutRes resId: Int) {
        Espresso.onView(ViewMatchers.withId(resId)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

    fun checkIfTextDisplayed(text: String) {
        Espresso.onView(ViewMatchers.withText(text)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}