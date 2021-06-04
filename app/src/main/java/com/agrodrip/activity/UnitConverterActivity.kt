package com.agrodrip.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import androidx.databinding.DataBindingUtil
import com.agrodrip.BaseActivity
import com.agrodrip.R
import com.agrodrip.databinding.ActivityUnitConverterBinding
import com.agrodrip.utils.Constants
import com.agrodrip.utils.Function
import com.agrodrip.utils.Pref
import kotlinx.android.synthetic.main.activity_unit_converter.*


class UnitConverterActivity : BaseActivity(), AdapterView.OnItemSelectedListener, TextWatcher {

    private lateinit var imgback: ImageView
    private lateinit var binding: ActivityUnitConverterBinding
    private var convertPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_unit_converter)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        val toolbarText = toolbar.findViewById<TextView>(R.id.toolbarText)
        toolbarText.text = resources.getString(R.string.unit_converter)
        imgback = toolbar.findViewById(R.id.imgBack)
        imgback.visibility = View.VISIBLE
        imgback.setOnClickListener{
            onBackPressed()
        }
        setSupportActionBar(toolbar)

        initWidget()
    }

    private fun initWidget() {

        binding.edtUnit.addTextChangedListener(this)

        // Spinner Drop down elements
        val categories: MutableList<String> = ArrayList()
        categories.add(getString(R.string.hectare))
        categories.add(getString(R.string.sq_yard))
        categories.add(getString(R.string.guntha))
        categories.add(getString(R.string.bigha_23))
        categories.add(getString(R.string.bigha_16))
        categories.add(getString(R.string.acre))

        val dataAdapter =
            object : ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories) {
                override fun getView(
                    position: Int,
                    @Nullable convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view = super.getView(position, convertView, parent)
                    val listItem = view.findViewById<TextView>(android.R.id.text1)
                    listItem.setTextColor(
                        ContextCompat.getColor(
                            this@UnitConverterActivity,
                            R.color.colorPrimary
                        )
                    )
                    listItem.textSize = 18f
                    return view
                }
            }
        dataAdapter.setDropDownViewResource(R.layout.spinner_layout)
        binding.spinnerUp.adapter = dataAdapter
        binding.spinnerUp.onItemSelectedListener = this
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val message = parent!!.getItemAtPosition(position).toString()
        when (position) {
            0 -> {
                convertPosition = 0
                binding.edtUnit.setText("")
                initUnit()
                Function.showMessage(message, parentRelative)
            }
            1 -> {
                convertPosition = 1
                binding.edtUnit.setText("")
                initUnit()
                Function.showMessage(message, parentRelative)
            }
            2 -> {
                convertPosition = 2
                binding.edtUnit.setText("")
                initUnit()
                Function.showMessage(message, parentRelative)
            }
            3 -> {
                convertPosition = 3
                binding.edtUnit.setText("")
                initUnit()
                Function.showMessage(message, parentRelative)
            }
            4 -> {
                convertPosition = 4
                binding.edtUnit.setText("")
                initUnit()
                Function.showMessage(message, parentRelative)
            }
            5 -> {
                convertPosition = 5
                binding.edtUnit.setText("")
                initUnit()
                Function.showMessage(message, parentRelative)
            }

        }
    }

    //OnBackpressed
    override fun onBackPressed() {
        super.onBackPressed()
        this.finish()
    }


    override fun beforeTextChanged(unit: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun onTextChanged(unit: CharSequence?, p1: Int, p2: Int, p3: Int) {
        Log.d("sdsdsdsdd", unit.toString())
        if (unit.toString() != "") {
            when (convertPosition) {
                0 -> {
                    hectareConverter(unit.toString())
                }
                1 -> {
                    squareYardConverter(unit.toString())
                }
                2 -> {
                    gunthaConverter(unit.toString())
                }
                3 -> {
                    bighaConverter23(unit.toString())
                }
                4 -> {
                    bighaConverter16(unit.toString())
                }
                5 -> {
                    acreConverter(unit.toString())
                }
            }
        }else{
            initUnit()
        }
    }

    override fun afterTextChanged(unit: Editable?) {

    }


    private fun initUnit(){
        binding.txtHectare.text = "0.0"
        binding.txtSqYard.text = "0.0"
        binding.txtGuntha.text = "0.0"
        binding.txtBigha.text = "0.0"
        binding.txtBigha16.text = "0.0"
        binding.txtAcre.text = "0.0"
    }

    private fun acreConverter(unit: String?) {
        binding.txtHectare.text = String.format("%.2f", ((unit.toString().toDouble() * 0.4047F)))
        binding.txtSqYard.text = String.format("%.2f", ((unit.toString().toDouble() * 4839.9597F)))
        binding.txtGuntha.text = String.format("%.2f", ((unit.toString().toDouble() * 40F)))
        binding.txtBigha.text = String.format("%.2f", ((unit.toString().toDouble() * 1.7021F)))
        binding.txtBigha16.text = String.format("%.2f", ((unit.toString().toDouble() * 2.5F)))
        binding.txtAcre.text = String.format("%.2f", ((unit.toString().toDouble() * 1F)))
    }

    private fun bighaConverter16(unit: String?) {
        binding.txtHectare.text = String.format("%.2f", ((unit.toString().toDouble() * 0.1619F)))
        binding.txtSqYard.text = String.format("%.2f", ((unit.toString().toDouble() * 1935.9839F)))
        binding.txtGuntha.text = String.format("%.2f", ((unit.toString().toDouble() * 16F)))
        binding.txtBigha.text = String.format("%.2f", ((unit.toString().toDouble() * 0.6809F)))
        binding.txtBigha16.text = String.format("%.2f", ((unit.toString().toDouble() * 1F)))
        binding.txtAcre.text = String.format("%.2f", ((unit.toString().toDouble() * 0.4F)))
    }

    private fun bighaConverter23(unit: String?) {
        binding.txtHectare.text = String.format("%.2f", ((unit.toString().toDouble() * 0.2378F)))
        binding.txtSqYard.text = String.format("%.2f", ((unit.toString().toDouble() * 2843.4764F)))
        binding.txtGuntha.text = String.format("%.2f", ((unit.toString().toDouble() * 23.5F)))
        binding.txtBigha.text = String.format("%.2f", ((unit.toString().toDouble() * 1F)))
        binding.txtBigha16.text = String.format("%.2f", ((unit.toString().toDouble() * 1.4688F)))
        binding.txtAcre.text = String.format("%.2f", ((unit.toString().toDouble() * 0.5875F)))
    }

    private fun gunthaConverter(unit: String?) {
        binding.txtHectare.text = String.format("%.2f", ((unit.toString().toDouble() * 0.0101F)))
        binding.txtSqYard.text = String.format("%.2f", ((unit.toString().toDouble() * 120.999F)))
        binding.txtGuntha.text = String.format("%.2f", ((unit.toString().toDouble() * 1F)))
        binding.txtBigha.text = String.format("%.2f", ((unit.toString().toDouble() * 0.0426F)))
        binding.txtBigha16.text = String.format("%.2f", ((unit.toString().toDouble() * 0.0625F)))
        binding.txtAcre.text = String.format("%.2f", ((unit.toString().toDouble() * 0.025F)))
    }


    private fun squareYardConverter(unit: String?) {
        binding.txtHectare.text = String.format("%.2f", ((unit.toString().toDouble() * 0.0001F)))
        binding.txtSqYard.text = String.format("%.2f", ((unit.toString().toDouble() * 1F)))
        binding.txtGuntha.text = String.format("%.2f", ((unit.toString().toDouble() * 0.0083F)))
        binding.txtBigha.text = String.format("%.2f", ((unit.toString().toDouble() * 0.0004F)))
        binding.txtBigha16.text = String.format("%.2f", ((unit.toString().toDouble() * 0.0005F)))
        binding.txtAcre.text = String.format("%.2f", ((unit.toString().toDouble() * 0.0002F)))
    }

    private fun hectareConverter(unit: String?) {
        binding.txtHectare.text =  String.format("%.2f", ((unit.toString().toDouble() * 1F)))
        binding.txtSqYard.text = String.format("%.2f", ((unit.toString().toDouble() * 11959.9005F)))
        binding.txtGuntha.text = String.format("%.2f", ((unit.toString().toDouble() * 98.843F)))
        binding.txtBigha.text = String.format("%.2f", ((unit.toString().toDouble() * 4.2061F)))
        binding.txtBigha16.text = String.format("%.2f", ((unit.toString().toDouble() * 6.1777F)))
        binding.txtAcre.text = String.format("%.2f", ((unit.toString().toDouble() * 2.4711F)))
    }


}
