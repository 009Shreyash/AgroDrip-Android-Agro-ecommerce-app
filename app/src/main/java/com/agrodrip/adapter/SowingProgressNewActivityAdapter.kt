package com.agrodrip.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.databinding.ItemSowingProcessStrightBinding
import com.agrodrip.model.Stage
import com.agrodrip.utils.LocaleManager

class SowingProgressNewActivityAdapter(val context: Context, var list: ArrayList<Stage>) : RecyclerView.Adapter<SowingProgressNewActivityAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_sowing_process_stright, parent, false)
        val binding: ItemSowingProcessStrightBinding = DataBindingUtil.bind(v)!!
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.tvDateRange.text = list[position].from_day + " to " + list[position].to_day + " days"



        if (LocaleManager.getLanguagePref(context) == LocaleManager.ENGLISH) {
            holder.binding.tvSowing.text = list[position].day_status_en
        } else {
            holder.binding.tvSowing.text = list[position].day_status_gu
        }
        if (list[position].datediff.toInt() >= list[position].from_day.toInt()) {
            holder.binding.ivSowingProgress.setImageResource(R.drawable.ic_leaf_selected)
            holder.binding.tvDateRange.setTextColor(ContextCompat.getColor(context, R.color.white))
            holder.binding.tvDateRange.background = ContextCompat.getDrawable(context, R.color.colorPrimaryDark)

        } else {
            holder.binding.ivSowingProgress.setImageResource(R.drawable.ic_leaf)
            holder.binding.tvDateRange.setTextColor(ContextCompat.getColor(context, R.color.lightGray))
            holder.binding.tvDateRange.background = ContextCompat.getDrawable(context, R.color.white)
        }

    }

    class ViewHolder(mBinding: ItemSowingProcessStrightBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        var binding: ItemSowingProcessStrightBinding = mBinding

    }


}