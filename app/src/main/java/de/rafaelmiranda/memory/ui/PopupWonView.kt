package de.rafaelmiranda.memory.ui

import android.content.Context
import android.os.Handler
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.events.BackGameEvent
import de.rafaelmiranda.memory.events.NextGameEvent
import org.greenrobot.eventbus.EventBus

class PopupWonView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : RelativeLayout(context, attrs) {

    private val mNextButton: ImageView
    private val mBackButton: ImageView
    private val mHandler: Handler

    init {
        // todo: Adding new edit text question.
        LayoutInflater.from(context).inflate(R.layout.popup_won_view, this, true)
        mBackButton = findViewById<View>(R.id.button_back) as ImageView
        mNextButton = findViewById<View>(R.id.button_next) as ImageView
        // FontLoader.setTypeface(context, arrayOf(SOMETEXTVIEW), FontLoader.Font.GROBOLD)
        setBackgroundResource(R.drawable.tile)
        mHandler = Handler()

        mBackButton.setOnClickListener { EventBus.getDefault().post(BackGameEvent()) }

        mNextButton.setOnClickListener { EventBus.getDefault().post(NextGameEvent()) }
    }

}
