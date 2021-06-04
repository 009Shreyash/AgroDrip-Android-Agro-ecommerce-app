package com.agrodrip.adapter.farmsAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.databinding.ProblemSolutionLayoutBinding
import com.agrodrip.model.FarmProblemListListData
import com.agrodrip.utils.LocaleManager
import com.bumptech.glide.Glide


class ProblemSolutionAdapter(
    val context: Context,
    var alist: ArrayList<FarmProblemListListData>,
    val itemClick: OnItemClick
) :
    RecyclerView.Adapter<ProblemSolutionAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.problem_solution_layout, parent, false)
        val binding: ProblemSolutionLayoutBinding = DataBindingUtil.bind(v)!!
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return alist.size
    }

    interface OnItemClick {
        fun onItemClick(farmProblemListListData: FarmProblemListListData)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (LocaleManager.getLanguagePref(context) == LocaleManager.ENGLISH) {
            holder.binding.txtProblemsTitle.text = alist[position].title_en
        } else {
            holder.binding.txtProblemsTitle.text = alist[position].title_gu
        }

        Glide.with(context)
            .load(alist[position].problem_image)
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .error(R.drawable.places_autocomplete_toolbar_shadow)
            .into(holder.binding.imgCropProblems)


        holder.binding.root.setOnClickListener {
            itemClick.onItemClick(alist[position])
        }
    }

    class ViewHolder(mBinding: ProblemSolutionLayoutBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        var binding: ProblemSolutionLayoutBinding = mBinding

    }
}