package com.agrodrip.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.agrodrip.R
import com.agrodrip.activity.MainActivity
import com.agrodrip.activity.SubDetailCategoryActivity
import com.agrodrip.activity.farm.SolutionProductActivity
import com.agrodrip.adapter.TopProductAdapter
import com.agrodrip.adapter.cropsAdapter.CropCategoryAdapter
import com.agrodrip.databinding.FragmentMarketPlaceBinding
import com.agrodrip.model.CropCategoryResponse
import com.agrodrip.model.TopProductData
import com.agrodrip.model.TopProductResponse
import com.agrodrip.utils.*
import com.agrodrip.utils.Function
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import com.agrodrip.webservices.toast
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MarketPlaceFragment : Fragment() {
    private lateinit var binding: FragmentMarketPlaceBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_market_place, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity?)!!.onHidingToolbar(false, resources.getString(R.string.market_place))
        (requireActivity() as MainActivity?)!!.bottomNavigation.visibility = View.VISIBLE

        initWidget()
    }

    private fun initWidget() {
        setTopProductAdapter()
        getCropCategoryList()
    }

      private fun setTopProductAdapter() {
        val mDialog = Utils.fcreateDialog(requireActivity())
        val call: Call<TopProductResponse>? = ApiHandler.getApiService(requireContext())?.topProductList()
        call?.enqueue(object : Callback<TopProductResponse> {
            override fun onResponse(call: Call<TopProductResponse>, response: Response<TopProductResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    val topProductAdapter = TopProductAdapter(requireContext(), resData.rows, "Market", object : TopProductAdapter.OnItemClick {
                        override fun onItemClick(topProductData: TopProductData) {
                            val intent = Intent(requireContext(), SolutionProductActivity::class.java)
                            intent.putExtra("topProductData", topProductData)
                            intent.putExtra("isFrom", "MarketPlaceFragment")
                            startActivity(intent)
                        }

                    })
                    val itemDecoration = ItemOffsetDecoration(context!!, R.dimen._2sdp)
                    binding.rvTopProduct.addItemDecoration(itemDecoration)
                    binding.rvTopProduct.adapter = topProductAdapter
                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<TopProductResponse>, t: Throwable) {
                mDialog.dismiss()
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    private fun getCropCategoryList() {
        val mDialog = Utils.fcreateDialog(requireActivity())
        val call: Call<CropCategoryResponse>? = ApiHandler.getApiService(requireContext())?.getCropCategoryList()
        call?.enqueue(object : Callback<CropCategoryResponse> {
            override fun onResponse(call: Call<CropCategoryResponse>, response: Response<CropCategoryResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    val categoryAdapter = CropCategoryAdapter(requireContext(), resData.rows, object : CropCategoryAdapter.GetCategoryListener {
                        override fun getCategoryId(categoryId: String) {
                            val intent = Intent(requireContext(), SubDetailCategoryActivity::class.java)
                            intent.putExtra("categoryId", categoryId)
                            startActivity(intent)
                        }
                    })
                    binding.productCategoryRecycler.adapter = categoryAdapter
                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<CropCategoryResponse>, t: Throwable) {
                mDialog.dismiss()
                requireContext().toast(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

}