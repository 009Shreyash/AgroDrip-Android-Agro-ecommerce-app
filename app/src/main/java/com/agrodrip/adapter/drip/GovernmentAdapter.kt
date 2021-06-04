package com.agrodrip.adapter.drip

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.databinding.GovSchemeLayoutBinding
import com.agrodrip.model.GovernmentSchemeData
import com.agrodrip.utils.LocaleManager
import com.bumptech.glide.Glide


class GovernmentAdapter(
    val context: Context,
    var alist: ArrayList<GovernmentSchemeData>,
    val itemClick: OnItemClick
) :
    RecyclerView.Adapter<GovernmentAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.gov_scheme_layout, parent, false)
        val binding: GovSchemeLayoutBinding = DataBindingUtil.bind(v)!!
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return alist.size
    }

    interface OnItemClick {
        fun onItemClick(governmentSchemeData: GovernmentSchemeData)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val description_en = Html.fromHtml(alist[position].description_en)
        val description_gu = Html.fromHtml(alist[position].description_gu)

        if (LocaleManager.getLanguagePref(context) == LocaleManager.ENGLISH) {
            holder.binding.txtGovSchemeTitle.text = alist[position].scheme_title_en
            holder.binding.txtGovSchemeDesc.text = description_en
        } else {
            holder.binding.txtGovSchemeTitle.text = alist[position].scheme_title_gu
            holder.binding.txtGovSchemeDesc.text = description_gu
        }

        Glide.with(context)
            .load(alist[position].scheme_image)
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .error(R.drawable.places_autocomplete_toolbar_shadow)
            .into(holder.binding.imgGovScheme)


        holder.binding.root.setOnClickListener {
            itemClick.onItemClick(alist[position])
        }
    }

    class ViewHolder(mBinding: GovSchemeLayoutBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        var binding: GovSchemeLayoutBinding = mBinding

    }
}