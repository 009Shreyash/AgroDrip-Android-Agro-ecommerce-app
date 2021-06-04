package com.agrodrip.adapter.bannerAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.databinding.DealBannerMainLayoutBinding
import com.agrodrip.model.BannerData
import com.bumptech.glide.Glide


class DealBannerMainAdapter(val context: Context, var bannerList: ArrayList<BannerData>, val itemClick: OnItemClick) : RecyclerView.Adapter<DealBannerMainAdapter.ViewHolder>() {
    interface OnItemClick {
        fun onItemClick(bannerData: BannerData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.deal_banner_main_layout, parent, false)
        val binding: DealBannerMainLayoutBinding = DataBindingUtil.bind(v)!!
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return bannerList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context)
            .load(bannerList[position].banner_image)
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .error(R.drawable.places_autocomplete_toolbar_shadow)
            .into(holder.binding.imgDealOfBanner)

        holder.binding.root.setOnClickListener {
            itemClick.onItemClick(bannerList[position])
        }
    }

    class ViewHolder(mBinding: DealBannerMainLayoutBinding) : RecyclerView.ViewHolder(mBinding.getRoot()) {
        var binding: DealBannerMainLayoutBinding = mBinding

    }


}