package rizvanov.dostoevskyassistant.fragment_epilepsy;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import java.util.Date;

public class LocationEventPusher extends Service implements LocationListener {

    private static final long MIN_DISTANCE_UPDATE = 1;
    private static final long MIN_TIME_BW_UPDATES = 5000;

    private Context context;

    private boolean isNetworkEnabled = false;
    private boolean isGPSEnabled = false;
    private boolean canGetLocation = false;

    private Location location;
    private double latitude;
    private double longitude;

    private LocationManager locationManager;

    public LocationEventPusher() {
    }

    public LocationEventPusher(Context context) {
        this.context = context;
        getLocation();
    }

    private Location getLocation() {
        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        boolean check1 = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        boolean check2 = ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED;
        if (check1 && check2) {
            return null;
        }
        if (isNetworkEnabled || isGPSEnabled) {
            canGetLocation = true;
            if (isNetworkEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_UPDATE,
                        this
                );
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
            if (isGPSEnabled) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_UPDATE,
                        this
                );
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }
            }
        }
        return location;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    @Override
    public void onProviderEnabled(String s) {}

    @Override
    public void onProviderDisabled(String s) {}

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    public String getCoordinates() {
        return formatLocation(location);
    }

    public boolean canGetLocation() {
        return canGetLocation;
    }

    public void stopUsingLocationTracker() {
        if (locationManager != null) {
            locationManager.removeUpdates(LocationEventPusher.this);
        }
    }
}