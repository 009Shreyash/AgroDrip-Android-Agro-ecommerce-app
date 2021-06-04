package com.agrodrip.activity.farm

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.BuildConfig
import com.agrodrip.R
import com.agrodrip.adapter.farmsAdapter.SolutionAdapter
import com.agrodrip.databinding.ActivityCropProblemBinding
import com.agrodrip.model.FarmProblemListListData
import com.agrodrip.model.FarmSolutionListResponse
import com.agrodrip.model.SolutionList
import com.agrodrip.utils.Function
import com.agrodrip.utils.LocaleManager
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CropProblemActivity : BaseActivity() {

    private lateinit var imgback: ImageView
    private lateinit var binding: ActivityCropProblemBinding
    private lateinit var farmProblemListListData: FarmProblemListListData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_crop_problem)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.problems_solutions)
        imgback = toolbar.findViewById<ImageView>(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener { onBackPressed() }
        setSupportActionBar(toolbar)

        initWidget()

    }

    private fun initWidget() {
        farmProblemListListData = intent.getParcelableExtra("farmProblemListListData") as FarmProblemListListData

        val description_en = Html.fromHtml(farmProblemListListData.description_en)
        val description_gu = Html.fromHtml(farmProblemListListData.description_gu)

        if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH){
            binding.txtProblemsTitle.text = farmProblemListListData.title_en
            binding.tvNewsDesc.text = description_en
        }else{
            binding.txtProblemsTitle.text = farmProblemListListData.title_gu
            binding.tvNewsDesc.text = description_gu
        }

        Glide.with(this)
            .load(farmProblemListListData.problem_image)
            .placeholder(R.drawable.places_autocomplete_toolbar_shadow)
            .error(R.drawable.places_autocomplete_toolbar_shadow)
            .into(binding.imgCropProblems)

        binding.shareProblems.setOnClickListener {
            shareProblemsData(farmProblemListListData)
        }

        getSolutionsList()
    }

    private fun getSolutionsList() {
        val call: Call<FarmSolutionListResponse>? = ApiHandler.getApiService(this)?.getFarmSolutionsList(farmProblemListListData.id)
        call?.enqueue(object : Callback<FarmSolutionListResponse> {
            override fun onResponse(call: Call<FarmSolutionListResponse>, response: Response<FarmSolutionListResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    Log.d("res", "res$resData")
                    val solutionAdapter = SolutionAdapter(this@CropProblemActivity, resData.rows.solution, object : SolutionAdapter.OnItemClick {
                        override fun onItemClick(solutionList: SolutionList) {
                            val intent = Intent(this@CropProblemActivity, SolutionProductActivity::class.java)
                            intent.putExtra("solutionData", solutionList)
                            intent.putExtra("isFrom", "CropProblemActivity")
                            startActivity(intent)
                        }
                    })
                    binding.rvCropSolutionList.adapter = solutionAdapter
                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<FarmSolutionListResponse>, t: Throwable) {
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })

    }


    private fun shareProblemsData(problemData: FarmProblemListListData) {

        Picasso.get().load(problemData.problem_image).into(object : com.squareup.picasso.Target {
            override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {

            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }

            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "image/*"
                intent.putExtra(Intent.EXTRA_STREAM, Function.getLocalBitmapUri(bitmap, this@CropProblemActivity))

                val description_en = Html.fromHtml(farmProblemListListData.description_en)
                val description_gu = Html.fromHtml(farmProblemListListData.description_gu)

                if (LocaleManager.getLanguagePref(this@CropProblemActivity) == LocaleManager.ENGLISH) {
                    intent.putExtra(Intent.EXTRA_TEXT, problemData.title_en + " \n\n " + description_en
                            + " \n\n " + "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                } else {
                    intent.putExtra(Intent.EXTRA_TEXT, problemData.title_gu + " \n\n " + description_gu
                            + " \n\n " + "http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)
                }

                startActivity(Intent.createChooser(intent, getString(R.string.share_crop_problem_by_txt)))
            }
        })
    }


    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}