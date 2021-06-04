package com.agrodrip.adapter.farmsAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.databinding.SolutionLayoutBinding
import com.agrodrip.model.SolutionList
import com.agrodrip.utils.LocaleManager
import com.bumptech.glide.Glide

class SolutionAdapter(
    val context: Context,
    var alist: ArrayList<SolutionList>,
    val itemClick: OnItemClick
) :
    RecyclerView.Adapter<SolutionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.solution_layout, parent, false)
        val binding: SolutionLayoutBinding = DataBindingUtil.bind(v)!!
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return alist.size
    }

    interface OnItemClick {
        fun onItemClick(solutionList: SolutionList)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (LocaleManager.getLanguagePref(context) == LocaleManager.ENGLISH) {
            holder.binding.txtSolutionName.text = alist[position].pro_name_en
        } else {
            holder.binding.txtSolutionName.text = alist[position].pro_name_gu
        }

        Glide.with(context)
            .load(alist[position].pro_image)
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .error(R.drawable.places_autocomplete_toolbar_shadow)
            .into(holder.binding.imgSolution)


        holder.binding.root.setOnClickListener {
            itemClick.onItemClick(alist[position])
        }
    }

    class ViewHolder(mBinding: SolutionLayoutBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        var binding: SolutionLayoutBinding = mBinding

    }

}