package com.app.fo.mapboxnavi;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

// classes needed to initialize map
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.matching.v5.MapboxMapMatching;
import com.mapbox.api.matching.v5.models.MapMatchingResponse;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;

// classes needed to add location layer
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;

import android.location.Location;

import com.mapbox.mapboxsdk.geometry.LatLng;

import android.support.annotation.NonNull;

import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.plugins.locationlayer.LocationLayerPlugin;
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.RenderMode;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEnginePriority;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;

// classes needed to add a marker
import com.mapbox.mapboxsdk.annotations.Marker;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;

// classes to calculate a route
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;

// classes needed to launch navigation UI
import android.view.View;
import android.widget.Button;

import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.v5.offroute.OffRouteListener;


// classes needed to add location layer


// classes needed to add a marker


// classes to calculate a route


// classes needed to launch navigation UI


public class NavigationActivity extends AppCompatActivity implements LocationEngineListener, PermissionsListener {

    private MapView mapView;
    MapboxNavigation navigation;


    // variables for adding location layer
    private MapboxMap map;
    private PermissionsManager permissionsManager;
    private LocationLayerPlugin locationPlugin;
    private LocationEngine locationEngine;
    private Location originLocation;


    // variables for adding a marker
    private Marker destinationMarker;
    private LatLng originCoord;
    private LatLng destinationCoord;


    // variables for calculating and drawing a route
    private Point originPosition;
    private Point destinationPosition;
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";
    private NavigationMapRoute navigationMapRoute;

    ArrayList<Point> routearray;
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.access_token));
        setContentView(R.layout.activity_navigationn);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        routearray = new ArrayList<>();

        //        Point point1 = Point.fromLngLat(40.975322, 29.233392);
        //        Point point2 = Point.fromLngLat(40.972095, 29.231290);
        //        Point point3 = Point.fromLngLat(40.972095, 29.231290);
        //        Point point4 = Point.fromLngLat(40.971662, 29.232907);




        Point point1 = Point.fromLngLat(29.2330,40.9746 );
        Point point2 = Point.fromLngLat(29.233267,40.9750);

        Point point3 = Point.fromLngLat(29.2333,40.9750);
        Point point4 = Point.fromLngLat(29.2334,40.9752);
        Point point5 = Point.fromLngLat(29.2334,40.9753);
        Point point6 = Point.fromLngLat(29.233503,40.975399);
        Point point7 = Point.fromLngLat(29.233524,40.975441);
        Point point8 = Point.fromLngLat(29.233911,40.975441);
        Point point9 = Point.fromLngLat(29.234340,40.975420);
        Point point10 = Point.fromLngLat(29.234769,40.975420);
        Point point11 = Point.fromLngLat(29.234833,40.975334);
        Point point12 = Point.fromLngLat(29.235005,40.975141);
        Point point13 = Point.fromLngLat(29.235048,40.975120);
        Point point14 = Point.fromLngLat(29.235391,40.974926);
        Point point15 = Point.fromLngLat(29.235670,40.974798);
        Point point16 = Point.fromLngLat(29.235498,40.974497);
        Point point17 = Point.fromLngLat(29.235348,40.974283);
        Point point18 = Point.fromLngLat(29.235198,40.974004);
        Point point19 = Point.fromLngLat(29.235155,40.973939);



        routearray.add(point1);
        routearray.add(point2);
        routearray.add(point3);
        routearray.add(point4);
        routearray.add(point5);
        routearray.add(point6);
        routearray.add(point7);
        routearray.add(point8);
        routearray.add(point9);
        routearray.add(point10);
        routearray.add(point11);
        routearray.add(point12);
        routearray.add(point13);
        routearray.add(point14);
        routearray.add(point15);
        routearray.add(point16);
        routearray.add(point17);
        routearray.add(point18);
        routearray.add(point19);

        navigation = new MapboxNavigation(getApplicationContext(), "pk.eyJ1IjoiaGVhZGJhbmc5MiIsImEiOiJjamF4dTFqdng3cXE1MzNxOHBlMjNldDR0In0.lRmltVlXuYhDU7bUet_qRw");

//
//        navigation.addOffRouteListener(new OffRouteListener() {
//            @Override
//            public void userOffRoute(Location location) {
//
//                //todo: buraya yoldan çıktığı zaman yapacakları yazılacak.
//                // Make the Map Matching request here
//                // Call MapboxNavigation#startNavigation with successful response
//            }
//        });

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(final MapboxMap mapboxMap) {


                map = mapboxMap;
                System.out.println("yey");
                enableLocationPlugin();


                originCoord = new LatLng(originLocation.getLatitude(), originLocation.getLongitude());
                mapboxMap.addOnMapClickListener(new MapboxMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(@NonNull LatLng point) {
                        if (destinationMarker != null) {
                            mapboxMap.removeMarker(destinationMarker);
                        }
                        destinationCoord = point;
                        destinationMarker = mapboxMap.addMarker(new MarkerOptions()
                                .position(destinationCoord)
                        );


                        destinationPosition = Point.fromLngLat(destinationCoord.getLongitude(), destinationCoord.getLatitude());
                        originPosition = Point.fromLngLat(originCoord.getLongitude(), originCoord.getLatitude());
                        getRoute(originPosition, destinationPosition);


                        button.setEnabled(true);
                        button.setBackgroundResource(R.color.mapboxBlue);


                    }

                    ;
                });


                button = findViewById(R.id.startButton);
                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        boolean simulateRoute = true;


                        MapboxMapMatching.builder()
                                .accessToken(Mapbox.getAccessToken())
                                .coordinates(routearray)
                                .steps(true)
                                .voiceInstructions(true)
                                .bannerInstructions(true)
                                .profile(DirectionsCriteria.PROFILE_DRIVING)
                                .build()
                                .enqueueCall(new Callback<MapMatchingResponse>() {

                                    @Override
                                    public void onResponse(Call<MapMatchingResponse> call, Response<MapMatchingResponse> response) {
                                        if (response.isSuccessful()) {
                                            System.out.println("asdf"+response.body());
                                            DirectionsRoute route = response.body().matchings().get(0).toDirectionRoute();

                                            NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                                                                            .directionsRoute(route)
                                                                            .shouldSimulateRoute(simulateRoute)
                                                                            .build();
                                            navigation.startNavigation(route);

                                            //NavigationLauncher.startNavigation(NavigationActivity.this, options);

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<MapMatchingResponse> call, Throwable throwable) {

                                    }
                                });

//
//                        // deneme için
//                        NavigationLauncherOptions options = NavigationLauncherOptions.builder()
//                                .directionsRoute(currentRoute)
//                                .shouldSimulateRoute(simulateRoute)
//                                .build();
//
//                        // Call this method with Context from within an Activity
//                        NavigationLauncher.startNavigation(NavigationActivity.this, options);
                    }
                });
            }
        });


    }


    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }

                        currentRoute = response.body().routes().get(0);
                        Log.i("currentrouet", currentRoute.toString());

                        // Draw the route on the map
                        if (navigationMapRoute != null) {
                            navigationMapRoute.removeRoute();
                        } else {
                            navigationMapRoute = new NavigationMapRoute(null, mapView, map, R.style.NavigationMapRoute);
                        }
                        navigationMapRoute.addRoute(currentRoute);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }


    @SuppressWarnings( {"MissingPermission"})
    private void enableLocationPlugin() {

        System.out.println("deneme");
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Create an instance of LOST location engine
            initializeLocationEngine();

            locationPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
            locationPlugin.setRenderMode(RenderMode.COMPASS);
        } else {
            System.out.println("deneme5");
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @SuppressWarnings( {"MissingPermission"})
    private void initializeLocationEngine() {

        System.out.println("deneme2");
        LocationEngineProvider locationEngineProvider = new LocationEngineProvider(this);
        locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
        locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
        locationEngine.activate();

        Location lastLocation = locationEngine.getLastLocation();
        if (lastLocation != null) {
            originLocation = lastLocation;
            setCameraPosition(lastLocation);
            System.out.println("deneme3");
        } else {
            locationEngine.addLocationEngineListener(this);
            System.out.println("deneme4");
        }
    }

    private void setCameraPosition(Location location) {
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(location.getLatitude(), location.getLongitude()), 13));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            enableLocationPlugin();
        } else {
            finish();
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    public void onConnected() {
        locationEngine.requestLocationUpdates();
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            originLocation = location;
            setCameraPosition(location);
            locationEngine.removeLocationEngineListener(this);
        }
    }

    @Override
    @SuppressWarnings( {"MissingPermission"})
    protected void onStart() {
        super.onStart();
        if (locationEngine != null) {
            locationEngine.requestLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStart();
        }
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }
        if (locationPlugin != null) {
            locationPlugin.onStop();
        }
        mapView.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if (locationEngine != null) {
            locationEngine.deactivate();
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


}