package com.h31030.personalcalendar

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.h31030.personalcalendar.ui.LocalAppContainer
import com.h31030.personalcalendar.ui.navigation.PersonalCalendarNavHost
import com.h31030.personalcalendar.ui.theme.PersonalCalendarTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val container = (application as PersonalCalendarApplication).container

        setContent {
            CompositionLocalProvider(LocalAppContainer provides container) {
                PersonalCalendarTheme {
                    Surface(modifier = Modifier.fillMaxSize()) {
                        PersonalCalendarNavHost()
                    }
                }
            }
        }
    }
}
