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
import de.rafaelmiranda.memory.events.BackEvent
import de.rafaelmiranda.memory.events.NextEvent
import de.rafaelmiranda.memory.events.OpenSettingsEvent
import de.rafaelmiranda.memory.fragments.GameSettingsFragment
import de.rafaelmiranda.memory.fragments.MenuFragment
import de.rafaelmiranda.memory.model.GameSettings
import de.rafaelmiranda.memory.ui.PopupManager
import de.rafaelmiranda.memory.utils.JsonUtils
import de.rafaelmiranda.memory.utils.Utils
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private val mBackgroundImage: ImageView by lazy {
        findViewById<ImageView>(R.id.background_image)
    }

    private val eegBlinkView: View by lazy {
        findViewById<View>(R.id.v_eeg_blink)
    }

    /**
     * How long the light will flash for our EEG to see.
     */
    private val blinkTime: Long by lazy {
        resources.getInteger(R.integer.blink_time).toLong()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // starting everything
        Shared.context = applicationContext
        Engine.start()
        EventBus.getDefault().register(this)

        setContentView(R.layout.activity_main)

        Shared.activity = this
        Engine.setBackgroundImageView(mBackgroundImage)
        Shared.tts = TextToSpeech(this, this)

        // getting the game settings from the Json file
        val gson = Gson()
        val settingsJson = JsonUtils
                .getJsonStringFromRaw(this, R.raw.game_settings)
        Shared.gameSettings = gson.fromJson(settingsJson, GameSettings::class.java)

        // makes sure we have that precious real state by hiding the soft keys
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        }

        // set background
        setBackgroundImage()

        // set menu
        ScreenController.openFragment(supportFragmentManager, MenuFragment(), false)
    }

    override fun onDestroy() {
        Engine.stop()
        Shared.tts?.shutdown()
        Shared.tts = null
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onBackPressed() {
        when {
            PopupManager.isShown -> PopupManager.closePopup()
            supportFragmentManager.backStackEntryCount > 0 -> supportFragmentManager.popBackStack()
            else -> super.onBackPressed()
        }
    }

    fun blinkEegLight() {
        eegBlinkView.setBackgroundColor(Color.WHITE)

        Thread(Runnable {
            try {
                Thread.sleep(blinkTime)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            runOnUiThread { eegBlinkView.setBackgroundColor(Color.BLACK) }
        }).start()
    }

    private fun setBackgroundImage() {
        var bitmap = Utils.scaleDown(R.drawable.background, Utils.screenWidth(), Utils.screenHeight())
        bitmap = Utils.crop(bitmap, Utils.screenHeight(), Utils.screenWidth())
        bitmap = Utils.downscaleBitmap(bitmap, 2)
        mBackgroundImage.setImageBitmap(bitmap)
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


    @Suppress("unused", "UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBackEvent(event: BackEvent) {
        PopupManager.closePopup()
        onBackPressed()
    }

    @Suppress("unused", "UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNextEvent(event: NextEvent) {
        Engine.onNext(supportFragmentManager)
    }

    @Suppress("unused", "UNUSED_PARAMETER")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onOpenSettingsEvent(event: OpenSettingsEvent) {
        ScreenController.openFragment(supportFragmentManager, GameSettingsFragment(),
                true)
    }

    companion object {

    }
}
