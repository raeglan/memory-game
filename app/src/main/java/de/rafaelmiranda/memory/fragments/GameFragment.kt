package de.rafaelmiranda.memory.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.events.FlipDownCardsEvent
import de.rafaelmiranda.memory.events.GameWonEvent
import de.rafaelmiranda.memory.events.HidePairCardsEvent
import de.rafaelmiranda.memory.model.Impairment
import de.rafaelmiranda.memory.ui.BoardView
import de.rafaelmiranda.memory.ui.PopupManager
import de.rafaelmiranda.memory.utils.Clock
import de.rafaelmiranda.memory.utils.FontLoader
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class GameFragment : BaseFragment() {

    private lateinit var mBoardView: BoardView
    private lateinit var mTime: TextView
    private lateinit var mTimeImage: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.game_fragment, container, false) as ViewGroup
        view.clipChildren = false
        (view.findViewById<View>(R.id.game_board) as ViewGroup).clipChildren = false
        mTime = view.findViewById<View>(R.id.time_bar_text) as TextView
        mTimeImage = view.findViewById<View>(R.id.time_bar_image) as ImageView
        FontLoader.setTypeface(Shared.context, arrayOf(mTime), FontLoader.Font.GROBOLD)
        mBoardView = BoardView.fromXml(activity!!.applicationContext, view)
        val frameLayout = view.findViewById<View>(R.id.game_container) as FrameLayout
        frameLayout.addView(mBoardView)
        frameLayout.clipChildren = false

        // registering for events
        EventBus.getDefault().register(this)

        // build board
        buildBoard()

        // Setting visual foreground impairment, if any
        val boardConfiguration = Shared.engine.activeGame!!.boardConfiguration
        val foreground = Shared.activity.findViewById<ImageView>(R.id.foreground)
        if (boardConfiguration.impairment === Impairment.VISUAL_BLUR) {
            foreground.visibility = View.VISIBLE
            foreground.setImageResource(R.drawable.blur)
            foreground.alpha = 0.98f
        } else {
            foreground.visibility = View.INVISIBLE
        }

        return view
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        val foreground = Shared.activity.findViewById<ImageView>(R.id.foreground)
        foreground.visibility = View.INVISIBLE
        super.onDestroy()
    }

    private fun buildBoard() {
        val game = Shared.engine.activeGame
        val time = game!!.boardConfiguration.time
        setTime(time)
        mBoardView!!.setBoard(game)

        startClock(time)
    }

    private fun setTime(time: Int) {
        val min = time / 60
        val sec = time - min * 60
        mTime!!.text = " " + String.format("%02d", min) + ":" + String.format("%02d", sec)
    }

    private fun startClock(sec: Int) {
        val clock = Clock
        clock.startTimer((sec * 1000).toLong(), 1000, object : Clock.OnTimerCount {

            override fun onTick(millisUntilFinished: Long) {
                setTime((millisUntilFinished / 1000).toInt())
            }

            override fun onFinish() {
                setTime(0)
            }
        })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGameWonEvent(event: GameWonEvent) {
        mTime.visibility = View.GONE
        mTimeImage.visibility = View.GONE
        PopupManager.showPopupWon()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFlipDownCardsEvent(event: FlipDownCardsEvent) {
        mBoardView.flipDownAll()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onHidePairEvent(event: HidePairCardsEvent) {
        mBoardView.hideCards(event.id1, event.id2)
    }

}
