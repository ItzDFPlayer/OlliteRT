/*
 * Copyright 2025-2026 @NightMean (https://github.com/NightMean)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ollitert.llm.server.ui

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

/**
 * Manages app locale configuration and applies language changes.
 */
object LocaleManager {
  
  /**
   * Applies the selected language to the app configuration.
   * This should be called after the language preference is changed.
   */
  fun applyLanguage(context: Context, languageCode: String) {
    val locale = when (languageCode) {
      "zh-rCN" -> Locale.SIMPLIFIED_CHINESE
      "es" -> Locale("es", "ES")
      "fr" -> Locale.FRENCH
      "de" -> Locale.GERMAN
      else -> Locale(languageCode)
    }
    
    Locale.setDefault(locale)
    
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    context.createConfigurationContext(config).also { updatedContext ->
      // Update the base context if needed
      @Suppress("DEPRECATION")
      context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
  }
  
  /**
   * Gets the current locale code from preferences.
   */
  fun getCurrentLanguageCode(context: Context): String {
    return com.ollitert.llm.server.data.ServerPrefs.getLanguage(context)
  }
  
  /**
   * Gets the display name for a language code.
   */
  fun getLanguageDisplayName(languageCode: String): String {
    return when (languageCode) {
      "en" -> "English"
      "es" -> "Español"
      "zh-rCN" -> "中文 (简体)"
      "fr" -> "Français"
      "de" -> "Deutsch"
      "uk" -> "Українська"
      else -> languageCode
    }
  }
}
