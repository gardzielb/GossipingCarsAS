package com.kgd.maps.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
import uk.org.webcompere.systemstubs.jupiter.SystemStub;
import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;

@ExtendWith(SystemStubsExtension.class)
public class GeoApiContextProviderTests {

    @SystemStub
    private EnvironmentVariables environmentVariables;

    @Test
    public void getApiContextShouldAlwaysReturnTheSameInstance() {
        environmentVariables.set("GOOGLE_API_KEY", "test");
        var apiKey1 = GeoApiContextProvider.getApiContext();
        var apiKey2 = GeoApiContextProvider.getApiContext();
        Assertions.assertSame(apiKey1, apiKey2);
    }
}
