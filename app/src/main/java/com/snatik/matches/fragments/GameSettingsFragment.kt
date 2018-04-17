package com.snatik.matches.fragments

import android.os.Bundle
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.ListPreference
import android.support.v7.preference.PreferenceFragmentCompat
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.snatik.matches.R
import com.snatik.matches.common.MemoryDb
import com.snatik.matches.common.Shared

class GameSettingsFragment : PreferenceFragmentCompat() {

    lateinit var userList: ListPreference
    lateinit var newUser: EditTextPreference
    lateinit var gameSets: ListPreference

    lateinit var userKey: String
    lateinit var gameSetKey: String

    val userOther = "Other"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        val fireDb = FirebaseFirestore.getInstance()

        userKey = getString(R.string.pref_user)
        gameSetKey = getString(R.string.pref_game_set)

        userList = findPreference(userKey) as ListPreference
        newUser = findPreference(getString(R.string.pref_new_user)) as EditTextPreference
        gameSets = findPreference(gameSetKey) as ListPreference

        // disabling adding new user(we have to wait for our DB beforehand)
        newUser.isEnabled = false

        // getting the sets available from our settings json and showing them.
        val setNames = Shared.gameSettings.gameSets.map { it.setName }.toTypedArray()
        val setBaseFiles = Shared.gameSettings.gameSets.map { it.baseFileName }.toTypedArray()
        gameSets.entries = setNames
        gameSets.entryValues = setBaseFiles

        // now checking if a set was previously chosen and showing it(if it is still available)
        val previousSetIndex =
                preferenceManager
                        .sharedPreferences
                        .getString(gameSetKey, null)
                        .let {
                            setBaseFiles.indexOf(it)
                        }
        gameSets.setValueIndex(if (previousSetIndex != -1) previousSetIndex else 0)

        fireDb.collection(MemoryDb.COLLECTION_USERS)
                .get()
                .addOnSuccessListener(this::setupUserList)
                .addOnFailureListener({
                    setupUserList(null)
                    setDefaultUser()
                })
    }

    private fun setupUserList(snapshot: QuerySnapshot?) {
        val userNames = ArrayList<String>()
        val names = ArrayList<String>()

        // getting the names from the document.
        snapshot?.documents?.forEach({
            val name = it.getString(MemoryDb.FIELD_USER)
            if (name != null) {
                userNames.add(it.id)
                names.add(name)
            }
        })

        // the default should be there, if not then we add it anyway(this implementation is not
        // supposed to be "released" in any way, shape or form.
        val selectedUser =
                preferenceManager
                        .sharedPreferences
                        .getString(userKey, null)
        val selectedUserIndex = if(selectedUser == null) {
            // no user, we should enable the new user thingy
            newUser.isEnabled = true
            0
        } else {
            val index = userNames.indexOf()
        }

        // adding the "Other" selection as last
        userNames.add(userOther)
        names.add(userOther)


    }

    private fun setDefaultUser() {

    }


}