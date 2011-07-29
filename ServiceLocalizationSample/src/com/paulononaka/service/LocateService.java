package com.paulononaka.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.util.Log;

import com.paulononaka.R;
import com.paulononaka.apihelper.MyLocationManager;
import com.paulononaka.apihelper.ServiceManager;
import com.paulononaka.remote.RequestMethod;
import com.paulononaka.remote.RestClient;

public class LocateService extends ServiceManager {

	private static final String TAG = "LocateService";

	private static final int ID_NOTIFICATION = 0;
	
	private MyLocationManager myLocationManager;
	
	public LocateService () {
		super();
	}
	
	public static void start(Context context) {
		Intent intent = new Intent(LocateService.ACTION_FOREGROUND);
		intent.setClass(context, LocateService.class);
		context.startService(intent);
	}
	
	@Override
	public void onStart() {
		myLocationManager = new MyLocationManager(this) {
			
			@Override
			public void positionReceived(Location location) {

		        try
		        {
		        	RestClient client = new RestClient("http://servicelocalization.appspot.com/servicelocalizationserver");
		        	client.AddParam("lat", String.valueOf(location.getLatitude()));
		        	client.AddParam("lng", String.valueOf(location.getLongitude()));
		            client.Execute(RequestMethod.GET);
		            
		            String response = client.getResponse();
		            if (response != null) {
		            	Log.d(TAG, response);
		            	notificate(LocateService.this, R.drawable.img_droidcool, "Send", response, response);
		            }
		        }
		        catch (Exception e)
		        {
		        	Log.d(TAG, e.toString());
		        }
			}
		};
		myLocationManager.startLocationReceiving();
	}

	@Override
	public void onStop() {
		myLocationManager.stopLocationReceiving();
	}
	
	public static void notificate(Context context, int icon, String tickerText, String contentTitle, String contentText) {
		
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		long when = System.currentTimeMillis();

		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, null, 0);
		
		Notification notification = new Notification(icon, tickerText, when);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		
		mNotificationManager.cancel(ID_NOTIFICATION);
		mNotificationManager.notify(ID_NOTIFICATION, notification);
	}

}
