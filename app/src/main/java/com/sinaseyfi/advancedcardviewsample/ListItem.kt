package com.sinaseyfi.advancedcardviewsample

import android.view.View
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView

class ListItem(rootView: View): RecyclerView.ViewHolder(rootView) {
    val title = rootView.findViewById<AppCompatTextView>(R.id.item)
}