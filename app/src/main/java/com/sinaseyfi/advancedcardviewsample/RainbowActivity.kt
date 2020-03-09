package com.sinaseyfi.advancedcardviewsample

import android.animation.Animator
import android.animation.ValueAnimator
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import com.google.android.material.animation.AnimationUtils
import kotlinx.android.synthetic.main.activity_rainbow.*

class RainbowActivity : AppCompatActivity() {

    val hue0 = Color.rgb(255, 26, 26)
    val hue15 = Color.rgb(255, 83, 26)
    val hue30 = Color.rgb(255, 128, 0)
    val hue45 = Color.rgb(255, 204, 51)
    val hue60 = Color.rgb(230, 230, 46)
    val hue75 = Color.rgb(184, 230, 46)
    val hue90 = Color.rgb(135, 217, 54)
    val hue105 = Color.rgb(103, 217, 65)
    val hue120 = Color.rgb(61, 204, 61)
    val hue135 = Color.rgb(41, 204, 82)
    val hue150 = Color.rgb(33, 217, 125)
    val hue165 = Color.rgb(22, 217, 168)
    val hue180 = Color.rgb(11, 217, 217)
    val hue195 = Color.rgb(11, 175, 230)
    val hue210 = Color.rgb(24, 133, 242)
    val hue225 = Color.rgb(38, 92, 255)
    val hue240 = Color.rgb(64, 64, 255)
    val hue255 = Color.rgb(102, 51, 255)
    val hue270 = Color.rgb(147, 38, 255)
    val hue285 = Color.rgb(188, 24, 242)
    val hue300 = Color.rgb(230, 23, 230)
    val hue315 = Color.rgb(217, 33, 171)

    val colorArray = intArrayOf(
        hue0,
        hue15,
        hue30,
        hue45,
        hue60,
        hue75,
        hue90,
        hue105,
        hue120,
        hue135,
        hue150,
        hue165,
        hue180,
        hue195,
        hue210,
        hue225,
        hue240,
        hue255,
        hue270,
        hue285,
        hue300,
        hue315,
        hue0
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rainbow)
        rainbow.stroke_Gradient_Colors = colorArray
        rotate_it.setOnClickListener(rotateItListener)
    }

    private val rotateItListener = object: View.OnClickListener {
        override fun onClick(v: View?) {
            val valueAnimator = ValueAnimator.ofFloat(0f, 360f * 3)
            valueAnimator.apply {
                duration = 2000
            }
            valueAnimator.addUpdateListener {
                rainbow.stroke_Gradient_Angle = it.animatedValue as Float
                rainbow.invalidate()
            }
            valueAnimator.addListener(object: Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onAnimationEnd(animation: Animator?) {
                    rotate_it.visibility = View.VISIBLE
                }

                override fun onAnimationStart(animation: Animator?) {
                    rotate_it.visibility = View.INVISIBLE
                }

                override fun onAnimationCancel(animation: Animator?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

            })
            valueAnimator.start()
        }
    }

}
