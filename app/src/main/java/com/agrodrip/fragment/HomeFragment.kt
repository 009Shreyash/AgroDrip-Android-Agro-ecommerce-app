package com.agrodrip.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.location.*
import android.location.Address
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
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
import com.agrodrip.BuildConfig
import com.agrodrip.R
import com.agrodrip.activity.AddYourProblemsActivity
import com.agrodrip.activity.MainActivity
import com.agrodrip.activity.VideoGalleryActivity
import com.agrodrip.activity.agroNews.AgroNewsActivity
import com.agrodrip.activity.agroNews.AgroNewsListActivity
import com.agrodrip.activity.dealOfTheDay.DealBannerListActivity
import com.agrodrip.activity.dealOfTheDay.DealOfBannerActivity
import com.agrodrip.activity.farm.*
import com.agrodrip.adapter.TopProductHomeAdapter
import com.agrodrip.adapter.agroNewsAdapter.AgroNewsAdapter
import com.agrodrip.adapter.bannerAdapter.SliderDealOfDayAdapter
import com.agrodrip.adapter.farmsAdapter.MyFarmListAdapter
import com.agrodrip.databinding.FragmentHomeBinding
import com.agrodrip.model.*
import com.agrodrip.utils.ItemOffsetDecoration
import com.agrodrip.utils.LocaleManager
import com.agrodrip.utils.Pref
import com.agrodrip.utils.Utils
import com.agrodrip.weather.activities.ForecastActivity
import com.agrodrip.weather.models.DataObject
import com.agrodrip.weather.models.Forecast
import com.agrodrip.weather.utilities.Constants
import com.agrodrip.weather.utilities.Utility
import com.agrodrip.webservices.ApiHandler
import com.agrodrip.webservices.snackBar
import com.google.android.material.snackbar.Snackbar
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView
import kotlinx.android.synthetic.main.content_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class HomeFragment : Fragment(), View.OnClickListener {

    private lateinit var agroNewsAdapter: AgroNewsAdapter
    private lateinit var dealBannerAdapter: SliderDealOfDayAdapter
    private lateinit var binding: FragmentHomeBinding
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
    private lateinit var data: UserData

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireContext() as MainActivity?)!!.onHidingToolbar(
            false,
            resources.getString(R.string.app_name)
        )
        (requireContext() as MainActivity?)!!.bottomNavigation.visibility = View.VISIBLE

        initWidget()

    }


    @SuppressLint("SetTextI18n")
    private fun initWidget() {
        data = Pref.getUserData(requireContext())!!
        //Welcome text
        initMembers()
        if (!TextUtils.isEmpty(data.name)) {
            binding.txtWelcome.text = getString(R.string.txt_hoe_are_you) + " " + data.name + " !"
        } else {
            binding.txtWelcome.text = getString(R.string.txt_hoe_are_you) + " !"
        }
        binding.btnAddFarm.setOnClickListener(this)
        binding.btnShareWhatsApp.setOnClickListener(this)
        binding.btnShareFacebook.setOnClickListener(this)
        binding.txtMoreVideo.setOnClickListener(this)
        binding.txtViewMoreDeal.setOnClickListener(this)
        binding.layoutWeather.setOnClickListener(this)
        binding.btnAllAgroNews.setOnClickListener(this)
        binding.txtViewAllProducts.setOnClickListener(this)
        binding.txtViewAllFarm.setOnClickListener(this)
        binding.llAddProblems.setOnClickListener(this)

        getMyFarmList()
        getNewsList()
        getBannerList()
        setTopProductAdapter()

        detectLocation()
        if (Pref.getValue(requireContext(), Constants.PREF_ADDRESS, "").toString().isNotEmpty()) {
            addressStringBuilder = StringBuilder()
            addressStringBuilder!!.append(
                Pref.getValue(
                    requireContext(),
                    Constants.PREF_ADDRESS,
                    ""
                ).toString()
            )
        }

        if (LocaleManager.getLanguagePref(requireActivity()) == LocaleManager.ENGLISH) {
            getWeather(addressStringBuilder!!, LocaleManager.ENGLISH)
        } else {
            getWeather(addressStringBuilder!!, LocaleManager.GUJARATI)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1234) {
            if (resultCode == Activity.RESULT_OK) {
                getMyFarmList()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        detectLocation()
        if (Pref.getValue(requireContext(), Constants.PREF_ADDRESS, "").toString().isNotEmpty()) {
            addressStringBuilder = StringBuilder()
            addressStringBuilder!!.append(
                Pref.getValue(
                    requireContext(),
                    Constants.PREF_ADDRESS,
                    ""
                ).toString()
            )
        }

        if (LocaleManager.getLanguagePref(requireActivity()) == LocaleManager.ENGLISH) {
            getWeather(addressStringBuilder!!, LocaleManager.ENGLISH)
        } else {
            getWeather(addressStringBuilder!!, LocaleManager.GUJARATI)
        }
    }


    private fun getMyFarmList() {
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
                    Log.d("getMyFarmList", "res$resData")
                    val myFarfmListAdapter = MyFarmListAdapter(
                        requireContext(),
                        "home",
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
                        binding.addFarmLayout.visibility = View.VISIBLE
                    }
                }
            }

            override fun onFailure(call: Call<MyFarmListResponse>, t: Throwable) {
                binding.sliderViewFarmList.visibility = View.GONE
                binding.addFarmLayout.visibility = View.VISIBLE
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }


    private fun setTopProductAdapter() {
        val mDialog = Utils.fcreateDialog(requireActivity())
        val call: Call<TopProductResponse>? =
            ApiHandler.getApiService(requireContext())?.topProductList()
        call?.enqueue(object : Callback<TopProductResponse> {
            override fun onResponse(
                call: Call<TopProductResponse>,
                response: Response<TopProductResponse>
            ) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    val topProductAdapter = TopProductHomeAdapter(
                        requireContext(),
                        resData.rows,
                        "Home",
                        object : TopProductHomeAdapter.OnItemClick {
                            override fun onItemClick(topProductData: TopProductData) {
                                val intent = Intent(
                                    requireContext(),
                                    SolutionProductActivity::class.java
                                )
                                intent.putExtra("topProductData", topProductData)
                                intent.putExtra("isFrom", "MarketPlaceFragment")
                                startActivity(intent)
                            }

                        })
                    val itemDecoration = ItemOffsetDecoration(context!!, R.dimen._2sdp)
                    binding.rvTopProductList.addItemDecoration(itemDecoration)
                    binding.rvTopProductList.adapter = topProductAdapter
                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<TopProductResponse>, t: Throwable) {
                mDialog.dismiss()
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }


    private fun initMembers() {
        builder = AlertDialog.Builder(requireContext())
        connectivityManager =
            requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkInfo = connectivityManager!!.activeNetworkInfo
        isConnected = networkInfo != null && networkInfo!!.isConnectedOrConnecting
        locationManager =
            requireContext().getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        permissionRequestResult =
            requireContext().checkCallingOrSelfPermission("android.permission.ACCESS_FINE_LOCATION")
    }


    private fun shareViaWhatsApp() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.whatsapp")
        intent.putExtra(
            Intent.EXTRA_TEXT,
            resources.getString(R.string.share_app_desc) + BuildConfig.APPLICATION_ID
        )
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=com.whatsapp")
                )
            )
        }
    }

    private fun shareViaFacebook() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.setPackage("com.facebook.katana")
        intent.putExtra(
            Intent.EXTRA_TEXT,
            resources.getString(R.string.share_app_desc) + BuildConfig.APPLICATION_ID
        )
        try {
            startActivity(intent)
        } catch (ex: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=com.facebook.katana")
                )
            )
        }
    }

    private fun checkPermissionAndConnectivity(
        permissionRequestResult: Int,
        locationManager: LocationManager,
        isConnected: Boolean
    ) {
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


    override fun onClick(v: View?) {
        when (v) {
            binding.btnAddFarm -> {
                val intent = Intent(requireContext(), AddFarmActivity::class.java)
                intent.putExtra("type", "add")
                startActivityForResult(intent, 1234)
            }
            binding.llAddProblems -> {
                val intent = Intent(requireContext(), AddYourProblemsActivity::class.java)
                startActivity(intent)
            }

            binding.btnShareWhatsApp -> {
                shareViaWhatsApp()
            }

            binding.btnShareFacebook -> {
                shareViaFacebook()
            }

            binding.txtMoreVideo -> {
                startActivity(Intent(requireContext(), VideoGalleryActivity::class.java))
            }

            binding.txtViewMoreDeal -> {
                startActivity(Intent(requireContext(), DealBannerListActivity::class.java))
            }

            binding.btnAllAgroNews -> {
                startActivity(Intent(requireContext(), AgroNewsListActivity::class.java))
            }

            binding.layoutWeather -> {
                initMembers()
                checkPermissionAndConnectivity(
                    permissionRequestResult,
                    locationManager!!,
                    isConnected
                )
            }

            binding.txtViewAllProducts -> {
                startActivity(Intent(requireActivity(), SpecialProductActivity::class.java))
            }

            binding.txtViewAllFarm -> {
                startActivity(Intent(requireActivity(), AllFarmListActivity::class.java))
            }
        }
    }

    private fun getBannerList() {
        val mDialog = Utils.fcreateDialog(requireContext())
        val call: Call<BannerResponse>? =
            ApiHandler.getApiService(requireContext())?.getBannerList()
        call?.enqueue(object : Callback<BannerResponse> {
            override fun onResponse(
                call: Call<BannerResponse>,
                response: Response<BannerResponse>
            ) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    mDialog.dismiss()
                    Log.d("res", "res$resData")
                    dealBannerAdapter = SliderDealOfDayAdapter(
                        requireContext(),
                        resData.rows,
                        object : SliderDealOfDayAdapter.OnItemClick {
                            override fun onItemClick(bannerData: BannerData) {
                                val intent =
                                    Intent(requireContext(), DealOfBannerActivity::class.java)
                                intent.putExtra("bannerData", bannerData)
                                startActivity(intent)
                            }
                        })
                    binding.sliderView.setSliderAdapter(dealBannerAdapter)
                    binding.sliderView.setIndicatorAnimation(IndicatorAnimationType.DROP)
                    binding.sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                    binding.sliderView.autoCycleDirection =
                        SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
                    binding.sliderView.scrollTimeInSec = 3
                    binding.sliderView.isAutoCycle = true
                    binding.sliderView.startAutoCycle()

                    binding.sliderView.setOnIndicatorClickListener {
                        Log.i(
                            "GGG",
                            "onIndicatorClicked: " + binding.sliderView.currentPagePosition
                        )
                    }
                } else {
                    if (response.code() == 400) {
                        mDialog.dismiss()
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<BannerResponse>, t: Throwable) {
                mDialog.dismiss()
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

    //News List
    private fun getNewsList() {
        val call: Call<NewsResponse>? = ApiHandler.getApiService(requireContext())?.getNewsList()
        call?.enqueue(object : Callback<NewsResponse> {
            override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                val resData = response.body()
                if (resData != null && response.isSuccessful) {
                    Log.d("res", "res$resData")
                    agroNewsAdapter = AgroNewsAdapter(
                        context!!,
                        resData.rows,
                        object : AgroNewsAdapter.OnItemClick {
                            override fun onItemClick(newsData: NewsData) {
                                val intent = Intent(requireContext(), AgroNewsActivity::class.java)
                                intent.putExtra("newsData", newsData)
                                startActivity(intent)
                            }
                        })
                    binding.sliderViewNews.setSliderAdapter(agroNewsAdapter)
                    binding.sliderViewNews.setIndicatorAnimation(IndicatorAnimationType.DROP)
                    binding.sliderViewNews.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                    binding.sliderViewNews.autoCycleDirection =
                        SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH
                    binding.sliderViewNews.scrollTimeInSec = 3
                    binding.sliderViewNews.isAutoCycle = true
                    binding.sliderViewNews.startAutoCycle()

                    binding.sliderViewNews.setOnIndicatorClickListener {
                        Log.i(
                            "GGG",
                            "onIndicatorClicked: " + binding.sliderViewNews.currentPagePosition
                        )
                    }
                } else {
                    if (response.code() == 400) {
                        binding.root.snackBar(response.message())
                    }
                }
            }

            override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                binding.root.snackBar(getString(R.string.msg_internet))
                Log.d("TAG", "onFailure: " + t.cause.toString())
            }
        })
    }

}