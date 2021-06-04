package com.agrodrip.activity.farm

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.model.CommonResponse
import com.agrodrip.model.CropSubCategoryData
import com.agrodrip.model.MyFarmListData
import com.agrodrip.model.UserData
import com.agrodrip.utils.*
import com.agrodrip.utils.Function
import com.agrodrip.utils.Function.showMessage
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import com.agrodrip.webservices.toast
import kotlinx.android.synthetic.main.activity_add_farm.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class AddFarmActivity : BaseActivity(), AdapterView.OnItemSelectedListener {
    private var unitSelected: String = ""
    private var cropID: String = ""
    private var cropSubID: String = ""
    private lateinit var data: UserData
    private lateinit var myFarmListData: MyFarmListData
    private lateinit var imgback: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_farm)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.add_farm)
        imgback = toolbar.findViewById(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener { finish() }
        setSupportActionBar(toolbar)

        initWidget()
    }

    private fun initWidget() {
        data = Pref.getUserData(this)!!
        edtSowingDate.setOnClickListener { datePicker() }
        edtChooesCrop.setOnClickListener {
            val intent = Intent(this, SelectCropActivity::class.java)
            startActivityForResult(intent, 1001)
        }


        btnSave.setOnClickListener {

            if (intent.getStringExtra("type") == "edit") {
                editFarm()
            } else {
                addFarm()
            }

//            if (TextUtils.isEmpty(edtFieldName.text.toString())) {
//                showMessage(getString(R.string.enter_village_txt), parentRelative)
//            } else if (TextUtils.isEmpty(cropID)) {
//                showMessage(getString(R.string.chooes_crop_txt), parentRelative)
//            } else if (TextUtils.isEmpty(edtArea.text.toString())) {
//                showMessage(getString(R.string.choose_area), parentRelative)
//            } else if (TextUtils.isEmpty(edtSowingDate.text.toString())) {
//                showMessage(getString(R.string.choose_sowing_date_txt), parentRelative)
//            } else if (TextUtils.isEmpty(unitSelected)) {
//                showMessage(getString(R.string.choose_unit_txt), parentRelative)
//            } else {
//            }
        }
        spinerOfUnit()

        if (intent.getStringExtra("type") == "edit") {
            myFarmListData = intent.getParcelableExtra("myFarmListData") as MyFarmListData
            edtFieldName.setText(myFarmListData.farm_name)
            edtFieldName.setSelection(myFarmListData.farm_name.length)
            edtArea.setText(myFarmListData.area)
            edtSowingDate.setText(myFarmListData.sowing_date)
            if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH) {
                edtChooesCrop.setText(myFarmListData.cropSubType.sub_name_en)
            } else {
                edtChooesCrop.setText(myFarmListData.cropSubType.sub_name_gu)
            }

        }
    }

    private fun editFarm() {
        if (isValidate()){
            val mDialog = Utils.fcreateDialog(this)
            val call: Call<CommonResponse>? = ApiHandler.getApiService(this)?.editFarm(getParams(), "Bearer " + data.app_key)
            call?.enqueue(object : Callback<CommonResponse> {
                override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                    mDialog.dismiss()
                    val resData = response.body()
                    if (resData != null && response.isSuccessful) {
                        Log.d("res", "res$resData")
                        if (resData.code == 200) {
                            val intent = Intent()
                            intent.putExtra("backAddFarmData", true)
                            setResult(RESULT_OK, intent)
                            finish()
                            parentRelative.snackBar(resData.message)
                        } else if (resData.code == 400) {
                            parentRelative.snackBar(resData.message)
                        }

                    } else {
                        if (response.code() == 400) {
                            parentRelative.snackBar(response.message())
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
    }

    private fun spinerOfUnit() {
        // Spinner Drop down elements
        val categories: MutableList<String> = ArrayList()
        categories.add(getString(R.string.bigha))
        categories.add(getString(R.string.acre))
        categories.add(getString(R.string.hectare))
        categories.add(getString(R.string.guntha))

        val dataAdapter = object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories) {
            override fun getView(position: Int, @Nullable convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val listItem = view.findViewById<TextView>(android.R.id.text1)
                listItem.setTextColor(ContextCompat.getColor(this@AddFarmActivity, R.color.colorPrimary))
                listItem.textSize = 14f
                return view
            }
        }
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout)
        spinnerUnit.adapter = dataAdapter
        spinnerUnit.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val message = parent!!.getItemAtPosition(position).toString() + " " + "Selected"
//        showMessage(message, parentRelative)

        unitSelected = parent.getItemAtPosition(position).toString()
    }

    private fun datePicker() {
        val mcurrentDate = Calendar.getInstance()
        val mYear = mcurrentDate[Calendar.YEAR]
        val mMonth = mcurrentDate[Calendar.MONTH]
        val mDay = mcurrentDate[Calendar.DAY_OF_MONTH]

        val mDatePicker: DatePickerDialog
        mDatePicker = DatePickerDialog(this, R.style.DialogTheme, { _, selectedyear, selectedmonth, selectedday ->
            val selectedmonth = selectedmonth
            edtSowingDate.setText(Function.dateFormate(selectedday.toString() + "-" + (selectedmonth + 1) + "-" + selectedyear))
        }, mYear, mMonth, mDay)
        mDatePicker.show()

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001) {
            if (resultCode == Activity.RESULT_OK) {
                val categoyData = data?.getParcelableExtra("cropSubCategoryData") as CropSubCategoryData
                Log.d("TAG", "onActivityResult: ${categoyData.sub_name_en}")

                if (LocaleManager.getLanguagePref(this) == LocaleManager.ENGLISH) {
                    edtChooesCrop.setText(categoyData.sub_name_en)
                } else {
                    edtChooesCrop.setText(categoyData.sub_name_gu)
                }
                cropSubID = categoyData.id
                cropID = categoyData.category_id
            }

        }
    }

    private fun addFarm() {
        if (isValidate()){
            val mDialog = Utils.fcreateDialog(this)
            val call: Call<CommonResponse>? = ApiHandler.getApiService(this)?.addFarm(getParams(), "Bearer " + data.app_key)
            call?.enqueue(object : Callback<CommonResponse> {
                override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                    mDialog.dismiss()
                    val resData = response.body()
                    if (resData != null && response.isSuccessful) {
                        Log.d("res", "res$resData")
                        if (resData.code == 200) {
                            val intent = Intent()
                            intent.putExtra("backAddFarmData", true)
                            setResult(RESULT_OK, intent)
                            finish()
                            parentRelative.snackBar(resData.message)
                        } else if (resData.code == 400) {
                            parentRelative.snackBar(resData.message)
                        }

                    } else {
                        if (response.code() == 400) {
                            parentRelative.snackBar(response.message())
                        }
                    }
                }

                override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                    mDialog.dismiss()
                    toast(getString(R.string.msg_internet))
                    Log.d("TAG", "onFailure: " + t.cause.toString())
                }
            })
        }else{

        }
    }

    private fun isValidate(): Boolean {
        //Village Name
        if (edtFieldName.text.toString().trim() == "") {
            edtFieldName.error = resources.getString(R.string.should_not_blank)
            return false
        } else {
            edtFieldName.clearFocus()
            edtFieldName.error = null
        }

        //Crop
        if (edtChooesCrop.text.toString().trim() == "") {
            edtChooesCrop.error = resources.getString(R.string.should_not_blank)
            return false
        } else {
            edtChooesCrop.clearFocus()
            edtChooesCrop.error = null
        }

        if (edtSowingDate.text.toString().trim() == "") {
            edtSowingDate.error = resources.getString(R.string.should_not_blank)
            return false
        } else {
            edtSowingDate.clearFocus()
            edtSowingDate.error = null
        }

        if (edtArea.text.toString().trim() == "") {
            edtArea.error = resources.getString(R.string.should_not_blank)
            return false
        } else {
            edtArea.clearFocus()
            edtArea.error = null
        }

        return true
    }

    private fun getParams(): HashMap<String, String> {
        val param = HashMap<String, String>()
        param["crop_type_id"] = cropID
        param["crop_type_sub_id"] = cropSubID
        param["farm_name"] = edtFieldName.text.toString()
        param["unit"] = unitSelected
        param["area"] = edtArea.text.toString()
        param["sowing_date"] = edtSowingDate.text.toString()
        return param
    }


}

