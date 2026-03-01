package com.thmanyah.thmanyahdemo.ui.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.thmanyah.thmanyahdemo.ui.MainActivity
import org.junit.Rule
import org.junit.Test

class HomeScreenActivityTest {
    // Use ActivityRule only for tests that assert on the real Activity UI (no setContent).
    @get:Rule
    val activityRule = createAndroidComposeRule<MainActivity>()


    @Test
    fun homeScreen_withActivityRule_launchesAndShowsHomeTab() {
        // MainActivity already set content; wait for the home tab label to appear.
        activityRule.waitUntil(timeoutMillis = 5_000) {
            try {
                activityRule.onNodeWithText("home", substring = true).assertExists()
                true
            } catch (_: AssertionError) {
                false
            }
        }
        activityRule
            .onNodeWithText("home", substring = true)
            .assertIsDisplayed()
    }

    @Test
    fun clickOnLibraryTab_showsLibraryScreen() {
        activityRule.waitUntil(timeoutMillis = 5_000) {
            try {
                activityRule.onNodeWithText("home", substring = true).assertExists()
                true
            } catch (_: AssertionError) {
                false
            }
        }
        activityRule
            .onNodeWithText("library", substring = true)
            .performClick()
        activityRule
            .onNodeWithText("library", substring = true)
            .assertIsDisplayed()
    }
}