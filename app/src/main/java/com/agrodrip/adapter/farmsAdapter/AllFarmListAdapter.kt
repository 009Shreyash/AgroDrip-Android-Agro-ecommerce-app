package com.agrodrip.adapter.farmsAdapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.databinding.ItemAllFarmListBinding
import com.agrodrip.model.MyFarmListData
import com.agrodrip.utils.Constants
import com.agrodrip.utils.Function
import com.agrodrip.utils.LocaleManager
import com.agrodrip.utils.Pref
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.agro_news_layout.view.*


class AllFarmListAdapter(val context: Context, var farmList: ArrayList<MyFarmListData>, val itemClick: OnItemClick) : RecyclerView.Adapter<AllFarmListAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_all_farm_list, parent, false)
        val binding: ItemAllFarmListBinding = DataBindingUtil.bind(v)!!
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return farmList.size
    }

    interface OnItemClick {
        fun onItemClick(myFarmListData: MyFarmListData, position: Int, s: String)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (LocaleManager.getLanguagePref(context) == LocaleManager.ENGLISH) {
            holder.binding.txtCropName.text = farmList[position].cropSubType.sub_name_en
        } else {
            holder.binding.txtCropName.text = farmList[position].cropSubType.sub_name_gu
        }

        holder.binding.txtSowingDate.text = context.getString(R.string.sowing_date) + " : " + farmList[position].sowing_date
        holder.binding.txtTotalArea.text = context.getString(R.string.area) + " " + farmList[position].area + " " + farmList[position].unit
        holder.binding.txtVillageName.text = farmList[position].farm_name

        Glide.with(context)
            .load(farmList[position].cropSubType.type_sub_image)
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .error(R.drawable.places_autocomplete_toolbar_shadow)
            .into(holder.binding.imgCrop)


        holder.binding.txtSowingDate.text = context.getString(R.string.sowing_date) + " : " + farmList[position].sowing_date
        holder.binding.txtTotalArea.text = context.getString(R.string.area) + " " + farmList[position].area + " " + farmList[position].unit
        holder.binding.txtVillageName.text = farmList[position].farm_name

        holder.binding.llMain.setOnClickListener {
            itemClick.onItemClick(farmList[position], position, "click")
        }

    }

    class ViewHolder(mBinding: ItemAllFarmListBinding) : RecyclerView.ViewHolder(mBinding.root) {
        var binding: ItemAllFarmListBinding = mBinding

    }

}