package com.sinaseyfi.advancedcardviewsample

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class ListAdapter(val context: Context): RecyclerView.Adapter<ListItem>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListItem {
        return ListItem(LayoutInflater.from(context).inflate(R.layout.list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return context.resources.getStringArray(R.array.list_items_title).size
    }

    override fun onBindViewHolder(holder: ListItem, position: Int) {
        val title = context.resources.getStringArray(R.array.list_items_title)[position]
        holder.title.text = title
        holder.title.setOnClickListener(View.OnClickListener {
            context.startActivity(getIntent(position))
        })
    }

    private fun getIntent(position: Int): Intent {
        val intent = Intent()
        when(position) {
            0 -> intent.setClass(context, AHoleInYourScreenActivity::class.java)
            1 -> intent.setClass(context, LiftItUpActivity::class.java)
            2 -> intent.setClass(context, RainbowActivity::class.java)
            3 -> intent.setClass(context, SomeCardsActivity::class.java)
            4 -> intent.setClass(context, CalculatorActivity::class.java)
        }
        return intent
    }

}
