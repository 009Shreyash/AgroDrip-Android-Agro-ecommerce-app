package com.agrodrip.activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.agrodrip.R
import com.agrodrip.adapter.SowingProgressNewActivityAdapter
import com.agrodrip.databinding.ActivitySowingProcessBinding
import com.agrodrip.model.Stage

class SowingProcessActivity : AppCompatActivity() {
    lateinit var imgback: ImageView
    lateinit var binding: ActivitySowingProcessBinding
    lateinit var list: ArrayList<Stage>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sowing_process)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.crop_stages)
        imgback = toolbar.findViewById(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        setSupportActionBar(toolbar)


//        list = intent.getSerializableExtra("data")

//        val list = intent.getParcelableArrayListExtra("data")

        val list = intent.getSerializableExtra("data") as ArrayList<Stage>
        Log.d("SowingProcessActivity", "onCreate: $list")
        imgback.setOnClickListener {
            finish()
        }

        val adapter = SowingProgressNewActivityAdapter(this, list)
        binding.rvSowingProcess.adapter = adapter
    }
}