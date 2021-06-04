package com.agrodrip.adapter.cropsAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.databinding.CropCategoryLayoutBinding
import com.agrodrip.model.CropCategoryData
import com.agrodrip.utils.LocaleManager

class CropCategoryAdapter(
    val context: Context,
    var categoryCropList: ArrayList<CropCategoryData>,
    var getCategoryListener: GetCategoryListener
) : RecyclerView.Adapter<CropCategoryAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.crop_category_layout, parent, false)
        val binding: CropCategoryLayoutBinding = DataBindingUtil.bind(v)!!
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
            holder.binding.txtCategory.text = categoryCropList[position].description_en
        } else {
            holder.binding.txtCategory.text = categoryCropList[position].description_gu
        }

        holder.binding.root.setOnClickListener {
            getCategoryListener.getCategoryId(categoryCropList[position].id)
        }

    }

    class ViewHolder(mBinding: CropCategoryLayoutBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        var binding: CropCategoryLayoutBinding = mBinding

    }


    interface GetCategoryListener {
        fun getCategoryId(categoryId: String)
    }
}