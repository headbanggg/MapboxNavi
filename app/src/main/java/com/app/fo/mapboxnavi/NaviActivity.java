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
import com.mapbox.mapboxsdk.plugins.locationlayer.modes.CameraMode;
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
import com.mapbox.services.android.navigation.ui.v5.NavigationViewOptions;
import com.mapbox.services.android.navigation.ui.v5.OnNavigationReadyCallback;
import com.mapbox.services.android.navigation.ui.v5.route.NavigationMapRoute;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigation;
import com.mapbox.services.android.navigation.v5.navigation.MapboxNavigationOptions;
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
import com.mapbox.services.android.navigation.v5.route.RouteListener;
import com.mapbox.services.android.navigation.v5.utils.LocaleUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;


public class NaviActivity extends AppCompatActivity implements LocationEngineListener, PermissionsListener, com.mapbox.services.android.navigation.ui.v5.listeners.RouteListener {


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


    private NavigationView navigationView;

    MapboxNavigationOptions navigationOptions;



    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            Mapbox.getInstance(this, getString(R.string.access_token));
            setContentView(R.layout.activity_navi);


        navigationView =findViewById(R.id.navigationView);
        navigationView.onCreate(savedInstanceState);
        navigationView.initialize(new OnNavigationReadyCallback() {
            @Override
            public void onNavigationReady(boolean isRunning) {
                getRoute();
            }

        });

            //setContentView(R.layout.activity_draw_navigation);

/*            mapView = findViewById(R.id.mapView);
            mapView.onCreate(savedInstanceState);
        button = findViewById(R.id.startButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean simulateRoute = false;
                NavigationLauncherOptions options = NavigationLauncherOptions.builder()
                        .directionsRoute(directionsRoute)
                        .shouldSimulateRoute(simulateRoute)
                        .build();

                // Call this method with Context from within an Activity
                NavigationLauncher.startNavigation(NaviActivity.this, options);

            }
        });*/
/*

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
                            getRoute();
                            button.setEnabled(true);
                            button.setBackgroundResource(R.color.mapboxBlue);
                        }


                    });
                }
            });
*/





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
            locationEngine.setPriority(LocationEnginePriority.LOW_POWER);
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
           // mapView.onStart();
        }

        @Override
        public void onResume() {
            super.onResume();
           // mapView.onResume();
        }

        @Override
        public void onPause() {
            super.onPause();
            //mapView.onPause();
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
           // mapView.onStop();
        }

        @Override
        public void onLowMemory() {
            super.onLowMemory();
          //  mapView.onLowMemory();

        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
           // mapView.onDestroy();
            if (locationEngine != null) {
                locationEngine.deactivate();
            }
        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
           // mapView.onSaveInstanceState(outState);
        }


    private void getRoute() {

        List<Point> pointList = new ArrayList<>();
   /*     pointList.add(Point.fromLngLat(29.232760,40.974337 ));
        pointList.add(Point.fromLngLat(29.231857,40.972991 ));
        pointList.add(Point.fromLngLat(29.231857,40.972991 ));
        pointList.add(Point.fromLngLat(29.235857,40.972991 ));
        pointList.add(Point.fromLngLat(29.232305,40.971515));
*/

        pointList.add(Point.fromLngLat(29.809789336065183, 40.71711414829655));

        pointList.add(Point.fromLngLat(29.809448719024658,40.71704864501953));

        pointList.add(Point.fromLngLat(29.809105396270752,40.71702718734741));

        pointList.add(Point.fromLngLat(29.809019565582275,40.71702718734741));

        pointList.add(Point.fromLngLat(29.80886936187744,40.71700572967529));

        pointList.add(Point.fromLngLat(29.808783531188965,40.71700572967529));

        pointList.add(Point.fromLngLat(29.80865478515625,40.716984272003174));

        pointList.add(Point.fromLngLat(29.808483123779297,40.716962814331055));

        pointList.add(Point.fromLngLat(29.80841875076294,40.716962814331055));

        pointList.add(Point.fromLngLat(29.808204174041748,40.716941356658936));

        pointList.add(Point.fromLngLat(29.808096885681152,40.716941356658936));

        pointList.add(Point.fromLngLat(29.807989597320557,40.716941356658936));

        pointList.add(Point.fromLngLat(29.80768918991089,40.716941356658936));

        pointList.add(Point.fromLngLat(29.80738878250122,40.716962814331055));

        pointList.add(Point.fromLngLat(29.807109832763672,40.716984272003174));

        pointList.add(Point.fromLngLat(29.80687379837036,40.71700572967529));

        pointList.add(Point.fromLngLat(29.806723594665527,40.71702718734741));

        pointList.add(Point.fromLngLat(29.806551933288574,40.71702718734741));

        pointList.add(Point.fromLngLat(29.80644464492798,40.71700572967529));

        pointList.add(Point.fromLngLat(29.80638027191162,40.7168984413147));

        pointList.add(Point.fromLngLat(29.806337356567383,40.71685552597046));

        pointList.add(Point.fromLngLat(29.806315898895264,40.71683406829834));

        pointList.add(Point.fromLngLat(29.806315898895264,40.7167911529541));

        pointList.add(Point.fromLngLat(29.806294441223145,40.71676969528198));

        pointList.add(Point.fromLngLat(29.806230068206787,40.71666240692139));

        pointList.add(Point.fromLngLat(29.80618715286255,40.71657657623291));

        pointList.add(Point.fromLngLat(29.80612277984619,40.7163405418396));

        pointList.add(Point.fromLngLat(29.806079864501953,40.71625471115112));

        pointList.add(Point.fromLngLat(29.806036949157715,40.715932846069336));

        pointList.add(Point.fromLngLat(29.806015491485596,40.71580410003662));

        pointList.add(Point.fromLngLat(29.805994033813477,40.715675354003906));

        pointList.add(Point.fromLngLat(29.805994033813477,40.71554660797119));

        pointList.add(Point.fromLngLat(29.805972576141357,40.71516036987305));

        pointList.add(Point.fromLngLat(29.80595111846924,40.71485996246338));

        pointList.add(Point.fromLngLat(29.80595111846924,40.71479558944702));

        pointList.add(Point.fromLngLat(29.80592966079712,40.71466684341431));

        pointList.add(Point.fromLngLat(29.80592966079712,40.71460247039795));

        pointList.add(Point.fromLngLat(29.80592966079712,40.71455955505371));

        pointList.add(Point.fromLngLat(29.80592966079712,40.71449518203735));

        pointList.add(Point.fromLngLat(29.80592966079712,40.714452266693115));

        pointList.add(Point.fromLngLat(29.805972576141357,40.71438789367676));

        pointList.add(Point.fromLngLat(29.805994033813477,40.71434497833252));

        pointList.add(Point.fromLngLat(29.805994033813477,40.71408748626709));

        pointList.add(Point.fromLngLat(29.805994033813477,40.71382999420166));

        pointList.add(Point.fromLngLat(29.805994033813477,40.71365833282471));






        System.out.println("mine "+pointList.size());

getMapMatchingRoute(pointList);
    }

    private void getMapMatchingRoute(List<Point> points){
        MapboxMapMatching.builder()
                .accessToken(Mapbox.getAccessToken())
                .overview(DirectionsCriteria.OVERVIEW_FULL).voiceUnits("metric")
                .coordinates(points).language("tr")
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
                        launchNavigationWithRoute(false);
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

    private void launchNavigationWithRoute(Boolean shouldSimulateRoute) {
         navigationOptions = MapboxNavigationOptions.builder().enableOffRouteDetection(true).defaultMilestonesEnabled(true).snapToRoute(true)
                .build();



        NavigationViewOptions.Builder options_builder = NavigationViewOptions.builder().directionsRoute(directionsRoute)
                .shouldSimulateRoute(shouldSimulateRoute).routeListener(NaviActivity.this)
                .navigationOptions(navigationOptions);
        NavigationViewOptions options = options_builder.build();

        navigationView.startNavigation(options);

    }



    @Override
    public boolean allowRerouteFrom(Point offRoutePoint) {
        System.out.println("mine: rotadan çıktı false gönderildi");
        launchNavigationReroute(true);
        return false;
// Fetch new route with MapboxMapMatching

        // Create new options with map matching response route


    }

    @Override
    public void onOffRoute(Point offRoutePoint) {

        System.out.println("mine: rotadan çıktı ve tekrar navi açıldı");
    }

    @Override
    public void onRerouteAlong(DirectionsRoute directionsRoute) {

    }

    @Override
    public void onFailedReroute(String errorMessage) {

    }

    @Override
    public void onArrival() {

    }

    private void launchNavigationReroute(Boolean shouldSimulateRoute) {




        NavigationViewOptions.Builder options_builder = NavigationViewOptions.builder().directionsRoute(directionsRoute)
                .shouldSimulateRoute(shouldSimulateRoute).routeListener(NaviActivity.this)
                .navigationOptions(navigationOptions);
        NavigationViewOptions options = options_builder.build();

        navigationView.startNavigation(options);

    }
}

