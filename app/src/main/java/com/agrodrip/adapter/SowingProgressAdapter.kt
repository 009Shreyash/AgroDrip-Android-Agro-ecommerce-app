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
import com.agrodrip.databinding.ItemSowingProcessBinding
import com.agrodrip.model.Stage

class SowingProgressAdapter(val context: Context, var list: ArrayList<Stage>,val onClickListener: OnClickListener) : RecyclerView.Adapter<SowingProgressAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_sowing_process, parent, false)
        val binding: ItemSowingProcessBinding = DataBindingUtil.bind(v)!!
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
        holder.binding.tvDateRange.text = list[position].from_day + "-" + list[position].to_day


        if (list[position].datediff.toInt() >= list[position].from_day.toInt()) {

            holder.binding.ivSowingProgress.setImageResource(R.drawable.ic_leaf_selected)
            holder.binding.tvDateRange.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark))


            if (position == 0) {
                holder.binding.viewSowingProgress.setBackgroundResource(R.drawable.bg_sowing_left_selected)
            } else if (position == itemCount - 1) {
                holder.binding.viewSowingProgress.setBackgroundResource(R.drawable.bg_sowing_right_selected)
                holder.binding.viewSowingProgressLast.visibility=View.GONE
            } else {
                holder.binding.viewSowingProgress.setBackgroundResource(R.drawable.bg_sowing_progress)
            }
        } else {
            holder.binding.ivSowingProgress.setImageResource(R.drawable.ic_leaf)
            holder.binding.tvDateRange.setTextColor(ContextCompat.getColor(context, R.color.Gray))

            if (position == 0) {
                holder.binding.viewSowingProgress.setBackgroundResource(R.drawable.bg_sowing_left)
            } else if (position == itemCount - 1) {
                holder.binding.viewSowingProgress.setBackgroundResource(R.drawable.bg_sowing_right)
                holder.binding.viewSowingProgressLast.visibility=View.GONE
            } else {
                holder.binding.viewSowingProgress.setBackgroundResource(R.drawable.bg_sowing_progress_disable)
            }
        }

        holder.binding.root.setOnClickListener{
            onClickListener.onClick(list)
        }

    }

    class ViewHolder(mBinding: ItemSowingProcessBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        var binding: ItemSowingProcessBinding = mBinding

    }

    interface OnClickListener{
        fun onClick(data: ArrayList<Stage>)
    }

}