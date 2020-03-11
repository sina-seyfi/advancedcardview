package com.sinaseyfi.advancedcardviewsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        list.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        list.adapter = ListAdapter(this)
        sampleAlgorithm(intArrayOf(1, 2, 3, 4))
    }

    fun sampleAlgorithm(input: IntArray) {
        var left = IntArray(input.size, {x -> 1})
        var right = IntArray(input.size, {x -> 1})
        var output = IntArray(input.size)
        for(i in 1 until input.size) {
            left[i] = left[i - 1] * input[i - 1]
        }
        for(i in input.size - 2 downTo 0) {
            right[i] = right[i + 1] * input[i + 1]
        }
        for(i in input.indices) {
            output[i] = left[i] * right[i]
        }
        for(j in output) {
            Log.d("TAG",  j.toString() + ", ")
        }
    }

}
