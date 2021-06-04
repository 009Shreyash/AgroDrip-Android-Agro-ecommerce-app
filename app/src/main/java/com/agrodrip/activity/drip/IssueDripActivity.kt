package com.agrodrip.activity.drip

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.StrictMode
import android.text.Html
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.BuildConfig
import com.agrodrip.R
import com.agrodrip.activity.WatchDemoActivity
import com.agrodrip.activity.farm.CropProblemActivity
import com.agrodrip.adapter.farmsAdapter.ProblemSolutionAdapter
import com.agrodrip.databinding.ActivityIssueDripBinding
import com.agrodrip.model.FarmProblemListListData
import com.agrodrip.model.FarmProblemListResponse
import com.agrodrip.model.IssueDripData
import com.agrodrip.utils.Function
import com.agrodrip.utils.LocaleManager
import com.agrodrip.utils.Utils
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class IssueDripActivity : BaseActivity() {

    private lateinit var binding: ActivityIssueDripBinding
    private lateinit var issueDripData: IssueDripData
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_issue_drip)

        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())

        initWidget()
    }

    @SuppressLint("SetTextI18n")
    private fun initWidget() {

        issueDripData = intent.getParcelableExtra("issueDripData") as IssueDripData
        binding.imgBack.setOnClickListener { finish() }
        binding.shareDripIssue.setOnClickListener { shareNewsData(issueDripData) }
        binding.toolbarTxtMore.setOnClickListener {
            startActivity(Intent(this, IssueDripListActivity::class.java))
        }

        if (issueDripData.issue_video_url != "") {
            binding.txtViewVideo.visibility = View.VISIBLE

            binding.txtViewVideo.setOnClickListener {
                val intent = Intent(this, WatchDemoActivity::class.java)
                intent.putExtra("videoUrl", issueDripData.issue_video_url)
                startActivity(intent)
            }
        } else {
            binding.txtViewVideo.visibility = View.GONE
        }

        val description_en = Html.fromHtml(issueDripData.description_en)
        val description_gu = Html.fromHtml(issueDripData.description_gu)

        if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH){
            binding.txtDripIssueTitle.text = issueDripData.issue_title_en
            binding.tvDripIssueDesc.text = description_en
        }else{
            binding.txtDripIssueTitle.text = issueDripData.issue_title_gu
            binding.tvDripIssueDesc.text = description_gu
        }


        binding.txtDripIssueDate.text = issueDripData.date + " " + getString(R.string.txt_by_agrodrip)
        Glide.with(this)
            .load(issueDripData.issue_image)
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .into(binding.imgDripIssue)

        setProblemsAdapter()
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
                    val problemSolutionAdapter = ProblemSolutionAdapter(this@IssueDripActivity, resData.rows, object : ProblemSolutionAdapter.OnItemClick {
                        override fun onItemClick(farmProblemListListData: FarmProblemListListData) {
                            val intent = Intent(this@IssueDripActivity, CropProblemActivity::class.java)
                            intent.putExtra("farmProblemListListData", farmProblemListListData)
                            startActivity(intent)
                        }
                    })
                    binding.rvDripProblemList.adapter = problemSolutionAdapter
                } else {
                    if (response.code() == 400) {
                        mDialog.dismiss()
                        Log.d("TAG", "onFailure: " + response.message())
                        binding.llRecommended.visibility=View.GONE
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
        param["crop_type_id"] = issueDripData.id
//        param["crop_type_sub_id"] = issueDripData.cropSubType.id
        param["problem_type"] ="1"
        return param
    }

    private fun shareNewsData(issueDripData: IssueDripData) {

        Picasso.get().load(issueDripData.issue_image).into(object : com.squareup.picasso.Target {
            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {

            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_STREAM, Function.getLocalBitmapUri(bitmap, this@IssueDripActivity))

                val description_en = Html.fromHtml(issueDripData.description_en)
                val description_gu = Html.fromHtml(issueDripData.description_gu)

                if (LocaleManager.getLanguagePref(this@IssueDripActivity) == LocaleManager.ENGLISH){
                    intent.putExtra(Intent.EXTRA_TEXT, issueDripData.issue_title_en + " \n\n " +
                            description_en + " \n\n " + "http://play.google.com/store/apps/details?id=" +
                            BuildConfig.APPLICATION_ID)
                }else{
                    intent.putExtra(Intent.EXTRA_TEXT, issueDripData.issue_title_gu + " \n\n " +
                            description_gu + " \n\n " + "http://play.google.com/store/apps/details?id=" +
                            BuildConfig.APPLICATION_ID)
                }

                startActivity(Intent.createChooser(intent, getString(R.string.share_neews_by_txt)))
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}