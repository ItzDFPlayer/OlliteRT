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

package com.ollitert.llm.server.ui.server.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import java.util.Locale
import com.ollitert.llm.server.R
import com.ollitert.llm.server.ui.server.SettingsViewModel
import com.ollitert.llm.server.ui.theme.OlliteRTPrimary
import com.ollitert.llm.server.ui.LocaleManager

/**
 * Converts language code to readable display name using string resources
 */
@Composable
private fun getLanguageDisplayName(languageCode: String?): String {
  val context = LocalContext.current
  return when (languageCode) {
    "en" -> context.getString(R.string.language_name_en)
    "es" -> context.getString(R.string.language_name_es)
    "zh-rCN" -> context.getString(R.string.language_name_zh_rCN)
    "fr" -> context.getString(R.string.language_name_fr)
    "de" -> context.getString(R.string.language_name_de)
    "uk" -> context.getString(R.string.language_name_uk)
    else -> languageCode?.let { context.getString(R.string.language_name_en) } ?: context.getString(R.string.language_name_en)
  }
}

/**
 * Gets the system's default language code
 */
private fun getSystemLanguageCode(): String {
  val locale = Locale.getDefault()
  val language = locale.language
  val country = locale.country
  
  return when {
    language == "zh" && (country == "CN" || country == "SG") -> "zh-rCN"
    language in listOf("en", "es", "fr", "de", "uk") -> language
    else -> "en" // fallback to English
  }
}

@Composable
internal fun GeneralCard(vm: SettingsViewModel) {
  val context = LocalContext.current
  
  SettingsCard(
    icon = Icons.Outlined.PhoneAndroid,
    title = stringResource(R.string.settings_card_general),
    searchQuery = vm.searchQuery,
  ) {
    // Language dropdown setting
    if (vm.settingVisible(LANGUAGE.key)) {
      Text(
        text = stringResource(R.string.settings_language_label),
        style = androidx.compose.material3.MaterialTheme.typography.labelMedium,
        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.height(4.dp))
      Column {
        OutlinedTextField(
          value = getLanguageDisplayName(vm.getDropdownEntry(LANGUAGE.key)?.current),
          onValueChange = {},
          readOnly = true,
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .clickable { vm.showLanguageDropdown = true },
          enabled = false,
          colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = androidx.compose.material3.MaterialTheme.colorScheme.outline,
            disabledContainerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainerHigh,
          ),
        )
        DropdownMenu(
          expanded = vm.showLanguageDropdown,
          onDismissRequest = { vm.showLanguageDropdown = false },
        ) {
          listOf(
            "en",
            "es", 
            "zh-rCN",
            "fr",
            "de",
            "uk"
          ).forEach { code ->
            DropdownMenuItem(
              text = {
                Text(
                  getLanguageDisplayName(code),
                  color = if (vm.getDropdownEntry(LANGUAGE.key)?.current == code) OlliteRTPrimary else androidx.compose.material3.MaterialTheme.colorScheme.onSurface,
                )
              },
              onClick = {
                vm.getDropdownEntry(LANGUAGE.key)?.update(code)
                LocaleManager.applyLanguage(context, code)
                vm.showLanguageDropdown = false
              },
            )
          }
        }
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = stringResource(R.string.settings_language_desc),
        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
      )
      Spacer(modifier = Modifier.height(16.dp))
    }

    // Toggle settings
    val toggleKeys = cardDefsById[CardId.GENERAL]?.settings
      ?.filter { it is SettingDef.Toggle }
      ?.map { it.key } ?: emptyList()
    
    var visibleCount = 0
    toggleKeys.forEach { key ->
      if (!vm.settingVisible(key)) return@forEach
      val def = settingDefsByKey[key] as? SettingDef.Toggle ?: return@forEach
      val entry = vm.getToggleEntry(key) ?: return@forEach
      if (visibleCount > 0 || vm.settingVisible(LANGUAGE.key)) SettingDivider()
      visibleCount++
      val descRes = vm.settingDescriptionOverride(key) ?: def.descriptionRes
      ToggleSettingRow(
        label = stringResource(def.labelRes),
        description = stringResource(descRes),
        checked = entry.current,
        onCheckedChange = { newValue ->
          if (key == "trim_prompt" && newValue) {
            vm.showTrimPromptWarning = true
          } else {
            entry.update(newValue)
          }
        },
        searchQuery = vm.searchQuery,
        enabled = vm.isSettingEnabled(key),
        alphaOverride = vm.settingAlpha(key),
      )
    }
  }
}
