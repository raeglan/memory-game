package de.rafaelmiranda.memory.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Camera
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import de.rafaelmiranda.memory.R

class TileView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : FrameLayout(context, attrs) {

    private var mTopImage: RelativeLayout? = null
    private var mTileImage: ImageView? = null
    var isFlippedDown = true
        private set

    override fun onFinishInflate() {
        super.onFinishInflate()
        mTopImage = findViewById<View>(R.id.image_top) as RelativeLayout
        mTileImage = findViewById<View>(R.id.image) as ImageView
    }

    fun setTileImage(bitmap: Bitmap) {
        mTileImage!!.setImageBitmap(bitmap)
    }

    fun flipUp() {
        isFlippedDown = false
        flip()
    }

    fun flipDown() {
        isFlippedDown = true
        flip()
    }

    private fun flip() {
        val flipAnimation = FlipAnimation(mTopImage, mTileImage)
        if (mTopImage!!.visibility == View.GONE) {
            flipAnimation.reverse()
        }
        startAnimation(flipAnimation)
    }

    inner class FlipAnimation
    /**
     * Creates a 3D flip animation between two views.
     *
     * @param fromView
     * First view in the transition.
     * @param toView
     * Second view in the transition.
     */
    (private var fromView: View?, private var toView: View?) : Animation() {
        private var camera: Camera? = null

        private var centerX: Float = 0.toFloat()
        private var centerY: Float = 0.toFloat()

        private var forward = true

        init {

            duration = 700
            fillAfter = false
            interpolator = AccelerateDecelerateInterpolator()
        }

        fun reverse() {
            forward = false
            val switchView = toView
            toView = fromView
            fromView = switchView
        }

        override fun initialize(width: Int, height: Int, parentWidth: Int, parentHeight: Int) {
            super.initialize(width, height, parentWidth, parentHeight)
            centerX = (width / 2).toFloat()
            centerY = (height / 2).toFloat()
            camera = Camera()
        }

        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            // Angle around the y-axis of the rotation at the given time
            // calculated both in radians and degrees.
            val radians = Math.PI * interpolatedTime
            var degrees = (180.0 * radians / Math.PI).toFloat()

            // Once we reach the midpoint in the animation, we need to hide the
            // source view and show the destination view. We also need to change
            // the angle by 180 degrees so that the destination does not come in
            // flipped around
            if (interpolatedTime >= 0.5f) {
                degrees -= 180f
                fromView!!.visibility = View.GONE
                toView!!.visibility = View.VISIBLE
            }

            if (forward)
                degrees = -degrees // determines direction of rotation when
            // flip begins

            val matrix = t.matrix
            camera!!.save()
            camera!!.rotateY(degrees)
            camera!!.getMatrix(matrix)
            camera!!.restore()
            matrix.preTranslate(-centerX, -centerY)
            matrix.postTranslate(centerX, centerY)
        }
    }

    companion object {

        fun fromXml(context: Context, parent: ViewGroup): TileView {
            return LayoutInflater.from(context).inflate(R.layout.tile_view, parent, false) as TileView
        }
    }
}
