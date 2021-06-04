package com.agrodrip.weather.utilities;


import com.agrodrip.weather.models.Forecast;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface Apis {

    /*http://api.openweathermap.org/data/2.5/forecast/daily?q=tehran,ir&cnt=16&units=metric&
     lang=fa&appid=1487dd8a93bfd85d278d9ayty875ghyy*/

    @GET("forecast")
    Call<Forecast> getWeatherForecastData(@Query("q") StringBuilder cityName, @Query("APPID") String APIKEY,
                                          @Query("units") String TempUnit,@Query("lang") String lang);

}
