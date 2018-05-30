package de.rafaelmiranda.memory.ui

import android.content.Context
import android.support.constraint.Group
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import de.rafaelmiranda.memory.R
import de.rafaelmiranda.memory.events.BackEvent
import de.rafaelmiranda.memory.events.NextEvent
import de.rafaelmiranda.memory.events.QuestionsAnsweredEvent
import org.greenrobot.eventbus.EventBus

class PopupWonView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
        RelativeLayout(context, attrs) {

    private val mNextButton: ImageView
    private val mBackButton: ImageView
    private val mEditSum: EditText
    private val mSumGroup: Group

    private var isSumGame = false


    init {
        // todo: Adding new edit text question.
        LayoutInflater.from(context).inflate(R.layout.popup_won_view, this, true)
        mBackButton = findViewById<View>(R.id.button_back) as ImageView
        mNextButton = findViewById<View>(R.id.button_next) as ImageView
        mEditSum = findViewById(R.id.editText)
        mSumGroup = findViewById(R.id.number_sum_group)

        // background, duh!
        setBackgroundResource(R.drawable.tile)

        // and making the sum disappear
        setSumGame(false)

        mBackButton.setOnClickListener {
            if (checkAndSendAnswers()) {
                EventBus.getDefault().post(BackEvent())
            }
        }

        mNextButton.setOnClickListener {
            if (checkAndSendAnswers()) {
                EventBus.getDefault().post(NextEvent())
            }
        }
    }

    /**
     * If there are questions to answer it will only let the user leave if they were all properly
     * answered.
     * @return true if everything is fine, and false if someone has been naughty.
     */
    private fun checkAndSendAnswers(): Boolean {
        val sumAnswer = mEditSum.text.toString()
        return if (!isSumGame) {
            EventBus.getDefault().post(QuestionsAnsweredEvent())
            true
        } else if (sumAnswer.isBlank() || !TextUtils.isDigitsOnly(sumAnswer)) {
            Toast.makeText(context,
                    R.string.please_answer_sum_hint,
                    Toast.LENGTH_SHORT).show()
            false
        } else {
            EventBus.getDefault().post(QuestionsAnsweredEvent(sumAnswer.toInt()))
            true
        }
    }

    fun setSumGame(isSumGame: Boolean = true) {
        this.isSumGame = isSumGame
        mSumGroup.visibility = if (isSumGame) View.VISIBLE else View.GONE
    }

}
