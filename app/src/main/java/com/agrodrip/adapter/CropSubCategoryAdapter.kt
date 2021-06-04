package com.agrodrip.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.databinding.ItemSubCategoryBinding
import com.agrodrip.model.CropSubCategoryData
import com.agrodrip.utils.LocaleManager

class CropSubCategoryAdapter(
    val context: Context,
    var categoryCropList: ArrayList<CropSubCategoryData>,
    var getCategoryListener: GetCategoryListener
) : RecyclerView.Adapter<CropSubCategoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_sub_category, parent, false)
        val binding: ItemSubCategoryBinding = DataBindingUtil.bind(v)!!
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return categoryCropList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (LocaleManager.getLanguagePref(context) == LocaleManager.ENGLISH) {
            holder.binding.txtCategory.text = categoryCropList[position].sub_name_en
        } else {
            holder.binding.txtCategory.text = categoryCropList[position].sub_name_gu
        }

        holder.binding.root.setOnClickListener {
            getCategoryListener.getCategoryId(categoryCropList[position])
        }
    }

    class ViewHolder(mBinding: ItemSubCategoryBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        var binding: ItemSubCategoryBinding = mBinding

    }


    interface GetCategoryListener {
        fun getCategoryId(cropSubCategoryData: CropSubCategoryData)
    }
}