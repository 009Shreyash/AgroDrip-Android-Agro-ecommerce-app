package com.agrodrip.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.databinding.OrdersListLayoutBinding
import com.agrodrip.model.OrderListData
import com.agrodrip.utils.LocaleManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


class OrdersListAdapter(
    val context: Context,
    var alist: ArrayList<OrderListData>,
    val itemClick: OnItemClick
) : RecyclerView.Adapter<OrdersListAdapter.ViewHolder>() {


    interface OnItemClick {
        fun onItemClick(orderListData: OrderListData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View =
            LayoutInflater.from(parent.context).inflate(R.layout.orders_list_layout, parent, false)
        val binding: OrdersListLayoutBinding = DataBindingUtil.bind(v)!!
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

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (LocaleManager.getLanguagePref(context) == LocaleManager.ENGLISH) {
            holder.binding.txtproductName.text = alist[position].order_row[0].product.pro_name_en
        } else {
            holder.binding.txtproductName.text = alist[position].order_row[0].product.pro_name_gu
        }

        Glide.with(context)
            .load(alist[position].order_row[0].product.pro_image[0])
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .error(R.drawable.places_autocomplete_toolbar_shadow)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
            .into(holder.binding.imgOrder)

        holder.binding.txtproductQty.text =
            context.getString(R.string.quantity_txt) + " : " + alist[position].order_row[0].qty
        holder.binding.txtProductStatus.text = alist[position].status_name
        holder.binding.txtOrderDate.text = alist[position].order_date

        holder.binding.root.setOnClickListener {
            itemClick.onItemClick(alist[position])
        }
    }

    class ViewHolder(mBinding: OrdersListLayoutBinding) : RecyclerView.ViewHolder(mBinding.root) {
        var binding: OrdersListLayoutBinding = mBinding

    }
}