package com.agrodrip.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.location.*
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.agrodrip.R
import com.agrodrip.activity.MainActivity
import com.agrodrip.activity.farm.AddFarmActivity
import com.agrodrip.activity.farm.AllFarmListActivity
import com.agrodrip.activity.farm.MyFarmActivity
import com.agrodrip.adapter.farmsAdapter.MyFarmListAdapter
import com.agrodrip.databinding.FragmentMyFarmBinding
import com.agrodrip.model.MyFarmListData
import com.agrodrip.model.MyFarmListResponse
import com.agrodrip.model.UserData
import com.agrodrip.utils.LocaleManager
import com.agrodrip.utils.Pref
import com.agrodrip.utils.Utils
import com.agrodrip.weather.activities.ForecastActivity
import com.agrodrip.weather.models.DataObject
import com.agrodrip.weather.models.Forecast
import com.agrodrip.weather.utilities.Constants
import com.agrodrip.weather.utilities.Utility
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.toast
import com.google.android.material.snackbar.Snackbar
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MyFarmFragment : Fragment(), View.OnClickListener {
    private val PERMISSION_ACCESS_FINE_LOCATION = 0
    private var connectivityManager: ConnectivityManager? = null
    private var networkInfo: NetworkInfo? = null
    private var locationManager: LocationManager? = null
    private var builder: AlertDialog.Builder? = null
    private var isConnected = false
    private var permissionRequestResult = 0
    private var addressStringBuilder: StringBuilder? = null
    private var weatherList: List<DataObject>? = null
    private var location: Location? = null
    private var latitude = 0.0
    private var longitude = 0.0

    private lateinit var binding: FragmentMyFarmBinding
    private lateinit var data: UserData
    private lateinit var myFarfmListAdapter: MyFarmListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_farm, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MainActivity?)!!.onHidingToolbar(false, resources.getString(R.string.my_farm))
        (requireActivity() as MainActivity?)!!.bottomNavigation.visibility = View.VISIBLE

        initWidget()
    }

    private fun initWidget() {
        data = Pref.getUserData(requireContext())!!
        binding.btnAddFarm.setOnClickListener(this)
        binding.layoutWeather.setOnClickListener(this)
        binding.txtViewAllFarm.setOnClickListener(this)

        getMyFarmList()
        detectLocation()
        if (Pref.getValue(requireContext(), Constants.PREF_ADDRESS, "").toString().isNotEmpty()) {
            addressStringBuilder = StringBuilder()
            addressStringBuilder!!.append(Pref.getValue(requireContext(), Constants.PREF_ADDRESS, "").toString())
        }

        if (LocaleManager.getLanguagePref(requireActivity()) == LocaleManager.ENGLISH) {
            getWeather(addressStringBuilder!!, LocaleManager.ENGLISH)
        } else {
            getWeather(addressStringBuilder!!, LocaleManager.GUJARATI)
        }

    }

    override fun onClick(v: View?) {
        when (v) {
            binding.layoutWeather -> {
                initMembers()
                checkPermissionAndConnectivity(permissionRequestResult, locationManager!!, isConnected)
            }
            binding.btnAddFarm -> {
                val intent = Intent(requireActivity(), AddFarmActivity::class.java)
                intent.putExtra("type", "add")
                startActivityForResult(intent, 1234)
            }

            binding.txtViewAllFarm -> {
                startActivity(Intent(requireActivity(), AllFarmListActivity::class.java))
            }

        }
    }


    private fun getMyFarmList() {
        val mDialog = Utils.fcreateDialog(requireActivity())
        val call: Call<MyFarmListResponse>? =
            ApiHandler.getApiService(requireContext())?.getFarm("Bearer " + data.app_key)
        call?.enqueue(object : Callback<MyFarmListResponse> {
            override fun onResponse(
                call: Call<MyFarmListResponse>,
                response: Response<MyFarmListResponse>
            ) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    binding.sliderViewFarmList.visibility = View.VISIBLE
                    binding.addFarmLayout.visibility = View.GONE
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    myFarfmListAdapter = MyFarmListAdapter(
                        requireContext(),
                        "farm",
                        resData.rows,
                        object : MyFarmListAdapter.OnItemClick {
                            override fun onItemClick(
                                myFarmListData: MyFarmListData,
                                position: Int,
                                s: String
                            ) {
                                if (s == "click") {
                                    val intent =
                                        Intent(requireContext(), MyFarmActivity::class.java)
                                    intent.putExtra("myFarmListData", myFarmListData)
                                    startActivity(intent)
                                }
                            }
                        })

                    binding.sliderViewFarmList.setSliderAdapter(myFarfmListAdapter)
                    binding.sliderViewFarmList.setIndicatorAnimation(IndicatorAnimationType.DROP)
                    binding.sliderViewFarmList.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                    binding.sliderViewFarmList.autoCycleDirection =
                        SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
                    binding.sliderViewFarmList.scrollTimeInSec = 3
                    binding.sliderViewFarmList.isAutoCycle = true
                    binding.sliderViewFarmList.startAutoCycle()
                } else {
                    if (response.code() == 400) {
                        binding.sliderViewFarmList.visibility = View.GONE
                        mDialog.dismiss()
                        binding.sliderViewFarmList.visibility = View.GONE
                        binding.addFarmLayout.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MyFarmListResponse>, t: Throwable) {
                mDialog.dismiss()
                binding.sliderViewFarmList.visibility = View.GONE
                binding.addFarmLayout.visibility = View.VISIBLE
                context?.toast(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1234) {
            if (resultCode == Activity.RESULT_OK) {
                getMyFarmList()
            }
        }
    }

    private fun initMembers() {
        builder = AlertDialog.Builder(requireActivity())
        connectivityManager =
            requireActivity().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkInfo = connectivityManager!!.activeNetworkInfo
        isConnected = networkInfo != null && networkInfo!!.isConnectedOrConnecting
        locationManager =
            requireActivity().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        permissionRequestResult =
            requireActivity().checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION")
    }

    private fun checkPermissionAndConnectivity(permissionRequestResult: Int, locationManager: LocationManager, isConnected: Boolean) {
        if (permissionRequestResult == 0) {
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (isConnected) {
                    startWheatherActivity()
                } else {
                    Snackbar.make(parentLayout, R.string.msg_internet, Snackbar.LENGTH_LONG)
                        .setAction("Ok") { requireActivity().finish() }
                        .setActionTextColor(parentLayout.resources.getColor(android.R.color.holo_red_light))
                        .show()
                }
            } else {
                Snackbar.make(parentLayout, R.string.msg_gps, Snackbar.LENGTH_LONG)
                    .setAction("Ok") { requireActivity().finish() }
                    .setActionTextColor(parentLayout.resources.getColor(android.R.color.holo_red_light))
                    .show()
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ACCESS_FINE_LOCATION
            )
        }
    }

    private fun startWheatherActivity() {
        Handler().postDelayed({
            startActivity(Intent(requireActivity(), ForecastActivity::class.java))
        }, 1000)
    }

    private fun getWeather(addressStringBuilder: StringBuilder, lang: String) {
        val call = Utility.getApis()
            .getWeatherForecastData(addressStringBuilder, Constants.API_KEY, Constants.UNITS, lang)
        call.enqueue(object : Callback<Forecast> {
            @SuppressLint("RestrictedApi", "SetTextI18n")
            override fun onResponse(call: Call<Forecast>, response: Response<Forecast>) {
                if (response.isSuccessful) {
                    weatherList = response.body()!!.dataObjectList

                    binding.tvCityName.text = response.body()!!.city.name
                    Pref.getValue(
                        requireContext(),
                        com.agrodrip.utils.Constants.PREF_ADDRESS,
                        response.body()!!.city.name
                    )
                    binding.ivWeatherIcon.setColorFilter(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.colorPrimary
                        ), PorterDuff.Mode.MULTIPLY
                    )
                    binding.tvDescWithTemp.text = weatherList!![0].main.temp.toInt()
                        .toString() + " " + getString(R.string.temp_unit)
                    binding.txtTemp.text = weatherList!![0].weather.get(0).description
                    when (weatherList!!.get(0).weather[0].icon) {
                        "01d" -> {
                            binding.ivWeatherIcon.setBackgroundResource(R.drawable.ic_weather_clear_sky)
                        }
                        "01n" -> {
                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_clear_sky)
                        }
                        "02d" -> {
                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_few_cloud)
                        }
                        "02n" -> {
                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_few_cloud)
                        }
                        "03d" -> {
                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_scattered_clouds)
                        }
                        "03n" -> {
                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_scattered_clouds)
                        }
                        "04d" -> {
                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_broken_clouds)
                        }
                        "04n" -> {
                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_broken_clouds)
                        }
                        "09d" -> {
                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_shower_rain)
                        }
                        "09n" -> {
                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_shower_rain)
                        }
                        "10d" -> {
                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_rain)
                        }
                        "10n" -> {
                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_rain)
                        }
                        "11d" -> {
                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_thunderstorm)
                        }
                        "11n" -> {
                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_thunderstorm)
                        }
                        "13d" -> {
                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_snow)
                        }
                        "13n" -> {
                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_snow)
                        }
                        "15d" -> {
                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_mist)
                        }
                        "15n" -> {
                            binding.ivWeatherIcon.setImageResource(R.drawable.ic_weather_mist)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<Forecast>, t: Throwable) {
                Log.e("ForecastActivity.TAG", "onFailure: " + t.message)
                Toast.makeText(requireContext(), getString(R.string.msg_failed), Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun detectLocation() {
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        weatherList = java.util.ArrayList()

        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                latitude = location.latitude
                longitude = location.longitude
            }

            override fun onStatusChanged(s: String, i: Int, bundle: Bundle) {}
            override fun onProviderEnabled(s: String) {}
            override fun onProviderDisabled(s: String) {
                Toast.makeText(
                    requireActivity(),
                    getString(R.string.txt_connect_network),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        val permissionCheck = ContextCompat.checkSelfPermission(
            requireActivity(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        if (permissionCheck == 0) {
            location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            locationManager!!.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                0,
                0f,
                locationListener
            )
        }
        if (location != null) {
            latitude = location!!.latitude
            longitude = location!!.longitude
        }
        try {
            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
            val addressList: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)
            addressStringBuilder = java.lang.StringBuilder()
            if (addressList.size > 0) {
                val locationAddress = addressList[0]
                for (i in 0..locationAddress.maxAddressLineIndex) {
                    locationAddress.getAddressLine(i)
                    addressStringBuilder!!.append(locationAddress.locality)
                }

            }
        } catch (e: Exception) {
            Log.e("TAG", "Exception: " + e.message)
        }
    }

    override fun onResume() {
        super.onResume()
        getMyFarmList()
        if (LocaleManager.getLanguagePref(requireActivity()) == LocaleManager.ENGLISH) {
            getWeather(addressStringBuilder!!, LocaleManager.ENGLISH)
        } else {
            getWeather(addressStringBuilder!!, LocaleManager.GUJARATI)
        }
    }

}