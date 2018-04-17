package com.snatik.matches.fragments

import android.os.Bundle
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceFragmentCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.snatik.matches.R
import com.snatik.matches.common.Shared

class GameSettingsFragment : PreferenceFragmentCompat() {

    lateinit var userList : ListPreference
    lateinit var newUser : EditTextPreference
    lateinit var gameSets : ListPreference

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        val fireDb = FirebaseFirestore.getInstance()

        userList = findPreference(getString(R.string.pref_user_selected)) as ListPreference
        newUser = findPreference(getString(R.string.pref_new_user)) as EditTextPreference
        gameSets = findPreference(getString(R.string.pref_game_set_chosen)) as ListPreference

        val setNames = Shared.gameSettings.gameSets.map { it.setName }
        val setBaseFiles = Shared.gameSettings.gameSets.map { it.baseFileName }


    }


}