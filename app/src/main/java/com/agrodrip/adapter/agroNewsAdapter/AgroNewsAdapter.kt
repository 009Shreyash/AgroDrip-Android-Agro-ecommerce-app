package com.agrodrip.adapter.agroNewsAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.adapter.bannerAdapter.SliderDealOfDayAdapter
import com.agrodrip.databinding.AgroNewsLayoutBinding
import com.agrodrip.model.BannerData
import com.agrodrip.model.NewsData
import com.agrodrip.utils.Function
import com.agrodrip.utils.LocaleManager
import com.agrodrip.utils.Pref
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.agro_news_layout.view.*
import kotlinx.android.synthetic.main.image_slider_deal_of_day.view.*


class AgroNewsAdapter(val context: Context, var alist: ArrayList<NewsData>, val itemClick: OnItemClick)
    : SliderViewAdapter<AgroNewsAdapter.SliderAdapterVH>() {

    interface OnItemClick {
        fun onItemClick(newsData: NewsData)
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent!!.context).inflate(R.layout.agro_news_layout, parent, false)
        return SliderAdapterVH(inflate)
    }

    override fun getCount(): Int {
        return alist.size
    }

    override fun onBindViewHolder(holder: SliderAdapterVH?, position: Int) {
        holder!!.binding(position)
    }


    inner class SliderAdapterVH(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        fun binding(position: Int) {

            if (LocaleManager.getLanguagePref(context) == LocaleManager.ENGLISH) {
                itemView.txtNewArticle.text = alist[position].news_title_en
            } else {
                itemView.txtNewArticle.text = alist[position].news_title_gu
            }

            Glide.with(context)
                .load(alist[position].news_image)
                .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
                .error(R.drawable.places_autocomplete_toolbar_shadow)
                .into(itemView.imgNewArticle)

            itemView.parentLinear.setOnClickListener {
                itemClick.onItemClick(alist[position])
            }
        }

    }

    fun renewItems(newsItems: ArrayList<NewsData>) {
        this.alist = newsItems
        notifyDataSetChanged()
    }

}