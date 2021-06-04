package com.agrodrip.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.databinding.ItemSubCategoryBinding
import com.agrodrip.model.UnitRow


class UnitSelectAdapter(val context: Context, var alist: ArrayList<UnitRow>, val itemClick: OnItemClick) :
    RecyclerView.Adapter<UnitSelectAdapter.ViewHolder>() {

    private var selectPosition: Int = -1

    interface OnItemClick {
        fun onItemClick(unitRow: UnitRow)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_sub_category, parent, false)
        val binding: ItemSubCategoryBinding = DataBindingUtil.bind(v)!!
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return alist.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//
//        if (Pref.getValue(context, Constants.PREF_SELECTED_LANGUAGE, "").toString().isNotEmpty()) {
//            val language = Pref.getValue(context, Constants.PREF_SELECTED_LANGUAGE, "").toString()
//            if (language == Constants.PREF_ENGLISH) {
//                Function.setLocale(context, Constants.PREF_ENGLISH)
//                holder.binding.txtCategory.text = alist[position].unit_name_en
//            } else {
//                Function.setLocale(context, Constants.PREF_GUJARATI)
//                holder.binding.txtCategory.text = alist[position].unit_name_gu
//            }
//        }
        holder.binding.txtCategory.text = alist[position].unit_name_en
        if (selectPosition == position) {
            holder.binding.llBg.background = ContextCompat.getDrawable(context, R.drawable.rounded_package_select_bg)
            holder.binding.txtCategory.setTextColor(ContextCompat.getColor(context, android.R.color.white))
        } else {
            holder.binding.llBg.background = ContextCompat.getDrawable(context, R.drawable.btn_border)
            holder.binding.txtCategory.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
        }

        holder.binding.root.setOnClickListener {
            itemClick.onItemClick(alist[position])
            selectPosition = position
            notifyDataSetChanged()
        }
    }

    class ViewHolder(mBinding: ItemSubCategoryBinding) : RecyclerView.ViewHolder(mBinding.root) {
        var binding: ItemSubCategoryBinding = mBinding

    }
}