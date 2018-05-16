package de.rafaelmiranda.memory.ui

import android.content.Context
import android.support.annotation.DrawableRes
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.common.Shared
import java.util.*

class DifficultyView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {

    private val mTitle: ImageView

    init {
        LayoutInflater.from(context).inflate(R.layout.difficult_view, this, true)
        orientation = LinearLayout.VERTICAL
        mTitle = findViewById(R.id.title)
    }

    fun setDifficulty(difficulty: Int, stars: Int) {
        val titleResource = String.format(Locale.US, "button_difficulty_%d_star_%d", difficulty, stars)
        val drawableResourceId = Shared.context.resources.getIdentifier(titleResource, "drawable", Shared.context.packageName)
        mTitle.setImageResource(drawableResourceId)
    }

    fun setImage(@DrawableRes imageRes: Int) {
        mTitle.setImageResource(imageRes)
    }

}
