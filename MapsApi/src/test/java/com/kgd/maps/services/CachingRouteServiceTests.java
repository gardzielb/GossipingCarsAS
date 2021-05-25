//package com.kgd.maps.services;
//
//import com.kgd.maps.models.Route;
//import com.kgd.maps.repositories.PlaceRepository;
//import com.kgd.maps.repositories.RouteRepository;
//import org.bson.types.ObjectId;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mockito;
//import org.springframework.data.geo.Distance;
//import org.springframework.data.geo.Point;
//import uk.org.webcompere.systemstubs.environment.EnvironmentVariables;
//import uk.org.webcompere.systemstubs.jupiter.SystemStub;
//import uk.org.webcompere.systemstubs.jupiter.SystemStubsExtension;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//
//@ExtendWith(SystemStubsExtension.class)
//public class CachingRouteServiceTests {
//
//    @SystemStub
//    private EnvironmentVariables environmentVariables;
//
//    @Test
//    public void findRouteShouldReturnCachedRouteIfNoWaypointsRequestedAndRouteInCache() {
//        var placeRepositoryMock = Mockito.mock(PlaceRepository.class);
//
//        var cachedRoute = new Route(ObjectId.get(), new Point(21.37, 73.12), ObjectId.get(), new ArrayList<>());
//        var routeRepositoryMock = Mockito.mock(RouteRepository.class);
//        Mockito.when(routeRepositoryMock.findByOriginNearAndDestinationIdEquals(
//                eq(cachedRoute.origin()), any(Distance.class), eq(cachedRoute.destinationId())
//        )).thenReturn(List.of(cachedRoute));
//
//        environmentVariables.set("GOOGLE_API_KEY", "test");
//        var routeService = new CachingRouteService(routeRepositoryMock, placeRepositoryMock);
//        var foundRoute = routeService.findRoute(cachedRoute.origin(), cachedRoute.destinationId(), new Point[]{});
//
//        Assertions.assertEquals(foundRoute, cachedRoute);
//    }
//}
