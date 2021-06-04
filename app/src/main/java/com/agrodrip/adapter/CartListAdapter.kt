package com.agrodrip.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.agrodrip.R
import com.agrodrip.databinding.ItemCartListBinding
import com.agrodrip.model.CartListData
import com.agrodrip.utils.LocaleManager
import com.agrodrip.utils.Pref
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


class CartListAdapter(val context: Context, var alist: ArrayList<CartListData>, val itemClick: OnItemClick) :
    RecyclerView.Adapter<CartListAdapter.ViewHolder>() {


    interface OnItemClick {
        fun onItemClick(cartListData: CartListData, type: String, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v: View = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_list, parent, false)
        val binding: ItemCartListBinding = DataBindingUtil.bind(v)!!
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

    fun removeData(position: Int) {
        alist.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {


        if (LocaleManager.getLanguagePref(context) == LocaleManager.ENGLISH) {
            holder.binding.tvName.text = alist[position].product.pro_name_en
            holder.binding.tvQty.text = alist[position].qty
            holder.binding.tvProductPrice.text = context.getString(R.string.Rs) + " " + alist[position].product.price_gu
        } else {
            holder.binding.tvName.text = alist[position].product.pro_name_gu
            holder.binding.tvQty.text = alist[position].qty
            holder.binding.tvProductPrice.text = context.getString(R.string.Rs) + " " + alist[position].product.price_gu
        }

        Glide.with(context)
            .load(alist[position].product.pro_image[0])
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .error(R.drawable.places_autocomplete_toolbar_shadow)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
            .into(holder.binding.ivProduct)


        if (alist[position].qty.toInt() > 1) {
            holder.binding.ivMinusProduct.setOnClickListener {
                itemClick.onItemClick(alist[position], "minus", position)
            }
        }
        holder.binding.ivAddProduct.setOnClickListener {
            itemClick.onItemClick(alist[position], "add", position)
        }

        holder.binding.ivDeleteProduct.setOnClickListener {
            itemClick.onItemClick(alist[position], "delete", position)
        }
    }

    class ViewHolder(mBinding: ItemCartListBinding) : RecyclerView.ViewHolder(mBinding.root) {
        var binding: ItemCartListBinding = mBinding

    }
}