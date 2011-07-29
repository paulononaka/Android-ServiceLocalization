package com.paulononaka.apihelper;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public abstract class MyLocationManager {

	private static final String TAG = "MyLocationManager";

	private Context mContext = null;
	private LocationManager mLocationManager = null;

	public MyLocationManager(Context context) {
		this.mContext = context;
		mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);;
	}

	private GPSLocationListener gpsLocationListener = new GPSLocationListener();
	private NetworkLocationListener networkLocationListener = new NetworkLocationListener();
	
	public abstract void positionReceived(Location location);

	private class GPSLocationListener implements LocationListener {

		public void onLocationChanged(Location location) {
			Log.d(TAG, "position received: " + location);
			if (location.getLatitude() == 0.0 && location.getLongitude() == 0.0) {
				return;
			}
			positionReceived(location);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	private class NetworkLocationListener implements LocationListener {

		public NetworkLocationListener() {
		}

		public void onLocationChanged(Location location) {
			Log.d(TAG, "position received: " + location);
			if (location.getLatitude() == 0.0 && location.getLongitude() == 0.0) {
				return;
			}
			positionReceived(location);
		}

		public void onProviderEnabled(String provider) {
		}

		public void onProviderDisabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	public void startLocationReceiving() {
		mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0F, gpsLocationListener);
		mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0F, networkLocationListener);
	}

	public void stopLocationReceiving() {
		mLocationManager.removeUpdates(gpsLocationListener);
		mLocationManager.removeUpdates(networkLocationListener);
	}
	
}