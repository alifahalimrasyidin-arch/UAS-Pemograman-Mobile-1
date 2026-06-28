package com.example.uaspm1kelompok1.database

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    companion object {

        private const val PREF_NAME = "TIASA_SESSION"

        private const val KEY_LOGIN = "is_login"

        private const val KEY_USERNAME = "username"

        private const val KEY_NAMA = "nama"

        private const val KEY_ROLE = "role"
    }

    private val preferences: SharedPreferences =
        context.getSharedPreferences(
            PREF_NAME,
            Context.MODE_PRIVATE
        )

    fun saveLogin(

        username: String,

        nama: String,

        role: String

    ) {

        preferences.edit()

            .putBoolean(KEY_LOGIN, true)

            .putString(KEY_USERNAME, username)

            .putString(KEY_NAMA, nama)

            .putString(KEY_ROLE, role)

            .apply()
    }

    fun isLogin(): Boolean {

        return preferences.getBoolean(
            KEY_LOGIN,
            false
        )
    }

    fun getUsername(): String {

        return preferences.getString(
            KEY_USERNAME,
            ""
        ) ?: ""
    }

    fun getNama(): String {

        return preferences.getString(
            KEY_NAMA,
            ""
        ) ?: ""
    }

    fun getRole(): String {

        return preferences.getString(
            KEY_ROLE,
            ""
        ) ?: ""
    }

    fun logout() {

        preferences.edit()

            .clear()

            .apply()
    }
}