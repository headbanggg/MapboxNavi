package com.app.fo.mapboxnavi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// classes needed to initialize map
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.RouteOptions;
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
import com.mapbox.services.android.navigation.ui.v5.NavigationView;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


public class NaviActivity extends AppCompatActivity implements LocationEngineListener, PermissionsListener {


        private MapView mapView;
        // variables for adding location layer
        private MapboxMap map;
        private PermissionsManager permissionsManager;
        private LocationLayerPlugin locationPlugin;
        private LocationEngine locationEngine;
        private Location originLocation;

    DirectionsRoute directionsRoute;


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

    private Button button;



    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Mapbox.getInstance(this, getString(R.string.access_token));
            setContentView(R.layout.activity_navi);
            //setContentView(R.layout.activity_draw_navigation);

            mapView = findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);
        button = findViewById(R.id.startButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean simulateRoute = true;
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(directionsRoute)
                        .shouldSimulateRoute(simulateRoute)
                        .build();

                // Call this method with Context from within an Activity
                NavigationLauncher.startNavigation(NaviActivity.this, options);

            }
        });

            mapView.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final MapboxMap mapboxMap) {


                    map = mapboxMap;
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



                }

                ;
            });





        }
        @SuppressWarnings( {"MissingPermission"})
        private void enableLocationPlugin() {
            // Check if permissions are enabled and if not request
            if (PermissionsManager.areLocationPermissionsGranted(this)) {
                // Create an instance of LOST location engine
                initializeLocationEngine();

                locationPlugin = new LocationLayerPlugin(mapView, map, locationEngine);
                locationPlugin.setRenderMode(RenderMode.COMPASS);
            } else {
                permissionsManager = new PermissionsManager(this);
                permissionsManager.requestLocationPermissions(this);
            }
        }

        @SuppressWarnings( {"MissingPermission"})
        private void initializeLocationEngine() {
            LocationEngineProvider locationEngineProvider = new LocationEngineProvider(this);
            locationEngine = locationEngineProvider.obtainBestLocationEngineAvailable();
            locationEngine.setPriority(LocationEnginePriority.HIGH_ACCURACY);
            locationEngine.activate();

            Location lastLocation = locationEngine.getLastLocation();
            if (lastLocation != null) {
                originLocation = lastLocation;
                setCameraPosition(lastLocation);
            } else {
                locationEngine.addLocationEngineListener(this);
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

        // Add the mapView's own lifecycle methods to the activity's lifecycle methods
        @Override
        @SuppressWarnings( {"MissingPermission"})
        public void onStart() {
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
        public void onStop() {
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
        public void onLowMemory() {
            super.onLowMemory();
            mapView.onLowMemory();

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
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            mapView.onSaveInstanceState(outState);
        }


    private void getRoute(Point origin, Point destination) {

        List<Point> pointList = new ArrayList<>();
        pointList.add(Point.fromLngLat(29.232760,40.974337 ));
        pointList.add(Point.fromLngLat(29.231857,40.972991 ));
        pointList.add(Point.fromLngLat(29.231857,40.972991 ));
        pointList.add(Point.fromLngLat(29.235857,40.972991 ));
        pointList.add(Point.fromLngLat(29.232305,40.971515));


                System.out.println("mine "+pointList.size());

getMapMatchingRoute(pointList);
    }

    private void getMapMatchingRoute(List<Point> points){
        MapboxMapMatching.builder()
                .accessToken(Mapbox.getAccessToken())
                .coordinates(points)
                .steps(true)
                .voiceInstructions(true)
                .bannerInstructions(true)
                .profile(DirectionsCriteria.PROFILE_DRIVING)
                .build()
                .enqueueCall(new Callback<MapMatchingResponse>() {
                    @Override
                    public void onResponse(Call<MapMatchingResponse> call, Response<MapMatchingResponse> response) {
                        if (response.isSuccessful()) {
                             directionsRoute = response.body().matchings().get(0).toDirectionRoute();
                            Log.i("directions route: ",directionsRoute.toString());
                            //navigation.startNavigation(route);
                            // Draw the route on the map
                            if (navigationMapRoute != null) {
                                navigationMapRoute.removeRoute();
                            } else {
                                navigationMapRoute = new NavigationMapRoute(null, mapView, map, R.style.NavigationMapRoute);
                            }
                            navigationMapRoute.addRoute(directionsRoute);

                        }
                    }

                    @Override
                    public void onFailure(Call<MapMatchingResponse> call, Throwable throwable) {
                    }
                });
    }

    private void startNavi(DirectionsRoute dirRoute,MapboxNavigation mapboxNavigation){
        mapboxNavigation.startNavigation(dirRoute);
    }
    }

