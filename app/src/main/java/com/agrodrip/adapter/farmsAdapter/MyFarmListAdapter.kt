package com.agrodrip.adapter.farmsAdapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agrodrip.R
import com.agrodrip.model.MyFarmListData
import com.agrodrip.utils.LocaleManager
import com.bumptech.glide.Glide
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.item_my_farm_list.view.*


class MyFarmListAdapter(
    val context: Context,
    val type: String,
    var farmList: ArrayList<MyFarmListData>,
    val itemClick: OnItemClick
) : SliderViewAdapter<MyFarmListAdapter.SliderAdapterVH>() {
    interface OnItemClick {
        fun onItemClick(myFarmListData: MyFarmListData, position: Int, s: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup?): SliderAdapterVH {
        val inflate: View = LayoutInflater.from(parent!!.context)
            .inflate(R.layout.item_my_farm_list, parent, false)
        return SliderAdapterVH(inflate)
    }

    override fun getCount(): Int {
        return farmList.size
    }

    override fun onBindViewHolder(holder: SliderAdapterVH?, position: Int) {
        holder!!.binding(position)
    }

    inner class SliderAdapterVH(itemView: View) : SliderViewAdapter.ViewHolder(itemView) {
        @SuppressLint("SetTextI18n")
        fun binding(position: Int) {

            if (LocaleManager.getLanguagePref(context) == LocaleManager.ENGLISH) {
                itemView.txtCropName.text = farmList[position].cropSubType.sub_name_en
            } else {
                itemView.txtCropName.text = farmList[position].cropSubType.sub_name_gu
            }

            itemView.txtSowingDate.text =
                context.getString(R.string.sowing_date) + " : " + farmList[position].sowing_date
            itemView.txtTotalArea.text =
                context.getString(R.string.area) + " " + farmList[position].area + " " + farmList[position].unit
            itemView.txtVillageName.text = farmList[position].farm_name

            Glide.with(context)
                .load(farmList[position].cropSubType.type_sub_image)
                .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
                .error(R.drawable.places_autocomplete_toolbar_shadow)
                .into(itemView.imgCrop)


            itemView.txtSowingDate.text =
                context.getString(R.string.sowing_date) + " : " + farmList[position].sowing_date
            itemView.txtTotalArea.text =
                context.getString(R.string.area) + " " + farmList[position].area + " " + farmList[position].unit
            itemView.txtVillageName.text = farmList[position].farm_name

            itemView.ll_main.setOnClickListener {
                itemClick.onItemClick(farmList[position], position, "click")
            }

        }

    }

}