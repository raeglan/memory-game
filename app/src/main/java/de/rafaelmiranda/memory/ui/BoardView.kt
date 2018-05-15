package de.rafaelmiranda.memory.ui

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.os.AsyncTask
import android.util.AttributeSet
import android.util.SparseArray
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.LinearLayout
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.Shared
import de.rafaelmiranda.memory.events.ui.FlipCardEvent
import de.rafaelmiranda.memory.model.BoardArrangement
import de.rafaelmiranda.memory.model.BoardConfiguration
import de.rafaelmiranda.memory.model.Game
import de.rafaelmiranda.memory.utils.Utils
import java.util.*

class BoardView @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null) : LinearLayout(context, attributeSet) {

    private val mRowLayoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
    private var mTileLayoutParams: LinearLayout.LayoutParams? = null
    private val mScreenWidth: Int
    private val mScreenHeight: Int
    private var mBoardConfiguration: BoardConfiguration? = null
    private var mBoardArrangement: BoardArrangement? = null
    private val mViewReference: SparseArray<TileView>
    private val flippedUp = ArrayList<Int>()
    private var mLocked = false
    private var mSize: Int = 0

    init {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        val margin = resources.getDimensionPixelSize(R.dimen.margine_top)
        val padding = resources.getDimensionPixelSize(R.dimen.board_padding)
        mScreenHeight = resources.displayMetrics.heightPixels - margin - padding * 2
        mScreenWidth = resources.displayMetrics.widthPixels - padding * 2 - Utils.px(20)
        mViewReference = SparseArray()
        clipToPadding = false
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    fun setBoard(game: Game) {
        mBoardConfiguration = game.boardConfiguration
        mBoardArrangement = game.boardArrangement
        // calc prefered tiles in width and height
        var singleMargin = resources.getDimensionPixelSize(R.dimen.card_margin)
        val density = resources.displayMetrics.density
        singleMargin = Math.max((1 * density).toInt(), (singleMargin - mBoardConfiguration!!.difficulty.toFloat() * 2f * density).toInt())
        var sumMargin = 0
        for (row in 0 until mBoardConfiguration!!.numRows) {
            sumMargin += singleMargin * 2
        }
        val tilesHeight = (mScreenHeight - sumMargin) / mBoardConfiguration!!.numRows
        val tilesWidth = (mScreenWidth - sumMargin) / mBoardConfiguration!!.numTilesInRow
        mSize = Math.min(tilesHeight, tilesWidth)

        mTileLayoutParams = LinearLayout.LayoutParams(mSize, mSize)
        mTileLayoutParams!!.setMargins(singleMargin, singleMargin, singleMargin, singleMargin)

        // build the ui
        buildBoard()
    }

    /**
     * Build the board
     */
    private fun buildBoard() {

        for (row in 0 until mBoardConfiguration!!.numRows) {
            // add row
            addBoardRow(row)
        }

        clipChildren = false
    }

    private fun addBoardRow(rowNum: Int) {

        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.HORIZONTAL
        linearLayout.gravity = Gravity.CENTER

        for (tile in 0 until mBoardConfiguration!!.numTilesInRow) {
            addTile(rowNum * mBoardConfiguration!!.numTilesInRow + tile, linearLayout)
        }

        // add to this view
        addView(linearLayout, mRowLayoutParams)
        linearLayout.clipChildren = false
    }

    private fun addTile(placementId: Int, parent: ViewGroup) {
        val tileView = TileView.fromXml(context, parent)
        tileView.layoutParams = mTileLayoutParams
        parent.addView(tileView)
        parent.clipChildren = false
        mViewReference.put(placementId, tileView)

        if (placementId < mBoardConfiguration!!.numTiles) {
            object : AsyncTask<Void, Void, Bitmap>() {

                override fun doInBackground(vararg params: Void): Bitmap? {
                    return mBoardArrangement!!.getTileBitmap(placementId, mSize)
                }

                override fun onPostExecute(result: Bitmap) {
                    tileView.setTileImage(result)
                }
            }.execute()

            tileView.setOnClickListener {
                if (!mLocked && tileView.isFlippedDown) {
                    tileView.flipUp()
                    flippedUp.add(placementId)
                    if (flippedUp.size == 2) {
                        mLocked = true
                    }
                    val chosenCard = mBoardArrangement!!.cards!!.get(placementId)
                    Shared.eventBus.notify(FlipCardEvent(chosenCard))
                }
            }

            val scaleXAnimator = ObjectAnimator.ofFloat(tileView, "scaleX", 0.8f, 1f)
            scaleXAnimator.interpolator = BounceInterpolator()
            val scaleYAnimator = ObjectAnimator.ofFloat(tileView, "scaleY", 0.8f, 1f)
            scaleYAnimator.interpolator = BounceInterpolator()
            val animatorSet = AnimatorSet()
            animatorSet.playTogether(scaleXAnimator, scaleYAnimator)
            animatorSet.duration = 500
            tileView.setLayerType(View.LAYER_TYPE_HARDWARE, null)
            animatorSet.start()
        } else {
            tileView.visibility = View.INVISIBLE
        }
    }

    fun flipDownAll() {
        for (id in flippedUp) {
            mViewReference.get(id).flipDown()
        }
        flippedUp.clear()
        mLocked = false
    }

    fun hideCards(id1: Int, id2: Int) {
        animateHide(mViewReference.get(id1))
        animateHide(mViewReference.get(id2))
        flippedUp.clear()
        mLocked = false
    }

    protected fun animateHide(v: TileView) {
        val animator = ObjectAnimator.ofFloat(v, "alpha", 0f)
        animator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                v.setLayerType(View.LAYER_TYPE_NONE, null)
                v.visibility = View.INVISIBLE
            }
        })
        animator.duration = 100
        v.setLayerType(View.LAYER_TYPE_HARDWARE, null)
        animator.start()
    }

    companion object {

        fun fromXml(context: Context, parent: ViewGroup): BoardView {
            return LayoutInflater.from(context).inflate(R.layout.board_view, parent, false) as BoardView
        }
    }

}
