package com.agrodrip.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agrodrip.R
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.image_slider_product.view.*


class ProductImageAdapter(val context: Context, var alist: ArrayList<String>) : SliderViewAdapter<ProductImageAdapter.SliderAdapterVH>() {
    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent!!.context).inflate(R.layout.image_slider_product, parent, false)
        return SliderAdapterVH(inflate)
    }

    override fun getCount(): Int {
        return alist.size
    }

    override fun onBindViewHolder(holder: SliderAdapterVH?, position: Int) {
        holder!!.binding(position)
    }

    inner class SliderAdapterVH(itemView: View) : ViewHolder(itemView) {
        fun binding(position: Int) {
            Glide.with(context)
                .load(alist[position])
                .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
                .error(R.drawable.places_autocomplete_toolbar_shadow)
                .into(itemView.iv_auto_image_product)
        }

    }

}