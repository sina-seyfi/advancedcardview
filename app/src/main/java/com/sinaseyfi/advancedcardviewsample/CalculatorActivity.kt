package com.sinaseyfi.advancedcardviewsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_calculator.*

class CalculatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)
        buttons_list.adapter = CalculatorButtonListAdapter()
        buttons_list.layoutManager = GridLayoutManager(applicationContext, 4)
    }
}
