package de.rafaelmiranda.memory.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Chronometer
import android.widget.FrameLayout
import android.widget.ImageView
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.events.FlipDownCardsEvent
import de.rafaelmiranda.memory.events.GameWonEvent
import de.rafaelmiranda.memory.events.HidePairCardsEvent
import de.rafaelmiranda.memory.ui.BoardView
import de.rafaelmiranda.memory.ui.PopupManager
import de.rafaelmiranda.memory.utils.FontLoader
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class GameFragment : BaseFragment() {

    private lateinit var mBoardView: BoardView
    private lateinit var mTime: Chronometer
    private lateinit var mTimeImage: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.game_fragment, container, false) as ViewGroup
        view.clipChildren = false
        (view.findViewById<View>(R.id.game_board) as ViewGroup).clipChildren = false
        mTime = view.findViewById<View>(R.id.time_bar_text) as Chronometer
        mTimeImage = view.findViewById<View>(R.id.time_bar_image) as ImageView
        FontLoader.setTypeface(Shared.context, arrayOf(mTime), FontLoader.Font.GROBOLD)
        mBoardView = BoardView.fromXml(activity!!.applicationContext, view)
        val frameLayout = view.findViewById<View>(R.id.game_container) as FrameLayout
        frameLayout.addView(mBoardView)
        frameLayout.clipChildren = false

        // the intro popup.


        // registering for events
        EventBus.getDefault().register(this)

        // build board
        buildBoard()

        // starting clock
        mTime.start()

        return view
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        val foreground = Shared.activity.findViewById<ImageView>(R.id.foreground)
        foreground.visibility = View.INVISIBLE

        // stopping the clock
        mTime.stop()

        super.onDestroy()
    }

    private fun buildBoard() {
        val game = Shared.engine.activeGame
        if (game != null)
            mBoardView.setBoard(game)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onGameWonEvent(event: GameWonEvent) {
        mTime.visibility = View.GONE
        mTimeImage.visibility = View.GONE
        PopupManager.showPopupWon(event.gameType)
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFlipDownCardsEvent(event: FlipDownCardsEvent) {
        mBoardView.flipDownAll()
    }

    @Suppress("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onHidePairEvent(event: HidePairCardsEvent) {
        mBoardView.hideCards(event.id1, event.id2)
    }

}
