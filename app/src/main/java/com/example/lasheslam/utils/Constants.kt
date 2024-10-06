package com.example.lasheslam.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

class Constants {
    companion object{
        val MODE_INVITED = booleanPreferencesKey("mode_invited")
        val EMAIL = stringPreferencesKey("email")
    }
}