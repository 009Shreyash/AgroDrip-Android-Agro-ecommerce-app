package com.agrodrip.activity.orders

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.activity.MainActivity
import com.agrodrip.adapter.OrdersListAdapter
import com.agrodrip.databinding.ActivityOrderListBinding
import com.agrodrip.model.OrderListData
import com.agrodrip.model.OrderListResponse
import com.agrodrip.model.UserData
import com.agrodrip.utils.Constants
import com.agrodrip.utils.Function
import com.agrodrip.utils.Pref
import com.agrodrip.utils.Utils
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderListActivity : BaseActivity() {

    private lateinit var binding: ActivityOrderListBinding
    private lateinit var imgback: ImageView
    private lateinit var ordersListAdapter: OrdersListAdapter
    private lateinit var data: UserData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_order_list)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.order_list)
        imgback = toolbar.findViewById(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener { finish() }
        setSupportActionBar(toolbar)
        data = Pref.getUserData(this)!!

        setAdapterOrderList()
    }

    private fun setAdapterOrderList() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<OrderListResponse>? = ApiHandler.getApiService(this)?.orderList("Bearer " + data.app_key)
        call?.enqueue(object : Callback<OrderListResponse> {
            override fun onResponse(call: Call<OrderListResponse>, response: Response<OrderListResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    ordersListAdapter = OrdersListAdapter(this@OrderListActivity, resData.row, object : OrdersListAdapter.OnItemClick {
                        override fun onItemClick(orderListData: OrderListData) {
                            val intent = Intent(this@OrderListActivity, OrderDetailsActivity::class.java)
                            intent.putExtra("orderListData", orderListData)
                            startActivity(intent)
                        }
                    })

                    binding.orderlistRecycler.adapter = ordersListAdapter
                } else {
                    if (response.code() == 400) {
                        mDialog.dismiss()
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<OrderListResponse>, t: Throwable) {
                mDialog.dismiss()
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}