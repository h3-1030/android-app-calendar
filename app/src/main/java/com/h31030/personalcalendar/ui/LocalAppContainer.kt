package com.h31030.personalcalendar.ui

import androidx.compose.runtime.staticCompositionLocalOf
import com.h31030.personalcalendar.AppContainer

val LocalAppContainer = staticCompositionLocalOf<AppContainer> {
    error("AppContainer is not provided. Wrap the content with LocalAppContainer.Provides.")
}
