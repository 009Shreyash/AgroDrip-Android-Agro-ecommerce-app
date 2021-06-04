package com.agrodrip.adapter.farmsAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.databinding.TipsLayoutBinding
import com.agrodrip.model.TipsListData
import com.agrodrip.utils.Constants
import com.agrodrip.utils.Function
import com.agrodrip.utils.LocaleManager
import com.agrodrip.utils.Pref
import com.bumptech.glide.Glide


class TipsAdapter(val context: Context, var alist: ArrayList<TipsListData>, val itemClick: OnItemClick) :
    RecyclerView.Adapter<TipsAdapter.ViewHolder>() {


    interface OnItemClick {
        fun onItemClick(tipsListData: TipsListData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.tips_layout, parent, false)
        val binding: TipsLayoutBinding = DataBindingUtil.bind(v)!!
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
            holder.binding.txtTipsTitle.text = alist[position].pro_name_en
        } else {
            holder.binding.txtTipsTitle.text = alist[position].pro_name_gu
        }

        Glide.with(context)
            .load(alist[position].pro_image[0])
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .error(R.drawable.places_autocomplete_toolbar_shadow)
            .into(holder.binding.imgTips)


        holder.binding.root.setOnClickListener {
            itemClick.onItemClick(alist[position])
        }
    }


    class ViewHolder(mBinding: TipsLayoutBinding) : RecyclerView.ViewHolder(mBinding.root) {
        var binding: TipsLayoutBinding = mBinding

    }
}