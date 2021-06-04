package com.agrodrip.adapter.farmsAdapter

import android.content.Context
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.databinding.ProblemSolutionMainLayoutBinding
import com.agrodrip.model.FarmProblemListListData
import com.agrodrip.utils.LocaleManager
import com.bumptech.glide.Glide


class ProblemSolutionMainAdapter(
    val context: Context,
    var alist: ArrayList<FarmProblemListListData>,
    val itemClick: OnItemClick
) :
    RecyclerView.Adapter<ProblemSolutionMainAdapter.ViewHolder>() {


    interface OnItemClick {
        fun onItemClick(farmProblemListListData: FarmProblemListListData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.problem_solution_main_layout, parent, false)
        val binding: ProblemSolutionMainLayoutBinding = DataBindingUtil.bind(v)!!
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


        val description_en = Html.fromHtml(alist[position].description_en)
        val description_gu = Html.fromHtml(alist[position].description_gu)

        if (LocaleManager.getLanguagePref(context) == LocaleManager.ENGLISH) {
            holder.binding.txtProblemTitle.text = alist[position].title_en
            holder.binding.txtProblemDesc.text = description_en
        } else {
            holder.binding.txtProblemTitle.text = alist[position].title_gu
            holder.binding.txtProblemDesc.text = description_gu
        }

        Glide.with(context)
            .load(alist[position].problem_image)
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .error(R.drawable.places_autocomplete_toolbar_shadow)
            .into(holder.binding.imgCropProblem)


        holder.binding.root.setOnClickListener {
            itemClick.onItemClick(alist[position])
        }
    }

    class ViewHolder(mBinding: ProblemSolutionMainLayoutBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        var binding: ProblemSolutionMainLayoutBinding = mBinding

    }
}