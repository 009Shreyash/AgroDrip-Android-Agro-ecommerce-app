package com.agrodrip.activity.address

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.databinding.ActivityAddAddressBinding
import com.agrodrip.model.AddressListData
import com.agrodrip.model.CommonResponse
import com.agrodrip.model.UserData
import com.agrodrip.utils.Pref
import com.agrodrip.utils.Utils
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import com.agrodrip.webservices.toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class AddAddressActivity : BaseActivity() {

    private lateinit var imgback: ImageView
    private lateinit var binding: ActivityAddAddressBinding
    private lateinit var data: UserData
    private lateinit var addressListData: AddressListData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_address)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.add_address)
        imgback = toolbar.findViewById(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener { onBackPressed() }
        setSupportActionBar(toolbar)

        initWidget()
    }

    private fun initWidget() {
        data = Pref.getUserData(this)!!
        binding.btnSave.setOnClickListener {
            if (binding.edtName.text.toString().isEmpty()) {
                binding.root.snackBar(getString(R.string.pls_enter_name))
            } else if (binding.edtAddress1.text.toString().isEmpty()) {
                binding.root.snackBar(getString(R.string.pls_enter_address_1))
            } else if (binding.edtFieldAddress2.text.toString().isEmpty()) {
                binding.root.snackBar(getString(R.string.pls_enter_address_2))
            } else if (binding.edtcity.text.toString().isEmpty()) {
                binding.root.snackBar(getString(R.string.pls_enter_city))
            } else if (binding.edtState.text.toString().isEmpty()) {
                binding.root.snackBar(getString(R.string.pls_enter_state))
            } else if (binding.edtPincode.text.toString().isEmpty()) {
                binding.root.snackBar(getString(R.string.pls_enter_pincode))
            } else {
                if (intent.getStringExtra("isFrom") == "edit") {
                    editAddress()
                } else {
                    addAddress()
                }
            }
        }

        if (intent.getStringExtra("isFrom") == "edit") {
            addressListData = intent.getParcelableExtra("addressListData") as AddressListData
            if (addressListData.full_name.isNotEmpty()) {
                binding.edtName.setText(addressListData.full_name)
            }

            if (addressListData.address_1.isNotEmpty()) {
                binding.edtAddress1.setText(addressListData.address_1)
            }

            if (addressListData.address_2.isNotEmpty()) {
                binding.edtFieldAddress2.setText(addressListData.address_2)
            }

            if (addressListData.city.isNotEmpty()) {
                binding.edtcity.setText(addressListData.city)
            }

            if (addressListData.state.isNotEmpty()) {
                binding.edtState.setText(addressListData.state)
            }
            if (!TextUtils.isEmpty(addressListData.pincode.toString())) {
                binding.edtPincode.setText(addressListData.pincode.toString())
            }
        }
    }

    private fun addAddress() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<CommonResponse>? = ApiHandler.getApiService(this)?.addAddress("Bearer " + data.app_key,getParams())
        call?.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                mDialog.dismiss()
                val resData: CommonResponse? = response.body()
                if (resData != null) {
                    Log.d("res", "res$resData")
                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    try {
//                        val mError: CommonResponse = GsonBuilder().create().fromJson(response.errorBody()!!.string(), CommonResponse::class.java)
                        Log.d("TAG", "onResponse: " + response.message())
                    } catch (e: IOException) {
                        // handle failure to read error
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                mDialog.dismiss()
                toast(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    private fun editAddress() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<CommonResponse>? = ApiHandler.getApiService(this)?.editAddressList("Bearer " + data.app_key, getEditParams())
        call?.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                mDialog.dismiss()
                val resData: CommonResponse? = response.body()
                if (resData != null) {
                    Log.d("res", "res$resData")
                    val intent = Intent()
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    Log.d("TAG", "onResponse: " + response.message())
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                mDialog.dismiss()
                toast(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    private fun getParams(): HashMap<String, String> {
        val param = HashMap<String, String>()
        param["state"] = binding.edtState.text.toString()
        param["city"] = binding.edtcity.text.toString()
        param["full_name"] = binding.edtName.text.toString()
        param["pincode"] = binding.edtPincode.text.toString()
        param["address_1"] = binding.edtAddress1.text.toString()
        param["address_2"] = binding.edtFieldAddress2.text.toString()
        return param
    }

    private fun getEditParams(): HashMap<String, String> {
        val param = HashMap<String, String>()
        param["state"] = binding.edtState.text.toString()
        param["city"] = binding.edtcity.text.toString()
        param["full_name"] = binding.edtName.text.toString()
        param["pincode"] = binding.edtPincode.text.toString()
        param["address_1"] = binding.edtAddress1.text.toString()
        param["address_2"] = binding.edtFieldAddress2.text.toString()
        param["address_id"] = addressListData.id.toString()
        return param
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}