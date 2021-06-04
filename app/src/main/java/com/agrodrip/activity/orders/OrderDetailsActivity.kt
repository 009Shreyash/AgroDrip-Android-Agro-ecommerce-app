package com.agrodrip.activity.orders

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.activity.farm.SolutionProductActivity
import com.agrodrip.databinding.ActivityOrderDetailsBinding
import com.agrodrip.model.OrderListData
import com.agrodrip.utils.LocaleManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class OrderDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityOrderDetailsBinding
    private lateinit var imgback: ImageView
    private lateinit var orderListData: OrderListData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_details)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.order_details)
        imgback = toolbar.findViewById(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener { finish() }
        setSupportActionBar(toolbar)

        initWidget()
    }

    @SuppressLint("SetTextI18n")
    private fun initWidget() {
        orderListData = intent.getParcelableExtra("orderListData") as OrderListData
        binding.txtOrderDate.text = orderListData.order_date
        binding.txtOrderTotal.text = orderListData.total_amount
        binding.txtProductStatus.text = orderListData.status_name
        binding.txtOrderId.text = orderListData.order_no

        if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH) {
            binding.txtproductName.text = orderListData.order_row[0].product.pro_name_en
        } else {
            binding.txtproductName.text = orderListData.order_row[0].product.pro_name_gu
        }

        Glide.with(this)
            .load(orderListData.order_row[0].product.pro_image[0])
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .error(R.drawable.places_autocomplete_toolbar_shadow)
            .apply(RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE))
            .into(binding.imgOrder)

//        binding.tvPaymentMode.text = orderListData.payment_type
        binding.tvAddress.text = orderListData.address.address_1 + ", " + orderListData.address.address_2 + ", " +
                orderListData.address.city + ", " + orderListData.address.state + ", " + orderListData.address.pincode
        binding.txtproductQty.text = getString(R.string.quantity_txt)+ " : "  + orderListData.order_row[0].qty

        binding.llBuyItAgain.setOnClickListener{
            val intent = Intent(this@OrderDetailsActivity, SolutionProductActivity::class.java)
            intent.putExtra("orderListData", orderListData)
            intent.putExtra("isFrom", "OrderDetailsActivity")
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}