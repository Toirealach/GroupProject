package com.example.finalproject2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.finalproject2.databinding.ActivityMapsBinding;
import com.google.maps.android.SphericalUtil;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private int Fine_Location_Access_Request_Code = 10001;
    private int Fine_BACKGRPOUNDLOCATION_Access_Request_Code = 10002;

    private double geofenceLat = 53.519819153272236;
    private double geofenceLong = -7.905038109516353;

    private double geofenceLat2 = 53.5197;
    private double geofenceLong2 = -7.9037;

    private LatLng latLng;
    String GEOFENCE_ID ="";

    private final static int MY_PERMISSIONS_REQUEST_LOCATION = 1;

    private Handler handler = new Handler();


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        handler.postDelayed(runnable, 1000);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},MY_PERMISSIONS_REQUEST_LOCATION);

        } else {
            String locationServiceName = Context.LOCATION_SERVICE;
            String serviceProvider = LocationManager.GPS_PROVIDER;
            LocationManager locationManager = (LocationManager) getSystemService(locationServiceName);
            Location location = locationManager.getLastKnownLocation(serviceProvider);
            updateWithNewLocation(location);
            checkForGeoFenceEntry(location);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setCostAllowed(true);
            criteria.setPowerRequirement(Criteria.POWER_LOW);
            String provider = locationManager.getBestProvider(criteria, true);

            locationManager.requestLocationUpdates(provider, 1000, 1, locationListener);
        }
    }

    private final LocationListener locationListener = new LocationListener() {
       @Override
        public void onLocationChanged(Location location) {
            checkForGeoFenceEntry(location);
        }

       @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
            // updateWithNewLocation(null);
        }
    };

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        latLng = new LatLng(53.51900438894117, -7.905732178339688);

        geofenceOne();
        geofenceTwo();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        enableUserLocation();
    }


    @SuppressLint("MissingPermission")
    private void enableUserLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Fine_Location_Access_Request_Code);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Fine_Location_Access_Request_Code);

            }

        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Fine_Location_Access_Request_Code) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //we have the permission
                mMap.setMyLocationEnabled(true);
            } else {
                //we dont have the permission
            }
        }
        if (requestCode == Fine_BACKGRPOUNDLOCATION_Access_Request_Code) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //we have the permission
                Toast.makeText(MapsActivity.this, "BG LOCATION PERMISSION GIVEN..NOW YOU CAN ADD GEOFENCES", Toast.LENGTH_SHORT).show();

            } else {
                //we dont have the permission
                Toast.makeText(MapsActivity.this, "BG LOCATION PERMISSION IS NECCESSARRY TO TRIGGER THE GEOFENCES", Toast.LENGTH_SHORT).show();

            }
        }
    }

    private void checkForGeoFenceEntry(Location location) {

        LatLng startLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        LatLng geofenceLatLng = new LatLng(geofenceLat, geofenceLong); // Center of geofence
        LatLng geofenceLatLng2 = new LatLng(geofenceLat2, geofenceLong2);

        double distanceInMeters = SphericalUtil.computeDistanceBetween(startLatLng, geofenceLatLng);
        double distanceInMeters2 = SphericalUtil.computeDistanceBetween(startLatLng, geofenceLatLng2);

        if (distanceInMeters < 40) {
            Toast.makeText(this, "inside geofence 1", Toast.LENGTH_LONG).show();
            on3 process1 = new on3();
            process1.execute();

        } else if (distanceInMeters2 < 40) {
            Toast.makeText(this, "inside geofence 2", Toast.LENGTH_LONG).show();
            on1 process1 = new on1();
            process1.execute();
        } else if (distanceInMeters2 > 40 && distanceInMeters2 > 40) {
            Toast.makeText(this, "outside geofence 1 & 2", Toast.LENGTH_LONG).show();
            on2 process1 = new on2();
            process1.execute();
        } else if (distanceInMeters < 40) {
            Toast.makeText(this, "outside geofence 1", Toast.LENGTH_LONG).show();
          //  on2 process2 = new on2();
          //  process2.execute();
        }else if (distanceInMeters2 < 40) {
            Toast.makeText(this, "outside geofence 2", Toast.LENGTH_LONG).show();
         //   on2 process2 = new on2();
         //   process2.execute();
        }

    }

    private void updateWithNewLocation(Location location){ ;
        if(location != null){
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
        }else {
        }

    }

    private void geofenceOne()
    {


        GEOFENCE_ID = "ONE";
        latLng = new LatLng(53.519819153272236, -7.905038109516353);

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(40);
        circleOptions.strokeColor(Color.argb(255,255,0,0));
        circleOptions.fillColor(Color.argb(64,255,0,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);

    }

    private void geofenceTwo()
    {


        GEOFENCE_ID = "TWO";
        latLng = new LatLng(53.5197, -7.9037);

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(latLng);
        circleOptions.radius(40);
        circleOptions.strokeColor(Color.argb(255,0,255,0));
        circleOptions.fillColor(Color.argb(64,0,255,0));
        circleOptions.strokeWidth(4);
        mMap.addCircle(circleOptions);

    }

}
