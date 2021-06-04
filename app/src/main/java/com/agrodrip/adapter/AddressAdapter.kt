package com.agrodrip.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.databinding.AddressLayoutBinding
import com.agrodrip.model.AddressListData

class AddressAdapter(val context: Context, val type: String, var addressList: ArrayList<AddressListData>, val itemClick: OnItemClick) : RecyclerView.Adapter<AddressAdapter.ViewHolder>() {
    private var mPosition: Int = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.address_layout, parent, false)
        val binding: AddressLayoutBinding = DataBindingUtil.bind(v)!!
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return addressList.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun removeData(position: Int) {
        addressList.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.binding.txtName.text = addressList[position].full_name
        holder.binding.txtAddress.text = addressList[position].address_1 + "," + addressList[position].address_2 + "," +
                addressList[position].city + "," + addressList[position].state + "," + addressList[position].pincode
        holder.binding.imgDelete.setOnClickListener {
            itemClick.onItemClick(addressList[position], position, "delete")
        }
        if (mPosition == position) {
//            holder.binding.ivSelect.visibility = View.VISIBLE
            val backgroundTint =
                AppCompatResources.getColorStateList(context, R.color.colorPrimaryDark)!!
            ViewCompat.setBackgroundTintList(holder.binding.parentRelative, backgroundTint)
        } else {
//            holder.binding.ivSelect.visibility = View.GONE
            val backgroundTint =
                AppCompatResources.getColorStateList(context, R.color.colorPrimary)!!
            ViewCompat.setBackgroundTintList(holder.binding.parentRelative, backgroundTint)
        }

        if (type == "Cart") {
            holder.binding.root.setOnClickListener {
                itemClick.onItemClick(addressList[position], position, "click")
                mPosition = position
                notifyDataSetChanged()
            }
        }

        holder.binding.ivEdit.setOnClickListener {
            itemClick.onItemClick(addressList[position], position, "edit")
        }

    }

    class ViewHolder(mBinding: AddressLayoutBinding) : RecyclerView.ViewHolder(mBinding.root) {
        var binding: AddressLayoutBinding = mBinding
    }

    interface OnItemClick {
        fun onItemClick(addressList: AddressListData, position: Int, type: String)
    }

}