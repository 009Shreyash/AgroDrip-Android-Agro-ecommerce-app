package com.agrodrip.weather.utilities;

public class Utility {
    private static final String TAG = Utility.class.getSimpleName();

    public static Apis getApis() {
        return Client.getClient().create(Apis.class);
    }
}
