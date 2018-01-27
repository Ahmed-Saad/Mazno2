package com.softkey.mazno2.serviceHandler;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.softkey.mazno2.R;
import com.softkey.mazno2.parser.DataParser;
import com.softkey.mazno2.util.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ahmed Saad on 1/20/2018.
 */

public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    private String placesData, url;
    private GoogleMap mMap;


    @Override
    protected String doInBackground(Object... objects) {
        mMap = (GoogleMap) objects[0];
        url = (String) objects[1];
        DownloadUrl downloadUrl = new DownloadUrl();
        try {
            placesData = downloadUrl.readUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return placesData;
    }

    @Override
    protected void onPostExecute(String s) {
        DataParser parser = new DataParser();
        showNearbyPlaces(parser.parse(s));
    }

    private void showNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            HashMap<String, String> toiletPlaces = nearbyPlacesList.get(i);
            if (toiletPlaces.get("isOpen") != null) {
                if (toiletPlaces.get("isOpen").equals("1")) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    LatLng latLng = new LatLng(Double.parseDouble(toiletPlaces.get("lat")),
                            Double.parseDouble(toiletPlaces.get("lng")));
                    markerOptions.position(latLng);
                    markerOptions.title(toiletPlaces.get("placeName") + " : " + toiletPlaces.get("type"));
                    markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.toiletmarker));
                    mMap.addMarker(markerOptions);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            } else {
                MarkerOptions markerOptions = new MarkerOptions();
                LatLng latLng = new LatLng(Double.parseDouble(toiletPlaces.get("lat")),
                        Double.parseDouble(toiletPlaces.get("lng")));
                markerOptions.position(latLng);
                markerOptions.title(toiletPlaces.get("placeName") + " : " + toiletPlaces.get("type"));
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.toiletmarker));
                mMap.addMarker(markerOptions);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
            }
        }
    }

}
