package com.sinaseyfi.advancedcardviewsample

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class CalculatorButtonListAdapter: RecyclerView.Adapter<CircularButtonViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircularButtonViewHolder {
        return CircularButtonViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.circular_button_item, parent, false))
    }

    override fun getItemCount(): Int {
        return 16
    }

    override fun onBindViewHolder(holder: CircularButtonViewHolder, position: Int) {
//        val layoutParams = holder.button.layoutParams
//        layoutParams.height = layoutParams.width
//        holder.button.layoutParams = layoutParams
        val shadowColor = getShadowColor(position)
        holder.button.setShadowOuterAlpha(0, getAlpha(position))
        holder.button.setShadowOuterColor(0, shadowColor)
        holder.button.background_Color = getBackgroundColor(position)
        holder.text.text = getCalculatorButtonText(position)
        holder.text.setTextColor(getTextColor(position))
    }

    private fun getColumnNum(position: Int): Int {
        return (position % 4) + 1
    }

    private fun getRowNum(position: Int): Int {
        return (position / 4) + 1
    }

    private fun getTextColor(position: Int): Int {
        val columnNum = getColumnNum(position)
        val rowNum = getRowNum(position)
        return if(columnNum == 4)
                Color.parseColor("#D51B1B")
            else if(columnNum == 3 && rowNum == 4)
                Color.parseColor("#FFFFFF")
            else
                Color.parseColor("#2C4EB3")
    }

    private fun getBackgroundColor(position: Int): Int {
        val columnNum = getColumnNum(position)
        val rowNum = getRowNum(position)
        if(rowNum == 4 && columnNum == 3)
            return Color.parseColor("#2C4EB3")
        else
            return Color.parseColor("#F0F5F7")
    }

    private fun getAlpha(position: Int): Float {
        val columnNum = getColumnNum(position)
        return if(columnNum == 4)
            0.08f
        else
            0.08f
    }

    private fun getShadowColor(position: Int): Int {
        val columnNum = getColumnNum(position)
        return if(columnNum == 4)
            Color.parseColor("#D51B1B")
        else
            Color.parseColor("#2C4EB3")
    }

    private fun getCalculatorButtonText(position: Int): String {
        val columnNum = getColumnNum(position)
        val rowNum = getRowNum(position)
        if(columnNum == 4) {
            return when(rowNum) {
                1 -> "+"
                2 -> "-"
                3 -> "x"
                4 -> "/"
                else -> ""
            }
        }
        if(rowNum == 4) {
            return when(columnNum) {
                1 -> "."
                2 -> "0"
                3 -> "="
                else -> ""
            }
        }
        return ((rowNum - 1) * 3 + columnNum).toString()
    }

}