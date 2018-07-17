package com.app.fo.mapboxnavi;

import android.Manifest;
import android.content.pm.PackageManager;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineListener;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.matching.v5.MapboxMapMatching;
import com.mapbox.api.matching.v5.models.MapMatchingResponse;
import com.mapbox.geojson.BoundingBox;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncher;
import com.mapbox.services.android.navigation.ui.v5.NavigationLauncherOptions;
import com.mapbox.services.android.navigation.v5.milestone.Milestone;
import com.mapbox.services.android.navigation.v5.milestone.MilestoneEventListener;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.NavigationEventListener;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;
import com.mapbox.services.android.navigation.v5.offroute.OffRouteListener;
import com.mapbox.services.android.navigation.v5.routeprogress.RouteProgress;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements PermissionsListener ,MilestoneEventListener {


    private MapView mapView;
    LocationEngine locationEngine;
    Location lastLocation;
    MapboxNavigation navigation;
    Point origin;
    Point destination;
    ArrayList<Point> routearray;
    PermissionsManager permissionsManager;
    TextView tvDirections;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Mapbox.getInstance(this, "pk.eyJ1IjoiaGVhZGJhbmc5MiIsImEiOiJjamF4dTFqdng3cXE1MzNxOHBlMjNldDR0In0.lRmltVlXuYhDU7bUet_qRw");

        mapView = (MapView) findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        tvDirections = findViewById(R.id.tvDirections);

        routearray = new ArrayList<>();

//        Point point1 = Point.fromLngLat(40.975322, 29.233392);
//        Point point2 = Point.fromLngLat(40.972095, 29.231290);
//        Point point3 = Point.fromLngLat(40.972095, 29.231290);
//        Point point4 = Point.fromLngLat(40.971662, 29.232907);




        Point point1 = Point.fromLngLat(40.9746, 29.2330);
        Point point2 = Point.fromLngLat(40.9750, 29.233267307281494);
        Point point3 = Point.fromLngLat(40.9750, 29.2333);
        Point point4 = Point.fromLngLat(40.9752,29.2334);
        Point point5 = Point.fromLngLat(40.9753,29.2334);
        Point point6 = Point.fromLngLat(40.975399017333984,29.233503341674805);
        Point point7 = Point.fromLngLat(40.97544193267822,29.233524799346924);
        Point point8 = Point.fromLngLat(40.97544193267822,29.23391103744507);
        Point point9 = Point.fromLngLat(40.9754204750061, 29.23434019088745);
        Point point10 = Point.fromLngLat(40.9754204750061,29.234769344329834);
        Point point11 = Point.fromLngLat(40.97533464431763,29.23483371734619);
        Point point12 = Point.fromLngLat(40.975141525268555,29.235005378723145);
        Point point13 = Point.fromLngLat(40.975120067596436, 29.235048294067383);
        Point point14 = Point.fromLngLat(40.97492694854736,29.23539161682129);
        Point point15 = Point.fromLngLat(40.97479820251465,29.235670566558838);
        Point point16 = Point.fromLngLat(40.97449779510498, 29.235498905181885);
        Point point17 = Point.fromLngLat(40.97428321838379 , 29.23534870147705);
        Point point18 = Point.fromLngLat(40.97400426864624,29.235198497772217);
        Point point19 = Point.fromLngLat(40.97393989562988 ,29.23515558242798);





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



        Log.i("route array", routearray.toString());

        permissionsManager = new PermissionsManager(this);

        if (PermissionsManager.areLocationPermissionsGranted(this)) {

        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }


        navigation = new MapboxNavigation(this, "pk.eyJ1IjoiaGVhZGJhbmc5MiIsImEiOiJjamF4dTFqdng3cXE1MzNxOHBlMjNldDR0In0.lRmltVlXuYhDU7bUet_qRw");
        // From Mapbox to The White House
        locationEngine = new LocationEngineProvider(getApplicationContext()).obtainBestLocationEngineAvailable();


        locationEngine.activate();
        locationEngine.addLocationEngineListener(new LocationEngineListener() {
            @Override
            public void onConnected() {
                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[]
                    // permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the
                    // documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                locationEngine.requestLocationUpdates();
            }

            @Override
            public void onLocationChanged(Location location) {

            }
        });

         lastLocation = locationEngine.getLastLocation();




        navigation.addOffRouteListener(new OffRouteListener() {
            @Override
            public void userOffRoute(Location location) {
                // Make the Map Matching request here
                // Call MapboxNavigation#startNavigation with successful response
                MapboxMapMatching.builder().accessToken(Mapbox.getAccessToken()).coordinates(routearray).steps(true).voiceInstructions(true).bannerInstructions(true).profile(DirectionsCriteria.PROFILE_DRIVING).build().enqueueCall(new Callback<MapMatchingResponse>() {



                    @Override
                    public void onResponse(Call<MapMatchingResponse> call, Response<MapMatchingResponse>
                            response) {
                        if (response.isSuccessful()) {

                            DirectionsRoute route = response.body().matchings().get(0).toDirectionRoute();
                            navigation.startNavigation(route);
                            navigation.setLocationEngine(locationEngine);

                            Log.i("deneme","offroute çalıştı");

                        }
                    }

                    @Override
                    public void onFailure(Call<MapMatchingResponse> call, Throwable t) {

                    }
                });
            }
        });

        navigation.addNavigationEventListener(new NavigationEventListener() {
            @Override
            public void onRunning(boolean running) {

            }
        });






    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mapView.onStart();

        if (locationEngine != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission
                    .ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat
                    .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                    PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationEngine.requestLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (locationEngine != null) {
            locationEngine.removeLocationUpdates();
        }

        mapView.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // End the navigation session
        navigation.endNavigation();
        navigation.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }


    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {

    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            // Permission sensitive logic called here
        } else {
            // User denied the permission
        }
    }


    @Override
    public void onMilestoneEvent(RouteProgress routeProgress, String instruction, Milestone
            milestone) {
        tvDirections.setText(instruction+" "+milestone.toString());

    }
}
