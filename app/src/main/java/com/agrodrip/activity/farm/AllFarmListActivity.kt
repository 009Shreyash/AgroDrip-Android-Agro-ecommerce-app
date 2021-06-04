package com.agrodrip.activity.farm

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
import com.agrodrip.adapter.farmsAdapter.AllFarmListAdapter
import com.agrodrip.databinding.ActivityAllFarmListBinding
import com.agrodrip.model.MyFarmListData
import com.agrodrip.model.MyFarmListResponse
import com.agrodrip.model.UserData
import com.agrodrip.utils.Pref
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllFarmListActivity : BaseActivity() {

    private lateinit var imgback: ImageView
    private lateinit var binding: ActivityAllFarmListBinding
    private lateinit var data: UserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_all_farm_list)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.my_farm)
        imgback = toolbar.findViewById<ImageView>(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener {
            finish()
        }
        setSupportActionBar(toolbar)

        data = Pref.getUserData(this)!!

        getMyFarmList()

    }


    private fun getMyFarmList() {
        val call: Call<MyFarmListResponse>? = ApiHandler.getApiService(this)?.getFarm("Bearer " + data.app_key)
        call?.enqueue(object : Callback<MyFarmListResponse> {
            override fun onResponse(call: Call<MyFarmListResponse>, response: Response<MyFarmListResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    binding.rvFarmList.visibility = View.VISIBLE
                    binding.addFarmLayout.visibility = View.GONE
                    Log.d("getMyFarmList", "res$resData")
                    val myFarfmListAdapter = AllFarmListAdapter(this@AllFarmListActivity, resData.rows,
                        object : AllFarmListAdapter.OnItemClick {
                            override fun onItemClick(myFarmListData: MyFarmListData, position: Int, s: String) {
                                val intent = Intent(this@AllFarmListActivity, MyFarmActivity::class.java)
                                intent.putExtra("myFarmListData", myFarmListData)
                                startActivity(intent)
                            }


                        })


                    binding.rvFarmList.adapter = myFarfmListAdapter

                } else {
                    if (response.code() == 400) {
                        binding.rvFarmList.visibility = View.GONE
                        binding.addFarmLayout.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MyFarmListResponse>, t: Throwable) {
                binding.rvFarmList.visibility = View.GONE
                binding.addFarmLayout.visibility = View.VISIBLE
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }


    override fun onBackPressed() {
        super.onBackPressed()

        finish()
    }
}