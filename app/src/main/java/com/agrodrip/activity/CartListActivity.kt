package com.agrodrip.activity

import android.annotation.SuppressLint
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
import com.agrodrip.activity.address.AddressListActivity
import com.agrodrip.adapter.CartListAdapter
import com.agrodrip.databinding.ActivityCartListBinding
import com.agrodrip.model.CartListData
import com.agrodrip.model.CartListResponse
import com.agrodrip.model.CommonResponse
import com.agrodrip.model.UserData
import com.agrodrip.utils.Constants
import com.agrodrip.utils.Pref
import com.agrodrip.utils.Utils
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class CartListActivity : BaseActivity() {
    private lateinit var cartListAdapter: CartListAdapter
    private lateinit var toolbarText: TextView
    private lateinit var binding: ActivityCartListBinding
    private lateinit var imgback: ImageView
    private lateinit var data: UserData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cart_list)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        toolbarText = toolbar.findViewById(R.id.toolbarText)
        toolbarText.text = getString(R.string.title_cart_list)
        imgback = toolbar.findViewById(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener { onBackPressed() }
        data = Pref.getUserData(this)!!


        getCartList()

        binding.llPlaceOrder.setOnClickListener {
            val intent=Intent(this, AddressListActivity::class.java)
            intent.putExtra("isFrom","Cart")
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun getCartList() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<CartListResponse>? = ApiHandler.getApiService(this)?.getCartList("Bearer " + data.app_key)
        call?.enqueue(object : Callback<CartListResponse> {
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<CartListResponse>, response: Response<CartListResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    if (resData.code == 400) {
                        binding.llEmptyCart.visibility = View.VISIBLE
                        binding.rvCartList.visibility = View.GONE
                        binding.llPlace.visibility = View.GONE
                    } else {
                        Pref.setValue(this@CartListActivity, Constants.PREF_BADGE, resData.row.size.toString())
                        if (!TextUtils.isEmpty(resData.total_amount))
                            binding.tvTotalAmount.text = getString(R.string.total_amount_txt) + getString(R.string.Rs) + resData.total_amount
                        cartListAdapter = CartListAdapter(this@CartListActivity, resData.row, object :
                            CartListAdapter.OnItemClick {
                            override fun onItemClick(cartListData: CartListData, type: String, position: Int) {
                                if (type == "add") {
                                    updateCartItem(cartListData.product.id, cartListData.product.unit_id,position)
                                } else if (type == "minus") {
                                    deleteCartItem(cartListData.product.id, position)
                                } else if (type == "delete") {
                                    deleteWholeProduct(cartListData.product.id, position)
                                }
                            }

                        })
                        binding.rvCartList.adapter = cartListAdapter
                    }

                } else {
                    mDialog.dismiss()
                    if (response.code() == 400) {
                        binding.llEmptyCart.visibility = View.VISIBLE
                        binding.rvCartList.visibility = View.GONE
                        binding.llPlace.visibility = View.GONE
                    }
                }
            }

            override fun onFailure(call: Call<CartListResponse>, t: Throwable) {
                mDialog.dismiss()
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    private fun updateCartItem(id: String,unit_id:String, position: Int) {
        val call: Call<CommonResponse>? = ApiHandler.getApiService(this)?.addProduct("Bearer " + data.app_key, id,unit_id)
        call!!.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.isSuccessful) {
                    if (cartListAdapter.itemCount == 0) {
                        binding.llEmptyCart.visibility = View.VISIBLE
                    }
                    getCartList()
                    binding.root.snackBar(response.body()?.message!!)
                } else if (response.code() == 400) {
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

    private fun deleteCartItem(id: String, position: Int) {
        val call: Call<CommonResponse>? = ApiHandler.getApiService(this)?.removeProduct("Bearer " + data.app_key, id)
        call!!.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.isSuccessful) {
                    cartListAdapter.removeData(position)
                    if (cartListAdapter.itemCount == 0) {
                        binding.llEmptyCart.visibility = View.VISIBLE
                    }
                    binding.root.snackBar(response.body()?.message!!)
                    getCartList()
                } else if (response.code() == 400) {
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

    private fun deleteWholeProduct(id: String, position: Int) {
        val call: Call<CommonResponse>? = ApiHandler.getApiService(this)?.deleteProduct("Bearer " + data.app_key, id)
        call!!.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                if (response.isSuccessful) {
                    getCartList()
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
}