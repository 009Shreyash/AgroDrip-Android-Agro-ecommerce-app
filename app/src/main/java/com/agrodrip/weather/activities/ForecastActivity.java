package com.agrodrip.weather.activities;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.multidex.BuildConfig;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agrodrip.R;
import com.agrodrip.utils.LocaleManager;
import com.agrodrip.utils.Pref;
import com.agrodrip.weather.adapters.CardAdapter;
import com.agrodrip.weather.models.DataObject;
import com.agrodrip.weather.models.Forecast;
import com.agrodrip.weather.utilities.Constants;
import com.agrodrip.weather.utilities.Utility;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForecastActivity extends AppCompatActivity {
    private static final String TAG = ForecastActivity.class.getSimpleName();

    LocationManager locationManager;
    ProgressBar progressBar;
    ProgressDialog progressDialog;
    Location location;
    double latitute, longitude;
    Geocoder geocoder;
    StringBuilder addressStringBuilder;

    List<DataObject> weatherList;
    List<List<DataObject>> daysList;

    List<String> days;
    Set<String> distinctDays;
    CardAdapter cardAdapter;
    CardAdapter cardAdapter2;
    CardAdapter cardAdapter3;
    CardAdapter cardAdapter4;
    CardAdapter cardAdapter5;
    RecyclerView recyclerViewToday;
    RecyclerView recyclerViewTomorrow;
    RecyclerView recyclerViewLater;
    RecyclerView recyclerViewLater1;
    RecyclerView recyclerViewLater2;
    TabHost host;

    Toolbar toolbar;
    TextView toolbarText, txtChange;
    TextView txtLocation;
    LinearLayout shareWeatherLinear;
    ImageView imgBack;
    ImageView imageViewWeatherIcon;

    TextView tvTodayTemperature, tvTodayDescription, tvTodayWind, tvTodayPressure, tvTodayHumidity;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);
        Constants.TEMP_UNIT = " " + getResources().getString(R.string.temp_unit);
        initMember();
        initUi();
        detectLocation();
        host = findViewById(R.id.tabHostT);
        host.setup();

        Date date = new Date();
        int day = date.getDay();

        //Tab 1
        TabHost.TabSpec spec = host.newTabSpec(getString(R.string.txt_today));
        spec.setContent(R.id.tab1);
        spec.setIndicator(getString(R.string.txt_today));
        host.addTab(spec);

        //Tab 2
        spec = host.newTabSpec(getString(R.string.txt_tommorow));
        spec.setContent(R.id.tab2);
        spec.setIndicator(getString(R.string.txt_tommorow));
        host.addTab(spec);

        //Tab 3
        spec = host.newTabSpec(getString(R.string.txt_later));
        spec.setContent(R.id.tab3);
        spec.setIndicator(getString(R.string.txt_later));
        host.addTab(spec);


        recyclerViewToday = findViewById(R.id.my_recycler_view);
        recyclerViewTomorrow = findViewById(R.id.my_recycler_view2);
        recyclerViewLater = findViewById(R.id.my_recycler_view3);
        recyclerViewLater1 = findViewById(R.id.my_recycler_view4);
        recyclerViewLater2 = findViewById(R.id.my_recycler_view5);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager3 = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager4 = new LinearLayoutManager(this);
        LinearLayoutManager layoutManager5 = new LinearLayoutManager(this);
        recyclerViewToday.setLayoutManager(layoutManager);
        recyclerViewToday.setItemAnimator(new DefaultItemAnimator());

        recyclerViewTomorrow.setLayoutManager(layoutManager2);
        recyclerViewTomorrow.setItemAnimator(new DefaultItemAnimator());

        recyclerViewLater.setLayoutManager(layoutManager3);
        recyclerViewLater.setItemAnimator(new DefaultItemAnimator());

        recyclerViewLater1.setLayoutManager(layoutManager4);
        recyclerViewLater1.setItemAnimator(new DefaultItemAnimator());

        recyclerViewLater2.setLayoutManager(layoutManager5);
        recyclerViewLater2.setItemAnimator(new DefaultItemAnimator());

        if (!TextUtils.isEmpty(Pref.INSTANCE.getValue(ForecastActivity.this, Constants.PREF_ADDRESS, ""))) {
            addressStringBuilder = new StringBuilder();
            addressStringBuilder.append(Pref.INSTANCE.getValue(ForecastActivity.this, Constants.PREF_ADDRESS, ""));

            String language = Pref.INSTANCE.getValue(this, "language", "");
            if (language != null) {
                if (language.equals(LocaleManager.ENGLISH)) {
                    getWeather(addressStringBuilder, LocaleManager.ENGLISH);
                } else {
                    getWeather(addressStringBuilder, LocaleManager.ENGLISH);
                }
            }

        }

    }

    @Override
    public void onBackPressed() {
//        if (back_pressed + 2000 > System.currentTimeMillis()) super.onBackPressed();
//        else Toast.makeText(this, "Press once again to exit!", Toast.LENGTH_SHORT).show();
//        back_pressed = System.currentTimeMillis();
        super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * This method gets the device current location to call the weather api by default city
     */
    private void detectLocation() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                latitute = location.getLatitude();
                longitude = location.getLongitude();
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {
                Toast.makeText(ForecastActivity.this, R.string.txt_connect_network, Toast.LENGTH_SHORT).show();
            }
        };

        int permissionCheck = ContextCompat.checkSelfPermission(ForecastActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == 0) {
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        }
        if (location != null) {
            latitute = location.getLatitude();
            longitude = location.getLongitude();
        }

        try {
            geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addressList = geocoder.getFromLocation(latitute, longitude, 1);
            addressStringBuilder = new StringBuilder();
            if (addressList.size() > 0) {
                Address locationAddress = addressList.get(0);
                for (int i = 0; i <= locationAddress.getMaxAddressLineIndex(); i++) {
                    locationAddress.getAddressLine(i);
                    /*remove comment if you subLocality need to be shown*/
                    // addressStringBuilder.append(locationAddress.getSubLocality()).append(",");
                    addressStringBuilder.append(locationAddress.getLocality());
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

    public void showDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_search_city);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.WRAP_CONTENT
        );

        AppCompatEditText edtCityName = dialog.findViewById(R.id.edtCityName);

        TextView btnSearch = dialog.findViewById(R.id.btnSearch);
        TextView btnCancel = dialog.findViewById(R.id.btnCancel);


        btnSearch.setOnClickListener(v -> {
            String result = edtCityName.getText().toString().trim();
            if (!result.isEmpty()) {
                fetchUpdateOnSearched(result);
                dialog.dismiss();
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();

    }


    private void fetchUpdateOnSearched(String cityName) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(cityName);
        getWeather(stringBuilder, LocaleManager.GUJARATI);
    }


    private String getDate(Long milliTime) {
        Date currentDate = new Date(milliTime);
        SimpleDateFormat df = new SimpleDateFormat("dd");
        String date = df.format(currentDate);
        return date;
    }


    private void getWeather(StringBuilder addressStringBuilder, String lang) {
        progressDialog.show();
        Call<Forecast> call = Utility.getApis().getWeatherForecastData(addressStringBuilder,
                Constants.API_KEY, Constants.UNITS, "en");
        call.enqueue(new Callback<Forecast>() {
            @Override
            public void onResponse(Call<Forecast> call, Response<Forecast> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Log.i(TAG, "onResponse: " + response.isSuccessful());
                    weatherList = response.body().getDataObjectList();
                    distinctDays = new LinkedHashSet<>();
                    for (DataObject obj : weatherList) {
                        distinctDays.add(getDate(obj.getDt() * 1000));
                    }
                    Log.i("DISTINCTSIZE", distinctDays.size() + "");

                    days = new ArrayList<>();
                    days.addAll(distinctDays);

                    for (String day : days) {
                        List<DataObject> temp = new ArrayList<>();
                        Log.i("DAY", day);
                        for (DataObject data : weatherList) {
                            Log.i("ELEMENT", getDate(data.getDt() * 1000));
                            if (getDate(data.getDt() * 1000).equals(day)) {
                                Log.i("ADDEDDD", getDate(data.getDt() * 1000));
                                temp.add(data);
                            }
                        }
                        daysList.add(temp);
                    }

                    daysList.get(0).remove(0);

                    Log.i("DAYSLISTSIZE", daysList.size() + "");
                    cardAdapter = new CardAdapter(daysList.get(0));
                    recyclerViewToday.setAdapter(cardAdapter);

                    cardAdapter2 = new CardAdapter(daysList.get(1));
                    recyclerViewTomorrow.setAdapter(cardAdapter2);

                    cardAdapter3 = new CardAdapter(daysList.get(2));
                    recyclerViewLater.setAdapter(cardAdapter3);

                    cardAdapter4 = new CardAdapter(daysList.get(3));
                    recyclerViewLater1.setAdapter(cardAdapter4);

                    cardAdapter5 = new CardAdapter(daysList.get(4));
                    recyclerViewLater2.setAdapter(cardAdapter5);


                    txtLocation = findViewById(R.id.txtLocation);
                    txtLocation.setText(response.body().getCity().getName() + ", " + response.body().getCity().getCountry());
                    toolbar.setBackgroundResource(R.color.colorPrimary);

                    switch (weatherList.get(0).getWeather().get(0).getIcon()) {
                        case "01d":
                        case "01n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_clear_sky);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_clear_and_sunny));
                            break;
                        case "02d":
                        case "02n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_few_cloud);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_partly_cloudy));
                            break;
                        case "03d":
                        case "03n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_scattered_clouds);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_gusty_winds));
                            break;
                        case "04d":
                        case "04n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_broken_clouds);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_cloudy_overnight));
                            break;
                        case "09d":
                        case "09n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_shower_rain);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_hail_stroms));
                            break;
                        case "10d":
                        case "10n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_rain);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_heavy_rain));
                            break;
                        case "11d":
                        case "11n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_thunderstorm);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_thunderstroms));
                            break;
                        case "13d":
                        case "13n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_snow);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_snow));
                            break;
                        case "15d":
                        case "15n":
                            imageViewWeatherIcon.setImageResource(R.drawable.ic_weather_mist);
                            layout.setBackgroundColor(getResources().getColor(R.color.color_mix_snow_and_rain));
                            break;
                    }
                    tvTodayTemperature.setText((int) weatherList.get(0).getMain().getTemp() + " " + getString(R.string.temp_unit));
                    tvTodayDescription.setText(weatherList.get(0).getWeather().get(0).getDescription());
                    tvTodayWind.setText(getString(R.string.wind_lable) + " " + weatherList.get(0).getWind().getSpeed() + " " + getString(R.string.wind_unit));
                    tvTodayPressure.setText(getString(R.string.pressure_lable) + " " + weatherList.get(0).getMain().getPressure() + " " + getString(R.string.pressure_unit));
                    tvTodayHumidity.setText(getString(R.string.humidity_lable) + " " + weatherList.get(0).getMain().getHumidity() + " " + getString(R.string.humidity_unit));
                }
            }

            @Override
            public void onFailure(Call<Forecast> call, Throwable t) {
                progressDialog.dismiss();
                Log.e(TAG, "onFailure: " + t.getMessage());
                Toast.makeText(ForecastActivity.this, getString(R.string.msg_failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This method initialize the all ui member variables
     */
    private void initUi() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.progress));
        toolbar = findViewById(R.id.app_toolbar);
        toolbarText = toolbar.findViewById(R.id.toolbarText);
        txtChange = toolbar.findViewById(R.id.toolbarChangeLocation);
        txtChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        imgBack = toolbar.findViewById(R.id.imgBack);
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        setSupportActionBar(toolbar);
        imageViewWeatherIcon = findViewById(R.id.imageViewWeather);
        tvTodayTemperature = findViewById(R.id.todayTemperature);
        tvTodayDescription = findViewById(R.id.todayDescription);
        tvTodayWind = findViewById(R.id.todayWind);
        tvTodayPressure = findViewById(R.id.todayPressure);
        tvTodayHumidity = findViewById(R.id.todayHumidity);

        layout = findViewById(R.id.layoutWeather);

        shareWeatherLinear = findViewById(R.id.shareWeatherLinear);
        shareWeatherLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareApp();
            }
        });
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(
                Intent.EXTRA_TEXT,
                getResources().getString(R.string.share_app_desc) + BuildConfig.APPLICATION_ID
        );
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    /**
     * This method initialize the all member variables
     */
    private void initMember() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        weatherList = new ArrayList<>();
        daysList = new ArrayList<>();
    }
}
