package com.agrodrip.adapter.farmsAdapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.databinding.TipsMainLayoutBinding
import com.agrodrip.model.TipsListData
import com.agrodrip.utils.Constants
import com.agrodrip.utils.Function
import com.agrodrip.utils.LocaleManager
import com.agrodrip.utils.Pref
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


class TipsMainAdapter(val context: Context, var alist: ArrayList<TipsListData>, val itemClick: OnItemClick) :
    RecyclerView.Adapter<TipsMainAdapter.ViewHolder>() {


    interface OnItemClick {
        fun onItemClick(tipsListData: TipsListData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.tips_main_layout, parent, false)
        val binding: TipsMainLayoutBinding = DataBindingUtil.bind(v)!!
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

        val description_en = Html.fromHtml(alist[position].pro_description_en)
        val description_gu = Html.fromHtml(alist[position].pro_description_gu)

        if (LocaleManager.getLanguagePref(context) == LocaleManager.ENGLISH) {
            holder.binding.txtTipsTitle.text = alist[position].pro_name_en
            holder.binding.txtTipsDesc.text = description_en
        } else {
            holder.binding.txtTipsTitle.text = alist[position].pro_name_gu
            holder.binding.txtTipsDesc.text = description_gu
        }

        Glide.with(context)
            .load(alist[position].pro_image)
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .error(R.drawable.places_autocomplete_toolbar_shadow)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
            .into(holder.binding.tipsImg)

        holder.binding.root.setOnClickListener {
            itemClick.onItemClick(alist[position])
        }
    }


    class ViewHolder(mBinding: TipsMainLayoutBinding) : RecyclerView.ViewHolder(mBinding.
    root) {
        var binding: TipsMainLayoutBinding = mBinding

    }
}