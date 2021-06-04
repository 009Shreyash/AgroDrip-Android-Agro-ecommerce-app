package com.agrodrip.adapter.cropsAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.databinding.CropLayoutBinding
import com.agrodrip.model.CropSubCategoryData
import com.agrodrip.utils.LocaleManager
import com.bumptech.glide.Glide

class CropsAdapter(
    val context: Context,
    var list: ArrayList<CropSubCategoryData>,
    val getSubCategoryListener: GetSubCategoryListener
) : RecyclerView.Adapter<CropsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.crop_layout, parent, false)
        val binding: CropLayoutBinding = DataBindingUtil.bind(v)!!
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

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (LocaleManager.getLanguagePref(context) == LocaleManager.ENGLISH) {
            holder.binding.txtCropName.text = list[position].sub_name_en
        } else {
            holder.binding.txtCropName.text = list[position].sub_name_gu
        }

        Glide.with(context)
            .load(list[position].sub_image)
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .error(R.drawable.places_autocomplete_toolbar_shadow)
            .into(holder.binding.imgCrop)


        holder.binding.root.setOnClickListener {
            getSubCategoryListener.getSubCategoryId(list[position])
        }
    }

    class ViewHolder(mBinding: CropLayoutBinding) : RecyclerView.ViewHolder(mBinding.root) {
        var binding: CropLayoutBinding = mBinding

    }

    interface GetSubCategoryListener {
        fun getSubCategoryId(cropSubCategoryData: CropSubCategoryData)
    }
}