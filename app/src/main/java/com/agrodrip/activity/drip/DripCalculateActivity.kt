package com.agrodrip.activity.drip

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.ActionBar
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.activity.farm.SelectCropActivity
import com.agrodrip.databinding.ActivityDripCalculateBinding
import com.agrodrip.model.CommonResponse
import com.agrodrip.model.CropSubCategoryData
import com.agrodrip.model.TalukaListResponse
import com.agrodrip.model.UserData
import com.agrodrip.utils.Function
import com.agrodrip.utils.LocaleManager
import com.agrodrip.utils.Pref
import com.agrodrip.utils.Utils
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_add_farm.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.set

class DripCalculateActivity : BaseActivity() {

    private var cropID: String = ""
    private var taluka = ""
    private var crop = ""
    private var spacing = ""
    private var areaUnit = ""
    private var repeatCase = ""
    private var totalPrice = ""
    private var estimatePrice = ""
    private var subsidy = ""
    private lateinit var data: UserData
    private lateinit var imgback: ImageView
    
    private lateinit var binding: ActivityDripCalculateBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_drip_calculate)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.drip_calculation)
        imgback = toolbar.findViewById<ImageView>(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener {
            finish()
        }

        initWidget()
    }

    @SuppressLint("SetTextI18n")
    private fun initWidget() {
        data = Pref.getUserData(this)!!
        binding.selectCrop.setOnClickListener {
            val intent = Intent(this@DripCalculateActivity, SelectCropActivity::class.java)
            startActivityForResult(intent, 1001)
        }

        binding.llAskExpert.setOnClickListener {
            AskExpertDialog()
        }

        binding.btnCalculateDrip.setOnClickListener {

            if (isValidate()) {

                binding.llCalculation.visibility = View.VISIBLE

                binding.txtVillage.text = binding.villageName.text.toString()
                binding.txtTaluka.text = taluka
                binding.txtCrop.text = crop
                binding.txtSpacing.text = spacing
                binding.txtArea.text = binding.editArea.text.toString() + " " + areaUnit
                binding.txtRepeatCase.text = repeatCase

                if (getTotalPriceGov() != "0" || getTotalPriceGov() != "") {
                    binding.txtPriceTotalGov.text = getTotalPriceGov()
                } else {
                    binding.txtPriceTotalGov.text = "0"
                }


                if (getSubsidy() != "") {
                    binding.txtSubsidy.text = getSubsidy() + " %"
                } else {
                    binding.txtSubsidy.text = ""
                }

                if (getEstimatePrice() != "" || getEstimatePrice() != "0") {
                    binding.txtEstimatePrice.text = getEstimatePrice()
                } else {
                    binding.txtEstimatePrice.text = "0"
                }

                binding.btnSave.setOnClickListener {
                    dripAPI()
                }

                Function.showMessage(getString(R.string.drip_cal_success_txt), binding.parentLinear)
            } else {
                binding.llCalculation.visibility = View.GONE
            }

        }

        binding.closeCalculation.setOnClickListener {
            binding.llCalculation.visibility = View.GONE

            binding.txtVillage.text = ""
            binding.txtTaluka.text = ""
            binding.txtCrop.text = ""
            binding.txtSpacing.text = ""
            binding.txtArea.text = ""
            binding.txtRepeatCase.text = ""
            binding.txtPriceTotalGov.text = ""
            binding.txtSubsidy.text = ""
            binding.txtEstimatePrice.text = ""
        }

        chooseTalukaDropDown()
        chooseAreaDropDown()
        chooseSpacingDropDown()
        chooseRpeatCaseDropDown()
    }

    private fun isValidate(): Boolean {

        //Village Name
        if (binding.villageName.text.toString().trim() == "") {
            binding.villageName.error = resources.getString(R.string.should_not_blank)
            return false
        } else {
            binding.villageName.clearFocus()
            binding.villageName.error = null
        }

        //Crop
        if (binding.selectCrop.text.toString().trim() == "") {
            binding.selectCrop.error = resources.getString(R.string.should_not_blank)
            return false
        } else {
            binding.selectCrop.clearFocus()
            binding.selectCrop.error = null
        }
        //Area
        if (binding.editArea.text.toString().trim() == "") {
            binding.editArea.error = resources.getString(R.string.should_not_blank)
            return false
        } else {
            binding.editArea.clearFocus()
            binding.editArea.error = null
        }

        return true
    }

    private fun getSubsidy(): String? {

        if (repeatCase == resources.getString(R.string.txt_no)) {

            when (areaUnit) {
                getString(R.string.hectare) -> {
                    if (binding.editArea.text.toString() != "") {
                        if (binding.editArea.text.toString() > "2") {
                            subsidy = "70"
                        } else if (binding.editArea.text.toString() <= "2" && taluka.contains("DarkZone")) {
                            subsidy = "80"
                        } else if (binding.editArea.text.toString() <= "2" && !taluka.contains("DarkZone")) {
                            subsidy = "70"
                        }
                    }
                }
                getString(R.string.bigha) -> {
                    if (binding.editArea.text.toString() != "") {
                        val bigToHec = binding.editArea.text.toString().toDouble() * 0.2378F
                        val area = String.format("%.2f", bigToHec)
                        if (area > "2") {
                            subsidy = "70"
                        } else if (binding.editArea.text.toString() <= "2" && taluka.contains("DarkZone")) {
                            subsidy = "80"
                        } else if (binding.editArea.text.toString() <= "2" && !taluka.contains("DarkZone")) {
                            subsidy = "70"
                        }
                    }
                }
                getString(R.string.acre) -> {
                    if (binding.editArea.text.toString() != "") {
                        val acreToHec = binding.editArea.text.toString().toDouble() * 0.4047F
                        val area = String.format("%.2f", acreToHec)
                        if (area > "2") {
                            subsidy = "70"
                        } else if (binding.editArea.text.toString() <= "2" && taluka.contains("DarkZone")) {
                            subsidy = "80"
                        } else if (binding.editArea.text.toString() <= "2" && !taluka.contains("DarkZone")) {
                            subsidy = "70"
                        }
                    }
                }
                getString(R.string.guntha) -> {
                    if (binding.editArea.text.toString() != "") {
                        val gunToHec = binding.editArea.text.toString().toDouble() * 0.0101F
                        val area = String.format("%.2f", gunToHec)
                        if (area > "2") {
                            subsidy = "70"
                        } else if (binding.editArea.text.toString() <= "2" && taluka.contains("DarkZone")) {
                            subsidy = "80"
                        } else if (binding.editArea.text.toString() <= "2" && !taluka.contains("DarkZone")) {
                            subsidy = "70"
                        }
                    }
                }
            }

        } else {

            when (areaUnit) {
                getString(R.string.hectare) -> {
                    if (binding.editArea.text.toString() != "") {
                        if (binding.editArea.text.toString() > "2") {
                            subsidy = "45"
                        } else if (binding.editArea.text.toString() <= "2" && taluka.contains("DarkZone")) {
                            subsidy = "55"
                        } else if (binding.editArea.text.toString() <= "2" && !taluka.contains("DarkZone")) {
                            subsidy = "45"
                        }
                    }
                }
                getString(R.string.bigha) -> {
                    if (binding.editArea.text.toString() != "") {
                        val bigToHec = binding.editArea.text.toString().toDouble() * 0.2378F
                        val area = String.format("%.2f", bigToHec)
                        if (area > "2") {
                            subsidy = "45"
                        } else if (binding.editArea.text.toString() <= "2" && taluka.contains("DarkZone")) {
                            subsidy = "55"
                        } else if (binding.editArea.text.toString() <= "2" && !taluka.contains("DarkZone")) {
                            subsidy = "45"
                        }
                    }
                }
                getString(R.string.acre) -> {
                    if (binding.editArea.text.toString() != "") {
                        val acreToHec = binding.editArea.text.toString().toDouble() * 0.4047F
                        val area = String.format("%.2f", acreToHec)
                        if (area > "2") {
                            subsidy = "45"
                        } else if (binding.editArea.text.toString() <= "2" && taluka.contains("DarkZone")) {
                            subsidy = "55"
                        } else if (binding.editArea.text.toString() <= "2" && !taluka.contains("DarkZone")) {
                            subsidy = "45"
                        }
                    }
                }
                getString(R.string.guntha) -> {
                    if (binding.editArea.text.toString() != "") {
                        val gunToHec = binding.editArea.text.toString().toDouble() * 0.0101F
                        val area = String.format("%.2f", gunToHec)
                        if (area > "2") {
                            subsidy = "45"
                        } else if (binding.editArea.text.toString() <= "2" && taluka.contains("DarkZone")) {
                            subsidy = "55"
                        } else if (binding.editArea.text.toString() <= "2" && !taluka.contains("DarkZone")) {
                            subsidy = "45"
                        }
                    }
                }
            }

        }


        return subsidy
    }

    private fun getEstimatePrice(): String? {
        if (totalPrice != "" && subsidy != "") {
            val estimate = (totalPrice.toDouble() * subsidy.toDouble()) / 100
            estimatePrice = String.format("%.2f", (totalPrice.toDouble() - estimate))
        } else {
            estimatePrice = "0"
        }

        return estimatePrice
    }

    private fun getTotalPriceGov(): String? {

        if (spacing == "1.4") {

            when (areaUnit) {
                getString(R.string.hectare) -> {
                    if (binding.editArea.text.toString() != "") {
                        if (binding.editArea.text.toString() >= "1") {
                            val area = binding.editArea.text.toString()
                            totalPrice = String.format("%.2f", (area.toDouble() * 93483))
                        } else {
                            totalPrice = "0"
                        }
                    }
                }
                getString(R.string.bigha) -> {
                    if (binding.editArea.text.toString() != "") {
                        if (binding.editArea.text.toString() >= "1") {
                            val area = binding.editArea.text.toString()
                            totalPrice = String.format("%.2f", (area.toDouble() * (93483/4.2)))
                        } else {
                            totalPrice = "0"
                        }
                    }

                }
                getString(R.string.acre) -> {
                    if (binding.editArea.text.toString() != "") {
                        if (binding.editArea.text.toString() >= "1") {
                            val area = binding.editArea.text.toString()
                            totalPrice = String.format("%.2f", (area.toDouble() * (93483/2.5)))
                        } else {
                            totalPrice = "0"
                        }
                    }
                }
                getString(R.string.guntha) -> {
                    if (binding.editArea.text.toString() != "") {
                        if (binding.editArea.text.toString() >= "1") {
                            val area = binding.editArea.text.toString()
                            totalPrice = String.format("%.2f", (area.toDouble() * (93483/98.9)))
                        } else {
                            totalPrice = "0"
                        }
                    }
                }
            }

        } else {
            when (areaUnit) {
                getString(R.string.hectare) -> {
                    if (binding.editArea.text.toString() != "") {
                        if (binding.editArea.text.toString() >= "1") {
                            val area = binding.editArea.text.toString()
                            totalPrice = String.format("%.2f", (area.toDouble() * 86703))
                        } else {
                            totalPrice = "0"
                        }
                    }
                }
                getString(R.string.bigha) -> {
                    if (binding.editArea.text.toString() != "") {
                        if (binding.editArea.text.toString() >= "1") {
                            val area = binding.editArea.text.toString()
                            totalPrice = String.format("%.2f", (area.toDouble() * (86703/4.2)))
                        } else {
                            totalPrice = "0"
                        }
                    }
                }
                getString(R.string.acre) -> {
                    if (binding.editArea.text.toString() != "") {
                        if (binding.editArea.text.toString() >= "1") {
                            val area = binding.editArea.text.toString()
                            totalPrice = String.format("%.2f", (area.toDouble() * (86703/2.5)))
                        } else {
                            totalPrice = "0"
                        }
                    }
                }
                getString(R.string.guntha) -> {
                    if (binding.editArea.text.toString() != "") {
                        if (binding.editArea.text.toString() >= "1") {
                            val area = binding.editArea.text.toString()
                            totalPrice = String.format("%.2f", (area.toDouble() * (86703/98.9)))
                        } else {
                            totalPrice = "0"
                        }
                    }
                }
            }
        }

        return totalPrice
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK) {
                val categoyData = data?.getParcelableExtra("cropSubCategoryData") as CropSubCategoryData
                Log.d("TAG", "onActivityResult: ${categoyData.sub_name_en}")
                if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH) {
                    binding.selectCrop.setText(categoyData.sub_name_en)
                } else {
                    binding.selectCrop.setText(categoyData.sub_name_gu)
                }
                cropID = categoyData.id
                crop = categoyData.sub_name_en
            }

        }
    }


    private fun chooseTalukaDropDown() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<TalukaListResponse>? = ApiHandler.getApiService(this)?.talukaList()
        call?.enqueue(object : Callback<TalukaListResponse> {
            override fun onResponse(call: Call<TalukaListResponse>, response: Response<TalukaListResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    Handler().postDelayed({
                        mDialog.dismiss()
                    }, 1000)

                    Log.d("res", "res$resData")
                    val list = mutableListOf<String>()
                    val responseList = resData.rows
                    val item = arrayOfNulls<String>(responseList.size)
                    for (i in 0 until responseList.size) {
                        if (LocaleManager.getLanguagePref(this@DripCalculateActivity) == LocaleManager.ENGLISH){
                            item[i] = responseList[i].name_en
                        }else{
                            item[i] = responseList[i].name_gu
                        }
                    }
                    val dataAdapter = object : ArrayAdapter<String>(this@DripCalculateActivity, android.R.layout.simple_spinner_item, item) {
                        override fun getView(position: Int, @Nullable convertView: View?, parent: ViewGroup): View {
                            val view = super.getView(position, convertView, parent)
                            val listItem = view.findViewById<TextView>(android.R.id.text1)
                            listItem.setTextColor(ContextCompat.getColor(this@DripCalculateActivity, R.color.colorPrimary))
                            listItem.textSize = 18f
                            return view
                        }
                    }
                    dataAdapter.setDropDownViewResource(R.layout.spinner_layout)
                    binding.spinnerTaluka.adapter = dataAdapter
                    binding.spinnerTaluka.onItemSelectedListener = object :
                        AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                            taluka = parent.getItemAtPosition(position).toString()
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }
                    }
                } else {
                    if (response.code() == 400) {
                        mDialog.dismiss()
                        binding.root.snackBar(response.message())
                    } else {
                        mDialog.dismiss()
                    }
                }
            }

            override fun onFailure(call: Call<TalukaListResponse>, t: Throwable) {
                mDialog.dismiss()
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }


    private fun chooseRpeatCaseDropDown() {
        // Spinner Drop down elements
        val categories: MutableList<String> = ArrayList()
        categories.add(getString(R.string.txt_yes))
        categories.add(getString(R.string.txt_no))


        val dataAdapter =
            object : ArrayAdapter<String>(this@DripCalculateActivity, android.R.layout.simple_spinner_item, categories) {
                override fun getView(
                    position: Int,
                    @Nullable convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getView(position, convertView, parent)
                    val listItem = view.findViewById<TextView>(android.R.id.text1)
                    listItem.setTextColor(
                        ContextCompat.getColor(
                            this@DripCalculateActivity,
                            R.color.colorPrimary
                        )
                    )
                    listItem.textSize = 18f
                    return view
                }
            }
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout)
        binding.spinnerRepeatCase.adapter = dataAdapter
        binding.spinnerRepeatCase.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                repeatCase = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun chooseSpacingDropDown() {
        // Spinner Drop down elements
        val categories: MutableList<String> = ArrayList()
        categories.add("1.4")
        categories.add("1.52")


        val dataAdapter =
            object : ArrayAdapter<String>(this@DripCalculateActivity, android.R.layout.simple_spinner_item, categories) {
                override fun getView(
                    position: Int,
                    @Nullable convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getView(position, convertView, parent)
                    val listItem = view.findViewById<TextView>(android.R.id.text1)
                    listItem.setTextColor(
                        ContextCompat.getColor(
                            this@DripCalculateActivity,
                            R.color.colorPrimary
                        )
                    )
                    listItem.textSize = 18f
                    return view
                }
            }
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout)
        binding.spinnerSpacing.adapter = dataAdapter
        binding.spinnerSpacing.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                spacing = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun chooseAreaDropDown() {
        // Spinner Drop down elements
        val categories: MutableList<String> = ArrayList()
        categories.add(getString(R.string.bigha))
        categories.add(getString(R.string.acre))
        categories.add(getString(R.string.hectare))
        categories.add(getString(R.string.guntha))

        val dataAdapter =
            object : ArrayAdapter<String>(this@DripCalculateActivity, android.R.layout.simple_spinner_item, categories) {
                override fun getView(
                    position: Int,
                    @Nullable convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getView(position, convertView, parent)
                    val listItem = view.findViewById<TextView>(android.R.id.text1)
                    listItem.setTextColor(
                        ContextCompat.getColor(
                            this@DripCalculateActivity,
                            R.color.colorPrimary
                        )
                    )
                    listItem.textSize = 18f
                    return view
                }
            }
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout)
        binding.spinnerUnit.adapter = dataAdapter
        binding.spinnerUnit.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                areaUnit = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }

    private fun getParams(): HashMap<String, String> {
        val param = HashMap<String, String>()
        param["village"] = binding.villageName.text.toString()
        param["taluka"] = taluka
        param["crop"] = crop
        param["spacing"] = spacing
        param["area"] = areaUnit
        param["repeat_case"] = repeatCase
        param["subsiy"] = subsidy
        param["estimated_cost"] = estimatePrice
        param["price_gov"] = totalPrice
        return param
    }

    private fun dripAPI() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<CommonResponse>? = ApiHandler.getApiService(this)?.addDrip("Bearer " + data.app_key, getParams())
        call?.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                val resData: CommonResponse? = response.body()
                if (resData != null) {
                    Handler().postDelayed({
                        mDialog.dismiss()
                        Log.d("res", "res$resData")
                        binding.root.snackBar(getString(R.string.drip_added_successfully))
                        binding.llCalculation.visibility = View.GONE
                        binding.txtVillage.text = ""
                        binding.txtTaluka.text = ""
                        binding.txtCrop.text = ""
                        binding.txtSpacing.text = ""
                        binding.txtArea.text = ""
                        binding.txtRepeatCase.text = ""
                        binding.txtPriceTotalGov.text = ""
                        binding.txtSubsidy.text = ""
                        binding.txtEstimatePrice.text = ""
                    }, 1000)

                } else {
                    try {
                        mDialog.dismiss()
                        val mError: CommonResponse = GsonBuilder().create().fromJson(response.errorBody()!!.string(), CommonResponse::class.java)
                        binding.root.snackBar(mError.message)
                    } catch (e: IOException) {
                        // handle failure to read error
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                mDialog.dismiss()
                binding.root.snackBar((getString(R.string.msg_internet)))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    private fun askExpert() {
        val mDialog = Utils.fcreateDialog(this)
        val call: Call<CommonResponse>? = ApiHandler.getApiService(this)?.askExpert("Bearer " + data.app_key)
        call?.enqueue(object : Callback<CommonResponse> {
            override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    binding.root.snackBar(getString(R.string.inqirey_added_ask_expert))
                } else {
                    if (response.code() == 400) {
                        mDialog.dismiss()
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                mDialog.dismiss()
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }


    private fun AskExpertDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.custom_dialog_layout)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window!!.setLayout(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.WRAP_CONTENT
        )

        val tvTitle = dialog.findViewById(R.id.tv_title) as TextView
        val tvDesc = dialog.findViewById(R.id.tv_desc) as TextView
        val textYes = dialog.findViewById(R.id.txtYes) as TextView
        val textNo = dialog.findViewById(R.id.txtNo) as TextView

        tvTitle.text = getString(R.string.title_customer_service)
        tvDesc.text = getString(R.string.desc_customer_service)

        textYes.setOnClickListener {
            askExpert()
            dialog.dismiss()
        }

        textNo.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}