package com.baurine.permissionutilsample.presenter;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.baurine.permissionutil.PermissionUtil;
import com.baurine.permissionutilsample.util.CheckPermissionUtil;

/**
 * Created by baurine on 2/18/17.
 */

public class LocationPresenter {

    public interface LocationView {
        void showLocationResult(String locationResult);
    }

    private LocationView locationView;

    //////////////////////////////////////
    private LocationManager locationManager;

    public LocationPresenter(Context context, LocationView locationView) {
        this.locationView = locationView;
        locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    //////////////////////////////////////
    public void requestLocation(Activity activity) {
        CheckPermissionUtil.checkLocation(activity,
                new PermissionUtil.ReqPermissionCallback() {
                    @Override
                    public void onResult(boolean success) {
                        if (success) {
                            updateLocation();
                        } else {
                            showLocation("disallowed!");
                        }
                    }
                });
    }

    @SuppressWarnings("MissingPermission")
    private void updateLocation() {
        String provider = LocationManager.NETWORK_PROVIDER;
        if (!locationManager.isProviderEnabled(provider)) {
            provider = LocationManager.GPS_PROVIDER;
        }
        if (!locationManager.isProviderEnabled(provider)) {
            showLocation("no provider!");
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            showLocation(location);
            return;
        }
        showLocation("no data!");
        locationManager.requestLocationUpdates(
                provider, 1000, 0.0f, new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        showLocation(location);
                        locationManager.removeUpdates(this);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                    }
                }
        );
    }

    private void showLocation(Location location) {
        String ret = "\nlat " + location.getLatitude()
                + "\nlng " + location.getLongitude();
        showLocation(ret);
    }

    private void showLocation(String str) {
        locationView.showLocationResult(str);
    }
}
