package de.rafaelmiranda.memory.fragments

import android.os.Bundle
import android.support.v7.preference.EditTextPreference
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.MemoryDb
import de.rafaelmiranda.memory.common.Shared

class GameSettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    private lateinit var userList: ListPreference
    lateinit var newUser: EditTextPreference
    lateinit var gameSets: ListPreference

    lateinit var newUserKey: String
    lateinit var userKey: String
    lateinit var gameSetKey: String

    val userOther = "Other"

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.preferences)

        val fireDb = FirebaseFirestore.getInstance()

        userKey = getString(R.string.pref_user)
        gameSetKey = getString(R.string.pref_game_set)
        newUserKey = getString(R.string.pref_new_user)

        userList = findPreference(userKey) as ListPreference
        newUser = findPreference(getString(R.string.pref_new_user)) as EditTextPreference
        gameSets = findPreference(gameSetKey) as ListPreference

        // disabling adding new user(we have to wait for our DB beforehand)
        newUser.isEnabled = false
        // without the list a user shouldn't click here neither.
        userList.isEnabled = false

        // setting change listeners
        userList.onPreferenceChangeListener = this
        newUser.onPreferenceChangeListener = this

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
                })
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        return when (preference?.key) {
            userKey -> {
                val user = newValue as String
                newUser.isEnabled = user == userOther
                true
            }
            newUserKey -> {
                val user = newValue as String
                val userName = user.toLowerCase().replace(' ', '_') // offshore method
                if (userList.entryValues.contains(userName)) {
                    Toast.makeText(context, "New users must be unique", Toast.LENGTH_SHORT)
                            .show()
                } else {
                    userList.entries = userList.entries.toMutableList()
                            .apply { this.add(0, user) }.toTypedArray()
                    userList.entryValues = userList.entryValues.toMutableList()
                            .apply { this.add(0, userName) }.toTypedArray()
                    userList.setValueIndex(0)
                }
                false
            }
            else -> true
        }
    }

    private fun setupUserList(snapshot: QuerySnapshot?) {
        val userNames = ArrayList<String>()
        val names = ArrayList<String>()

        // getting the names from the document.
        snapshot?.documents?.forEach({
            val name = it.getString(MemoryDb.FIELD_NAME)
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
                        .getString(userKey, userOther)
        val selectedUserIndex =
                if (selectedUser == userOther) {
                    // no user selected so we select the first one on the list(if any other than other)
                    0
                } else {
                    val index = userNames.indexOf(selectedUser)
                    if (index == -1) {
                        userNames.add(0, selectedUser)
                        0
                    } else {
                        index
                    }
                }

        // adding the "Other" selection as last
        userNames.add(userOther)
        names.add(userOther)

        userList.entries = names.toTypedArray()
        userList.entryValues = userNames.toTypedArray()

        userList.setValueIndex(selectedUserIndex)

        userList.isEnabled = true
    }
}