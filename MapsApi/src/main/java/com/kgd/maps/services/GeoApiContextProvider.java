package com.kgd.maps.services;

import com.google.maps.GeoApiContext;

// This class serves as a 'singleton' proxy to GeoApiContext, which,
// according to Google's docs should be instantiated once and shared across the application
public class GeoApiContextProvider {
    private static GeoApiContext apiContext = null;

    public static GeoApiContext getApiContext() {
        if (apiContext != null)
            return apiContext;

        String apiKey = System.getenv("GOOGLE_API_KEY");
        if (apiKey == null) {
            throw new RuntimeException("Set GOOGLE_API_KEY env variable to your api key");
        }

        apiContext = new GeoApiContext.Builder().apiKey(apiKey).build();
        return apiContext;
    }
}
