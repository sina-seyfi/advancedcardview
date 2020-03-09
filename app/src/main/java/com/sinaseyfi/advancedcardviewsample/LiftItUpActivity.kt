package com.sinaseyfi.advancedcardviewsample

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.SeekBar
import com.google.android.material.animation.ArgbEvaluatorCompat
import kotlinx.android.synthetic.main.activity_lift_it_up.*
import kotlin.math.max

class LiftItUpActivity : AppCompatActivity() {

    private var maxShadow = 16f
    private val alpha = 0.2f
    private val backgroundColor = Color.rgb(239, 239, 239)
    private val positiveLiftColor = Color.rgb(255, 255, 255)
    private val negativeLiftColor = Color.rgb(231, 231, 231)
    private val argbEvaluatorCompat = ArgbEvaluatorCompat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lift_it_up)
        maxShadow = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, maxShadow, resources.displayMetrics)
        lift_amount.setOnSeekBarChangeListener(onLiftChanged)
    }

    private fun setShadow(progress: Int) {
        var positiveFraction = ((progress - 100) / 100f)
        var negativeFraction = ((100 - progress) / 100f)
        if(progress > 100) {
            card.setShadowInnerAlpha(0, 0f)
            card.setShadowInnerBlur(0, 0f)
            card.setShadowOuterAlpha(0, alpha)
            card.setShadowOuterBlur(0, positiveFraction * maxShadow)
            card.background_Color = argbEvaluatorCompat.evaluate(positiveFraction, backgroundColor, positiveLiftColor)
        } else if(progress == 100) {
            card.setShadowInnerAlpha(0, 0f)
            card.setShadowOuterAlpha(0, 0f)
        } else {
            card.setShadowOuterAlpha(0, 0f)
            card.setShadowOuterBlur(0, 0f)
            card.setShadowInnerAlpha(0, alpha)
            card.setShadowInnerBlur(0, negativeFraction * maxShadow)
            card.background_Color = argbEvaluatorCompat.evaluate(negativeFraction, backgroundColor, negativeLiftColor)
        }
        card.invalidate()
    }

    private val onLiftChanged = object: SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            setShadow(progress)
            Log.d("TAGGGGG", progress.toString())
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {}

        override fun onStopTrackingTouch(seekBar: SeekBar?) {}

    }

}
