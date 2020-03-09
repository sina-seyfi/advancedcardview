package com.sinaseyfi.advancedcardviewsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import kotlinx.android.synthetic.main.activity_a_hole_in_your_screen.*
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.sqrt

class AHoleInYourScreenActivity : AppCompatActivity() {

    private var maxShadowDistance = 0
    private var centerX = 0
    private var centerY = 0
    private var x = centerX
    private var y = centerY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        maxShadowDistance =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4f, this.resources.displayMetrics)
                .toInt()
        centerX = this.resources.displayMetrics.widthPixels / 2
        centerY = this.resources.displayMetrics.heightPixels / 2


        setContentView(R.layout.activity_a_hole_in_your_screen)
        hole_screen.setOnTouchListener(onScreenTouched)
    }

    private fun setShadowDistance(x: Int, y: Int) {
        val diameter = sqrt(Math.pow(centerX.toDouble(), 2.toDouble()) + Math.pow(centerY.toDouble(), 2.toDouble())).toInt()
        val halfDiameter = diameter / 2
        val distanceFromCenter = sqrt(
            Math.pow(x - centerX.toDouble(), 2.toDouble()) +
                Math.pow(y - centerY.toDouble(), 2.toDouble())
        )
        val shadowDistance = (distanceFromCenter / halfDiameter * maxShadowDistance).toFloat()
        var angle = Math.toDegrees(asin( abs(x - centerX) / distanceFromCenter))
        if(x == centerX && y == centerY) angle = 0.toDouble()
        else if(x >= centerX && y >= centerY) angle = 180 - angle
        else if(x <= centerX && y <= centerY) angle *= -1
        else if(x <= centerX && y >= centerY) angle += 180
        hole.setShadowInnerDistance(0, shadowDistance)
        hole.setShadowInnerAngle(0, angle.toFloat())
        hole.invalidate()
    }

    private var onScreenTouched = object: View.OnTouchListener {

        override fun onTouch(v: View?, event: MotionEvent?): Boolean {
            event.let {
                it!!
                if(it.action == MotionEvent.ACTION_DOWN || it.action == MotionEvent.ACTION_MOVE) {
                    x = it.getX(0).toInt()
                    y = it.getY(0).toInt()
                    setShadowDistance(x, y)
                    return true
                } else if(it.action == MotionEvent.ACTION_UP) {
                    x = centerX
                    y = centerY
                    setShadowDistance(x, y)
                    return true
                }
            }
            return false
        }

    }

}
