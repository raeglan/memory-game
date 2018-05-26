package de.rafaelmiranda.memory


import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.ImageView
import com.google.gson.Gson
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.engine.Engine
import de.rafaelmiranda.memory.engine.ScreenController
import de.rafaelmiranda.memory.events.BackGameEvent
import de.rafaelmiranda.memory.model.GameSettings
import de.rafaelmiranda.memory.ui.PopupManager
import de.rafaelmiranda.memory.utils.JsonUtils
import de.rafaelmiranda.memory.utils.Utils
import org.greenrobot.eventbus.EventBus

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private var mBackgroundImage: ImageView? = null
    private var eegBlinkView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Shared.context = applicationContext
        Shared.engine = Engine
        Shared.engine.start()

        setContentView(R.layout.activity_main)
        mBackgroundImage = findViewById(R.id.background_image)
        eegBlinkView = findViewById(R.id.v_eeg_blink)

        Shared.activity = this
        Shared.engine.setBackgroundImageView(mBackgroundImage!!)
        Shared.tts = TextToSpeech(this, this)

        // getting the game settings from the Json file
        val gson = Gson()
        val settingsJson = JsonUtils
                .getJsonStringFromRaw(this, R.raw.game_settings)
        Shared.gameSettings = gson.fromJson(settingsJson, GameSettings::class.java)

        // makes sure we have that precious real state by hiding the soft keys
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        // set background
        setBackgroundImage()

        // set menu
        ScreenController.openScreen(ScreenController.Screen.MENU)
    }

    override fun onDestroy() {
        Engine.stop()
        super.onDestroy()
    }

    override fun onBackPressed() {
        if (PopupManager.isShown) {
            PopupManager.closePopup()
            if (ScreenController.lastScreen === ScreenController.Screen.GAME) {
                EventBus.getDefault().post(BackGameEvent())
            }
        } else if (ScreenController.onBack()) {
            super.onBackPressed()
        }
    }

    fun blinkEegLight() {
        eegBlinkView!!.setBackgroundColor(Color.WHITE)

        Thread(Runnable {
            try {
                Thread.sleep(BLINK_TIME)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            runOnUiThread { eegBlinkView!!.setBackgroundColor(Color.BLACK) }
        }).start()
    }

    private fun setBackgroundImage() {
        var bitmap = Utils.scaleDown(R.drawable.background, Utils.screenWidth(), Utils.screenHeight())
        bitmap = Utils.crop(bitmap, Utils.screenHeight(), Utils.screenWidth())
        bitmap = Utils.downscaleBitmap(bitmap, 2)
        mBackgroundImage!!.setImageBitmap(bitmap)
    }

    /**
     * called when the TextToSpeech finished initializing.
     *
     * @param status can be SUCCESS or ERROR
     */
    override fun onInit(status: Int) {
        if (status == TextToSpeech.ERROR)
            Shared.tts = null
    }

    companion object {

        private val BLINK_TIME: Long = 150
    }
}
