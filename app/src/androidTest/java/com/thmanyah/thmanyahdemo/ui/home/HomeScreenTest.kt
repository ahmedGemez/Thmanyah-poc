package com.thmanyah.thmanyahdemo.ui.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.thmanyah.thmanyahdemo.ui.MainActivity
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

    // Use ComposeRule for tests that set their own content (Activity has already set content in onCreate).
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun welcomeText_isDisplayed() {
        composeTestRule.setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Welcome to Thmanyah",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("welcome_text")
                )
            }
        }

        composeTestRule
            .onNodeWithText("Welcome to Thmanyah")
            .assertIsDisplayed()
    }

    @Test
    fun welcomeText_hasTestTag() {
        composeTestRule.setContent {
            Box(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Welcome to Thmanyah",
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("welcome_text")
                )
            }
        }

        composeTestRule
            .onNodeWithTag("welcome_text")
            .assertIsDisplayed()
    }


}
