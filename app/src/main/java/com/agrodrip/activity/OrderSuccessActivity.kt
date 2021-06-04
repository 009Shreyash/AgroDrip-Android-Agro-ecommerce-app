package com.agrodrip.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.activity.orders.OrderListActivity
import kotlinx.android.synthetic.main.activity_order_success.*

class OrderSuccessActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_success)

        txtViewStatus.setOnClickListener{
            startActivity(Intent(this, OrderListActivity::class.java))
            finish()
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}