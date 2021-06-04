package com.agrodrip.activityimport android.os.Bundleimport android.os.Handlerimport android.util.Logimport android.view.Viewimport android.widget.ImageViewimport android.widget.TextViewimport androidx.appcompat.app.AppCompatActivityimport androidx.appcompat.widget.Toolbarimport androidx.databinding.DataBindingUtilimport com.agrodrip.BaseActivityimport com.agrodrip.Rimport com.agrodrip.databinding.ActivityFeedbackBindingimport com.agrodrip.model.UserDataimport com.agrodrip.model.VerifyOtpResponseimport com.agrodrip.utils.Constantsimport com.agrodrip.utils.Functionimport com.agrodrip.utils.Prefimport com.agrodrip.utils.Utilsimport com.agrodrip.webservices.ApiHandlerimport com.agrodrip.webservices.snackBarimport com.agrodrip.webservices.toastimport retrofit2.Callimport retrofit2.Callbackimport retrofit2.Responseclass FeedbackActivity : BaseActivity() {    private lateinit var imgback: ImageView    private lateinit var data: UserData    private lateinit var binding: ActivityFeedbackBinding    override fun onCreate(savedInstanceState: Bundle?) {        super.onCreate(savedInstanceState)        binding = DataBindingUtil.setContentView(this, R.layout.activity_feedback)        val toolbar: Toolbar = findViewById(R.id.toolbar)        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)        toolbarText.text = resources.getString(R.string.feedback_title)        imgback = toolbar.findViewById<ImageView>(R.id.imgBack)        imgback.visibility = View.VISIBLE        imgback.setOnClickListener { finish() }        setSupportActionBar(toolbar)        data = Pref.getUserData(this)!!        binding.btnSend.setOnClickListener {            if (binding.edtFeedback.text.toString().isEmpty()) {                toast(getString(R.string.enter_feedback_txt))            } else {                callFeedback()            }        }    }    override fun onBackPressed() {        super.onBackPressed()        finish()    }    private fun callFeedback() {        val mDialog = Utils.fcreateDialog(this)        val call: Call<VerifyOtpResponse>? = ApiHandler.getApiService(this)?.giveFeedback("Bearer " + data.app_key, binding.edtFeedback.text.toString())        call?.enqueue(object : Callback<VerifyOtpResponse> {            override fun onResponse(call: Call<VerifyOtpResponse>, response: Response<VerifyOtpResponse>) {                val resData = response.body()                if (resData != null && response.isSuccessful) {                    mDialog.dismiss()                    Log.d("res", "res$resData")                    if (resData.code == 200) {                        toast(resData.message)                        Handler().postDelayed({ finish() }, 1000)                    } else if (resData.code == 400) {                        binding.root.snackBar(resData.message)                    }                } else {                    mDialog.dismiss()                    if (response.code() == 400) {                        binding.root.snackBar(response.message())                    }                }            }            override fun onFailure(call: Call<VerifyOtpResponse>, t: Throwable) {                mDialog.dismiss()                toast(getString(R.string.msg_internet))                Log.d("TAG", "onFailure: " + t.cause.toString())            }        })    }}