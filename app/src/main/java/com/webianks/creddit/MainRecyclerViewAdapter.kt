package com.webianks.creddit

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

/**
 * Created by ramankit on 25/9/17.
 */

class MainRecyclerViewAdapter(var context: Context,
                              var transactionDataList: List<SingleTransaction>?) :
        RecyclerView.Adapter<MainRecyclerViewAdapter.VH>() {


    override fun onBindViewHolder(holder: VH?, position: Int) {

        holder?.paid?.text = "$"+transactionDataList?.get(position)?.amount
        holder?.to?.text = "paid to "+transactionDataList?.get(position)?.paid_to
    }

    override fun getItemCount(): Int {
        return transactionDataList?.size ?: 0
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): VH {
        val view = LayoutInflater.from(context).inflate(R.layout.single_transaction_layout, parent, false)
        return VH(view)
    }

    class VH(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        val paid = itemView?.findViewById(R.id.amount) as TextView
        val to = itemView?.findViewById(R.id.to) as TextView

    }
}
