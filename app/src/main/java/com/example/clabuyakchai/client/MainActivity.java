package com.example.clabuyakchai.client;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends Activity {

    private TextView mPhoneUniqueId;
    private TextView mCoordinate;
    private Button mSend;

    private LocationManager mLocationManager;

    private String result;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        CryptoClass.testKey(MainActivity.this);

        mPhoneUniqueId = findViewById(R.id.phoneUniqueId);
        mCoordinate = findViewById(R.id.coordinate);
        mSend = findViewById(R.id.send);

        mPhoneUniqueId.setText(getDeviceUniqueID());

        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ServerAsync().execute();
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        isCheckSelfPermission();
        try{
            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 10, 10, locationListener);
            mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 10, 10, locationListener);
        }catch (Exception eo){
            Log.e("Main", eo.getMessage());
        }

    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    };

    private void isCheckSelfPermission(){
        if ( Build.VERSION.SDK_INT >= 23 &&
                ActivityCompat.checkSelfPermission( MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission( MainActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 2);
        }
    }

    private void showLocation(Location location) {
        if (location == null) {
            return;
        }
        mCoordinate.setText(formatLocation(location));
    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";

        String line = String.format(
                "time:%1$tF %1$tT,lat:%2$.4f,lng:%3$.4f",
                new Date(location.getTime()),location.getLatitude(), location.getLongitude() );

        String hash = CryptoClass.hasString(line);

        try {
            result = CryptoClass.encrypt(hash);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private String getDeviceUniqueID(){
        return Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeUpdates(locationListener);
    }

    private class ServerAsync extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return new RequestServer().startMethod(result, getDeviceUniqueID());
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

        }
    }
}
