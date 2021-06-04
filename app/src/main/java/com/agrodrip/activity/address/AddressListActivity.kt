package com.agrodrip.activity.address

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.activity.OrderSuccessActivity
import com.agrodrip.adapter.AddressAdapter
import com.agrodrip.databinding.ActivityAddressListBinding
import com.agrodrip.model.AddressListData
import com.agrodrip.model.AddressListResponse
import com.agrodrip.model.CommonResponse
import com.agrodrip.model.UserData
import com.agrodrip.utils.Pref
import com.agrodrip.utils.Utils
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import kotlinx.android.synthetic.main.activity_address_list.*
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*


class AddressListActivity : BaseActivity(), View.OnClickListener {

    private var addressId: String = ""
    private lateinit var imgback: ImageView
    private lateinit var toolbarAddress: TextView
    private lateinit var addressAdapter: AddressAdapter
    private lateinit var binding: ActivityAddressListBinding
    private lateinit var data: UserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_address_list)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)


        if (intent.getStringExtra("isFrom") == "Main") {
            toolbarText.text = resources.getString(R.string.address)
            binding.btnSave.visibility = View.GONE
        } else {
            toolbarText.text = resources.getString(R.string.choose_address)
        }
        toolbarAddress = toolbar.findViewById(R.id.toolbarAddress)
        toolbarAddress.setOnClickListener(this)
        imgback = toolbar.findViewById(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener(this)
        setSupportActionBar(toolbar)

        initWidget()
    }

    private fun initWidget() {
        data = Pref.getUserData(this)!!
        imgback.setOnClickListener(this)
        binding.btnSave.setOnClickListener(this)

        getAddressList()
    }

    override fun onClick(v: View?) {
        when (v) {
            btnSave -> {
                if (addressId != "") {
                    dialogPlaceOrder()
                } else {
                    binding.root.snackBar(getString(R.string.select_address))
                }
            }
            imgback -> {
                onBackPressed()
            }
            toolbarAddress -> {
                val intent = Intent(this, AddAddressActivity::class.java)
                startActivityForResult(intent, 1001)
            }
        }

    }

    private fun dialogPlaceOrder() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_layout)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.WRAP_CONTENT)

        val textYes = dialog.findViewById(R.id.txtYes) as TextView
        val textNo = dialog.findViewById(R.id.txtNo) as TextView
        val tv_title = dialog.findViewById(R.id.tv_title) as TextView
        val tv_desc = dialog.findViewById(R.id.tv_desc) as TextView
        tv_title.text = getString(R.string.place_order)
        tv_desc.text = getString(R.string.orderplace_text)
        textYes.setOnClickListener {
            placeOrder(dialog)
        }

        textNo.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    private fun placeOrder(dialog: Dialog) {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<CommonResponse>? = ApiHandler.getApiService(this)?.placeOrder("Bearer " + data.app_key, getParams())
        call?.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    dialog.dismiss()
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    Handler().postDelayed({
                        startActivity(Intent(this@AddressListActivity, OrderSuccessActivity::class.java))
                        finish()
                    }, 1000)

                } else {
                    if (response.code() == 400) {
                        dialog.dismiss()
                        mDialog.dismiss()
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                dialog.dismiss()
                mDialog.dismiss()
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    private fun getParams(): HashMap<String, String> {
        val param = HashMap<String, String>()
        param["address_id"] = addressId
        param["payment_type"] = getString(R.string.cash_delivery)
        return param
    }


    private fun getAddressList() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<AddressListResponse>? = ApiHandler.getApiService(this)?.getAddressList("Bearer " + data.app_key)
        call?.enqueue(object : Callback<AddressListResponse> {
            override fun onResponse(call: Call<AddressListResponse>, response: Response<AddressListResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    if (resData.rows.size == 0) {
                        binding.tvNoData.visibility = View.VISIBLE
                        binding.addressListRecycler.visibility = View.INVISIBLE
                        if (intent.getStringExtra("isFrom") == "Main") {
                            binding.btnSave.visibility = View.INVISIBLE
                        }

                    } else {
                        binding.addressListRecycler.visibility = View.VISIBLE

                        if (intent.getStringExtra("isFrom") == "Main") {
                            binding.btnSave.visibility = View.INVISIBLE
                        } else binding.btnSave.visibility = View.VISIBLE

                        binding.tvNoData.visibility = View.INVISIBLE
                        addressAdapter = AddressAdapter(this@AddressListActivity, intent.getStringExtra("isFrom")!!, resData.rows, object :
                            AddressAdapter.OnItemClick {
                            override fun onItemClick(addressList: AddressListData, position: Int, type: String) {
                                if (type == "delete") {
                                    deleteAddressApi(addressList.id, position)
                                } else if (type == "click") {
                                    addressId = addressList.id.toString()
                                } else if (type == "edit") {
                                    val intent = Intent(this@AddressListActivity, AddAddressActivity::class.java)
                                    intent.putExtra("isFrom", "edit")
                                    intent.putExtra("addressListData", addressList)
                                    startActivity(intent)
                                }
                            }
                        })
                        binding.addressListRecycler.adapter = addressAdapter
                    }

                } else {
                    if (response.code() == 400) {
                        mDialog.dismiss()
                        binding.tvNoData.visibility = View.VISIBLE
                        binding.addressListRecycler.visibility = View.INVISIBLE
                        binding.btnSave.visibility = View.INVISIBLE
//                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<AddressListResponse>, t: Throwable) {
                mDialog.dismiss()
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })


    }
    private fun deleteAddressApi(id: Int, position: Int) {
        val call: Call<CommonResponse>? = ApiHandler.getApiService(this)
            ?.deleteAddresslist("Bearer " + data.app_key, id.toString())
        call!!.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.isSuccessful) {
                    addressAdapter.removeData(position)
                    if (addressAdapter.itemCount == 0) {
                        binding.tvNoData.visibility = View.VISIBLE
                    }
                    binding.root.snackBar(response.body()?.message!!)

                } else if (response.code() == 400) {
                    try {
                        var jObjError: JSONObject? = null
                        try {
                            jObjError = JSONObject(
                                response.errorBody()!!.string()
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        binding.root.snackBar(jObjError!!.getString("message"))

                    } catch (e: JSONException) {
                        e.printStackTrace()

                    }

                } else if (response.code() == 500) {
                    try {
                        var jObjError: JSONObject? = null
                        try {
                            jObjError = JSONObject(response.errorBody()!!.string())
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        binding.root.snackBar(jObjError!!.getString("message"))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }

                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            getAddressList()
        } 
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onResume() {
        super.onResume()
        getAddressList()
    }
}