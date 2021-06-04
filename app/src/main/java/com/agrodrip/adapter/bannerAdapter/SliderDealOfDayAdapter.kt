package com.agrodrip.adapter.bannerAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agrodrip.R
import com.agrodrip.model.BannerData
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.image_slider_deal_of_day.view.*


class SliderDealOfDayAdapter(val context: Context, var alist: ArrayList<BannerData>, val itemClick: OnItemClick) :
    SliderViewAdapter<SliderDealOfDayAdapter.SliderAdapterVH>() {

    interface OnItemClick {
        fun onItemClick(bannerData: BannerData)
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent!!.context).inflate(R.layout.image_slider_deal_of_day, parent, false)
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
                .load(alist[position].banner_image)
                .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
                .into(itemView.iv_auto_image_slider)

            itemView.parentFrame.setOnClickListener { itemClick.onItemClick(alist[position]) }
        }

    }

    fun renewItems(sliderItems: ArrayList<BannerData>) {
        this.alist = sliderItems
        notifyDataSetChanged()
    }
}