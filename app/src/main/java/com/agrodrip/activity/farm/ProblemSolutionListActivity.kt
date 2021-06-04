package com.agrodrip.activity.farm

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.adapter.farmsAdapter.ProblemSolutionMainAdapter
import com.agrodrip.databinding.ActivityProblemSolutionListBinding
import com.agrodrip.model.FarmProblemListListData
import com.agrodrip.model.FarmProblemListResponse
import com.agrodrip.utils.Constants
import com.agrodrip.utils.Function
import com.agrodrip.utils.Pref
import com.agrodrip.utils.Utils
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class ProblemSolutionListActivity : BaseActivity() {

    private lateinit var imgback: ImageView
    private lateinit var problemSolutionAdapter: ProblemSolutionMainAdapter
    private lateinit var binding: ActivityProblemSolutionListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_problem_solution_list)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.problems_solutions)
        imgback = toolbar.findViewById<ImageView>(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener{
            finish()
        }
        setSupportActionBar(toolbar)

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
                    problemSolutionAdapter = ProblemSolutionMainAdapter(this@ProblemSolutionListActivity, resData.rows, object : ProblemSolutionMainAdapter.OnItemClick {
                        override fun onItemClick(farmProblemListListData: FarmProblemListListData) {
                            val intent = Intent(this@ProblemSolutionListActivity, CropProblemActivity::class.java)
                            intent.putExtra("farmProblemListListData", farmProblemListListData)
                            startActivity(intent)
                        }
                    })
                    binding.problemsRecycler.adapter = problemSolutionAdapter
                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
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
//        param["crop_type_id"] = myFarmListData.cropType.id
//        param["crop_type_sub_id"] = myFarmListData.cropSubType.id
        return param
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        val actionSettings = menu!!.findItem(R.id.action_settings)
        actionSettings.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_refresh -> {
                setProblemsAdapter()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}