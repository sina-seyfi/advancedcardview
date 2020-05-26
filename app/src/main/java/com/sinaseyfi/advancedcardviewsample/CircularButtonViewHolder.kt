package com.sinaseyfi.advancedcardviewsample

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.sinaseyfi.advancedcardview.AdvancedCardView

class CircularButtonViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val button = itemView as AdvancedCardView
    val text = itemView.findViewById<AppCompatTextView>(R.id.text)
}