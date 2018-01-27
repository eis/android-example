package com.github.eis.myandroidapp.map;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class GPSTracker extends Service implements LocationListener {

    private final static String TAG = GPSTracker.class.getSimpleName();

    private final Context mContext;

    // Flag for GPS status
    private boolean isGPSEnabled = false;

    // Flag for network status
    private boolean isNetworkEnabled = false;

    // Flag for GPS status
    private boolean canGetLocation = false;

    private Location location;

    // The minimum distance to change in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 15 * 1; // 15 seconds

    // Declaring a Location Manager
    protected LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        locationManager = (LocationManager) mContext
                .getSystemService(LOCATION_SERVICE);

        // Getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        Log.d(TAG, isGPSEnabled ? "GPS enabled" : "GPS not enabled");

        // Getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        Log.d(TAG, isNetworkEnabled ? "Network enabled" : "Network not enabled");

        if (isGPSEnabled || isNetworkEnabled) {
            this.canGetLocation = true;
        }
    }

    public Location getLocation() throws SecurityException {
        if (location != null) {
            return location;
        }

        Location locationNet = null;
        if (isNetworkEnabled) {
            if (locationManager != null) {
                locationNet = locationManager
                        .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
        }
        Location locationGPS = null;
        // If GPS enabled, get latitude/longitude using GPS Services
        if (isGPSEnabled) {
            if (locationManager != null) {
                locationGPS = locationManager
                        .getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
        }

        long GPSLocationTime = 0;
        if (null != locationGPS) { GPSLocationTime = locationGPS.getTime(); }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if ( 0 < GPSLocationTime - NetLocationTime ) {
            return locationGPS;
        }
        else {
            return locationNet;
        }

    }

    public void registerForLocationUpdates() throws SecurityException {
        if (isNetworkEnabled) {
            locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }
        if (isGPSEnabled) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    MIN_TIME_BW_UPDATES,
                    MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
        }
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app.
     * */
    public void stopUsingGPS() {
        if (locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    /**
     * Function to check GPS/Wi-Fi enabled
     * @return boolean
     **/
    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    private OnLocationListener onLocationListener = null;

    public interface OnLocationListener {
        void onLocation(Location location);
    }
    public void registerOnLocation(OnLocationListener onLocationListener) {
        this.onLocationListener = onLocationListener;
    }

    @Override
    public void onLocationChanged(Location newLocation) {
        Log.d(TAG, "Got new location: " + newLocation);
        if (isBetterThanCurrent(newLocation)) {
            Log.d(TAG, "It is better than previous one, so it is stored");
            this.location = newLocation;
            if (onLocationListener != null) {
                onLocationListener.onLocation(location);
            }
        }
    }

    static final int TWO_MINUTES = 1000 * 60 * 2;
    /**
     * Determines whether one location reading is better than the current location fix
     * @param newLocation  The new location that you want to evaluate
     */
    protected boolean isBetterThanCurrent(Location newLocation) {
        if (location == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = newLocation.getTime() - location.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location,
        // because the user has likely moved.
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse.
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (newLocation.getAccuracy() - location.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(newLocation.getProvider(),
                location.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
    @Override
    public void onProviderDisabled(String provider) {
    }


    @Override
    public void onProviderEnabled(String provider) {
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }


    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}