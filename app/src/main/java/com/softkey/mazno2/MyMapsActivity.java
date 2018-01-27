package com.softkey.mazno2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.softkey.mazno2.serviceHandler.GetDirectionsData;
import com.softkey.mazno2.util.Utils;

public class MyMapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest locationRequest;
    private static final int REQUEST_LOCATION_CODE = 99;
    double latitude, longitude, placeLat, placeLong;
    Object dataTransfer[];
    int PROXIMITY_RADIUS = 1000;
    private int zoomLevel;
    private TextView durationTv, distanceTv, placeType, placeName;
    private ImageView placeImg;
    private AdView mAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.myMap);
        mapFragment.getMapAsync(this);
        placeLat = Double.parseDouble(getIntent().getStringExtra("lat"));
        placeLong = Double.parseDouble(getIntent().getStringExtra("lang"));
        latitude = Double.parseDouble(getIntent().getStringExtra("originLat"));
        longitude = Double.parseDouble(getIntent().getStringExtra("originLang"));
        mAdView = findViewById(R.id.navAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        durationTv = findViewById(R.id.place_eta_val);
        distanceTv = findViewById(R.id.place_dis_val);
        placeImg = findViewById(R.id.place_img);
        placeType = findViewById(R.id.place_type);
        placeName = findViewById(R.id.place_name);
        placeType.setText(getIntent().getStringExtra("placeType"));
        placeName.setText(getIntent().getStringExtra("placeName"));
        updateImage(getIntent().getStringExtra("placeType").trim());
    }


    private void updateImage(String type){
        if (type.equals("hospital")){
           placeImg.setImageResource(R.drawable.hostpital);
        } else if (type.equals("cafe")) {
            placeImg.setImageResource(R.drawable.cafe);
        } else if (type.equals("restaurant")) {
            placeImg.setImageResource(R.drawable.resturant);
        } else if (type.equals("gas_station")) {
            placeImg.setImageResource(R.drawable.gas);
        } else if (type.equals("subway_station")) {
            placeImg.setImageResource(R.drawable.metro);
        } else if (type.equals("train_station")) {
            placeImg.setImageResource(R.drawable.metro);
        } else if (type.equals("transit_station")) {
            placeImg.setImageResource(R.drawable.restimg);
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            bulidGoogleApiClient();
            mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.getUiSettings().setMapToolbarEnabled(false);
        }
        showNavigation(null);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        locationRequest = new LocationRequest().create();
        locationRequest.setInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(client, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            Utils.makeShortToast("Cannot find your location", this);
        } else {
            showNavigation(location);
        }
    }

    private void showNavigation(Location location){
        mMap.clear();
        if (location != null){
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        LatLng latLng = new LatLng(latitude, longitude);
        LatLng placeLatLng = new LatLng(placeLat, placeLong);
        float dis = calculateDistance(latitude,
                longitude,placeLat,placeLong);
        Circle circle = mMap.addCircle(new CircleOptions().center(latLng).radius(dis).strokeColor(Color.RED));
        circle.setVisible(false);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, getZoomLevel(circle)));
        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.icon(BitmapDescriptorFactory.fromResource(R.drawable.personmarker));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, getZoomLevel(circle)));
        MarkerOptions placeOptions = new MarkerOptions();
        placeOptions.position(placeLatLng);
        placeOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.toiletmarker));
        mMap.addMarker(placeOptions);
        mMap.addMarker(options);
        route();
        String disStr = Math.round(dis)+"";
        distanceTv.setText(disStr + "M");
        if (disStr.length() == 4){
            durationTv.setText(disStr.charAt(0)+disStr.charAt(1)+" Min");
        } else {
            durationTv.setText(disStr.charAt(0)+" Min");
        }
        /*PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.add(latLng);
        lineOptions.add(placeLatLng);
        lineOptions.width(10);
        lineOptions.color(Color.RED);
        if(lineOptions != null) {
            mMap.addPolyline(lineOptions);
        }
        else {
            Log.d("onPostExecute","without Polylines drawn");
        }*/
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        if (client == null) {
                            bulidGoogleApiClient();
                        }
                        mMap.setMyLocationEnabled(false);
                        mMap.getUiSettings().setCompassEnabled(true);
                        mMap.getUiSettings().setMapToolbarEnabled(false);
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                }
        }
    }

    protected synchronized void bulidGoogleApiClient() {
        client = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        client.connect();
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_CODE);
            }
            return false;

        } else
            return true;
    }

    private String getDirectionsUrl()
    {
        StringBuilder googleDirectionsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        googleDirectionsUrl.append("origin="+latitude+","+longitude);
        googleDirectionsUrl.append("&destination="+placeLat+","+placeLong);
        googleDirectionsUrl.append("&mode=walking");
        googleDirectionsUrl.append("&key="+"AIzaSyBdESSFLBS5o4Tk-uEiWxoTwcrgUSETBzI");
        Log.d("Duration Url = ", googleDirectionsUrl.toString());
        return googleDirectionsUrl.toString();
    }

    private void route(){
        dataTransfer = new Object[5];
        String url = getDirectionsUrl();
        GetDirectionsData getDirectionsData = new GetDirectionsData();
        dataTransfer[0] = mMap;
        dataTransfer[1] = url;
        dataTransfer[3] = new LatLng(placeLat, placeLong);
        getDirectionsData.execute(dataTransfer);
    }

    private Float calculateDistance(double latA, double langA, double latB, double langB){
        Location locationA = new Location("pointA");
        Location locationB = new Location("pointB");
        locationA.setLatitude(latA);
        locationA.setLongitude(langA);
        locationB.setLatitude(latB);
        locationB.setLongitude(langB);
        return locationA.distanceTo(locationB);
    }

    public int getZoomLevel(Circle circle) {
        if (circle != null){
            double radius = circle.getRadius();
            double scale = radius / 500;
            zoomLevel =(int) (16 - Math.log(scale) / Math.log(2));
        }
        return zoomLevel;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
