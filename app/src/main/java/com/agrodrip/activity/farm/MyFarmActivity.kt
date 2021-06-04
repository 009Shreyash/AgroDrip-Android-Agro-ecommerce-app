package com.agrodrip.activity.farm

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.activity.SowingProcessActivity
import com.agrodrip.adapter.SowingProgressAdapter
import com.agrodrip.adapter.farmsAdapter.ProblemSolutionAdapter
import com.agrodrip.adapter.farmsAdapter.TipsAdapter
import com.agrodrip.databinding.ActivityMyFarmBinding
import com.agrodrip.model.*
import com.agrodrip.utils.Function
import com.agrodrip.utils.LocaleManager
import com.agrodrip.utils.Pref
import com.agrodrip.utils.Utils
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import com.agrodrip.webservices.toast
import com.bumptech.glide.Glide
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class MyFarmActivity : BaseActivity(), View.OnClickListener {

    private lateinit var binding: ActivityMyFarmBinding

    private lateinit var imgback: ImageView
    private lateinit var problemSolutionAdapter: ProblemSolutionAdapter
    private lateinit var tipsAdapter: TipsAdapter
    private lateinit var myFarmListData: MyFarmListData
    private lateinit var data: UserData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_farm)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.my_farm)
        imgback = toolbar.findViewById(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener(this)
        setSupportActionBar(toolbar)

        initWidget()

    }

    @SuppressLint("SetTextI18n")
    private fun initWidget() {

        data = Pref.getUserData(this)!!

        myFarmListData = intent.getParcelableExtra("myFarmListData") as MyFarmListData
        binding.txtViewProblems.setOnClickListener(this)
        binding.txtViewAllTips.setOnClickListener(this)

        Glide.with(this)
            .load(myFarmListData.cropSubType.type_sub_image)
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .error(R.drawable.places_autocomplete_toolbar_shadow)
            .into(binding.imgCrop)

        if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH){
            binding.txtCropName.text = myFarmListData.cropSubType.sub_name_en
        }else{
            binding.txtCropName.text = myFarmListData.cropSubType.sub_name_gu
        }

        binding.txtVillageName.text = myFarmListData.farm_name
        binding.txtTotalArea.text = myFarmListData.area + " " + myFarmListData.unit
        binding.txtSowingDate.text = Function.dateFormate(myFarmListData.sowing_date) + " " + getString(R.string.sowing_date)

        val adapter = SowingProgressAdapter(this, myFarmListData.cropSubType.stage,object : SowingProgressAdapter.OnClickListener{
            override fun onClick(data: ArrayList<Stage>) {
                val intent=Intent(this@MyFarmActivity, SowingProcessActivity::class.java)
                intent.putExtra("data",data)
                startActivity(intent)
            }
        })
        binding.rvSowingProcess.adapter = adapter
        setProblemsAdapter()
        setTipsAdapter()
    }

    private fun setTipsAdapter() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<TipsListResponse>? = ApiHandler.getApiService(this)?.getTipList(myFarmListData.cropType.id)
        call?.enqueue(object : Callback<TipsListResponse> {
            override fun onResponse(call: Call<TipsListResponse>, response: Response<TipsListResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    tipsAdapter = TipsAdapter(this@MyFarmActivity, resData.rows.tips, object : TipsAdapter.OnItemClick {
                        override fun onItemClick(tipsListData: TipsListData) {
                            val intent = Intent(this@MyFarmActivity, TipsViewActivity::class.java)
                            intent.putExtra("tipsListData", tipsListData)
                            startActivity(intent)
                        }

                    })
                    binding.cropTipsListRecycler.adapter = tipsAdapter
                } else {
                    if (response.code() == 400) {
                        mDialog.dismiss()
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<TipsListResponse>, t: Throwable) {
                mDialog.dismiss()
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })


    }

    private fun setProblemsAdapter() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<FarmProblemListResponse>? = ApiHandler.getApiService(this)?.getFarmProblemList(getParams())
        call?.enqueue(object : Callback<FarmProblemListResponse> {
            override fun onResponse(call: Call<FarmProblemListResponse>, response: Response<FarmProblemListResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    problemSolutionAdapter = ProblemSolutionAdapter(this@MyFarmActivity, resData.rows, object : ProblemSolutionAdapter.OnItemClick {
                        override fun onItemClick(farmProblemListListData: FarmProblemListListData) {
                            val intent = Intent(this@MyFarmActivity, CropProblemActivity::class.java)
                            intent.putExtra("farmProblemListListData", farmProblemListListData)
                            startActivity(intent)
                        }
                    })
                    binding.problemsListRecycler.adapter = problemSolutionAdapter
                } else {
                    if (response.code() == 400) {
                        mDialog.dismiss()
//                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<FarmProblemListResponse>, t: Throwable) {
                mDialog.dismiss()
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    private fun getParams(): HashMap<String, String> {
        val param = HashMap<String, String>()
        param["crop_type_id"] = myFarmListData.cropType.id
        param["crop_type_sub_id"] = myFarmListData.cropSubType.id
        param["problem_type"] ="0"
        return param
    }

    override fun onClick(v: View?) {
        when (v) {
            imgback -> {
                onBackPressed()
            }
            binding.txtViewProblems -> {
                startActivity(Intent(this@MyFarmActivity, ProblemSolutionListActivity::class.java))
            }
            binding.txtViewAllTips -> {
                startActivity(Intent(this@MyFarmActivity, TipsListActivity::class.java))
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.farm_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.action_delete -> {
                deletefarm(myFarmListData.id)
                true
            }

            R.id.action_Edit -> {
                val intent = Intent(this, AddFarmActivity::class.java)
                intent.putExtra("type", "edit")
                intent.putExtra("myFarmListData", myFarmListData)
                startActivityForResult(intent, 1234)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun deletefarm(id: String) {
        val mDialog: Dialog = Utils.fcreateDialog(this)
        val call: Call<CommonResponse>? =
            ApiHandler.getApiService(this)?.deleteFarmlist("Bearer " + data.app_key, id)
        call!!.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(
                call: Call<CommonResponse>,
                response: Response<CommonResponse>
            ) {
                if (response.isSuccessful) {
                    mDialog.dismiss()
//                    myFarfmListAdapter.removeData(position)
//                    if (myFarfmListAdapter.itemCount == 0) {
//                        binding.tvNoData.visibility = View.VISIBLE
//                    }
                    finish()
                    setResult(RESULT_OK)
                    toast(response.body()?.message!!)

                } else if (response.code() == 400) {
//                                                mDialog!!.dismiss()
                    try {
                        var jObjError: JSONObject? = null
                        try {
                            jObjError = JSONObject(
                                response.errorBody()!!.string()
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        toast(jObjError!!.getString("message"))

                    } catch (e: JSONException) {
                        e.printStackTrace()

                    }

                } else if (response.code() == 500) {
//                  mDialog!!.dismiss()
                    try {
                        var jObjError: JSONObject? = null
                        try {
                            jObjError = JSONObject(
                                response.errorBody()!!.string()
                            )
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                        toast(jObjError!!.getString("message"))
                    } catch (e: JSONException) {
                        e.printStackTrace()
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


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}