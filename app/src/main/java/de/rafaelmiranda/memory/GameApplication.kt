package de.rafaelmiranda.memory

import android.app.Application
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings


class GameApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val firestore = FirebaseFirestore.getInstance()
        val settings = FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build()
        firestore.firestoreSettings = settings
    }
}
