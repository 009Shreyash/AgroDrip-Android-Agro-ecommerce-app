package com.agrodrip.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.databinding.ItemSpecialProductsHomeBinding
import com.agrodrip.databinding.ItemTopProductBinding
import com.agrodrip.model.TopProductData
import com.agrodrip.utils.Constants
import com.agrodrip.utils.Function
import com.agrodrip.utils.LocaleManager
import com.agrodrip.utils.Pref
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


class TopProductHomeAdapter(val context: Context, var alist: ArrayList<TopProductData>, var comeFrom: String, val itemClick: OnItemClick) :
    RecyclerView.Adapter<TopProductHomeAdapter.ViewHolder>() {


    interface OnItemClick {
        fun onItemClick(topProductData: TopProductData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_special_products_home, parent, false)
        val binding: ItemSpecialProductsHomeBinding = DataBindingUtil.bind(v)!!
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

        if (LocaleManager.getLanguagePref(context) == LocaleManager.ENGLISH) {
            holder.binding.tvProductName.text = alist[position].pro_name_en
        } else {
            holder.binding.tvProductName.text = alist[position].pro_name_gu
        }

        Glide.with(context)
            .load(alist[position].pro_image[0])
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .error(R.drawable.places_autocomplete_toolbar_shadow)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
            .into(holder.binding.ivProduct)

        holder.binding.root.setOnClickListener {
            itemClick.onItemClick(alist[position])
        }
    }

    class ViewHolder(mBinding: ItemSpecialProductsHomeBinding) : RecyclerView.ViewHolder(mBinding.root) {
        var binding: ItemSpecialProductsHomeBinding = mBinding

    }
}