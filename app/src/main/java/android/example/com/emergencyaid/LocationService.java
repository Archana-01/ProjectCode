package android.example.com.emergencyaid;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import static android.content.ContentValues.TAG;


public class LocationService extends Service implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener  {
    GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest = new LocationRequest();
    public static final String LOCATION_BROADCAST = LocationService.class.getName() + "LocationBroadcast";
    public static final String ELATITUDE = "elatitude";
    public static final String ELONGITUDE = "elongitude";

    public LocationService() {
    }

    public int onStartCommand(Intent intent, int flags, int startId) {
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // earlier were 10000 and 5000
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationClient.connect();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),"In service Could not get location permission",Toast.LENGTH_SHORT).show();
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);
        //Toast.makeText(getApplicationContext(),"Connected to Google API",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");

        if (location != null) {
Toast.makeText(getApplicationContext(),"location is sent:"+location,Toast.LENGTH_SHORT).show();
            sendDataToActivity(String.valueOf(location.getLatitude()), String.valueOf(location.getLongitude()));
        }
        else
        {Toast.makeText(getApplicationContext(),"location not sent:"+location,Toast.LENGTH_SHORT).show();}
    }

    private void sendDataToActivity(String lat, String lng) {

        Intent intent = new Intent(LocationService.class.getName() + "LocationBroadcast");
        intent.putExtra(ELATITUDE, lat);
        intent.putExtra(ELONGITUDE, lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");

    }
}
