package com.paulononaka.apihelper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.paulononaka.R;
import com.paulononaka.view.UserInterface;


public abstract class ServiceManager extends Service {

	public static final String ACTION_FOREGROUND = "com.paulononaka.FOREGROUND";
	public static final String ACTION_BACKGROUND = "com.paulononaka.BACKGROUND";

	@SuppressWarnings("rawtypes")
	private static final Class[] mStartForegroundSignature = new Class[] { int.class, Notification.class };
	@SuppressWarnings("rawtypes")
	private static final Class[] mStopForegroundSignature = new Class[] { boolean.class };

	private NotificationManager mNM;
	private Method mStartForeground;
	private Method mStopForeground;
	private Object[] mStartForegroundArgs = new Object[2];
	private Object[] mStopForegroundArgs = new Object[1];

	public abstract void onStart();
	public abstract void onStop();

	@Override
	public void onCreate() {
		mNM = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		try {
			mStartForeground = getClass().getMethod("startForeground", mStartForegroundSignature);
			mStopForeground = getClass().getMethod("stopForeground", mStopForegroundSignature);
		} catch (NoSuchMethodException e) {
			// Running on an older platform.
			mStartForeground = mStopForeground = null;
		}
	}

	@Override
	public void onDestroy() {
		// Make sure our notification is gone.
		stopForegroundCompat(R.string.foreground_service_started);
		onStop();
	}

	// This is the old onStart method that will be called on the pre-2.0
	// platform. On 2.0 or later we override onStartCommand() so this
	// method will not be called.
	@Override
	public void onStart(Intent intent, int startId) {
		handleCommand(intent);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		handleCommand(intent);
		// We want this service to continue running until it is explicitly
		// stopped, so return sticky.
		return START_STICKY;
	}

	void handleCommand(Intent intent) {
		if (ACTION_FOREGROUND.equals(intent.getAction())) {
			// In this sample, we'll use the same text for the ticker and the
			// expanded notification
			CharSequence text = getText(R.string.foreground_service_started);

			// Set the icon, scrolling text and timestamp
			Notification notification = new Notification(R.drawable.img_droidcool, text, System.currentTimeMillis());

			// The PendingIntent to launch our activity if the user selects this
			// notification
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, new Intent(this, UserInterface.class), 0);

			// Set the info for the views that show in the notification panel.
			notification.setLatestEventInfo(this, "Serviço iniciado", text, contentIntent);

			startForegroundCompat(R.string.foreground_service_started, notification);

		} else if (ACTION_BACKGROUND.equals(intent.getAction())) {
			stopForegroundCompat(R.string.foreground_service_started);
		}
		onStart();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	/**
	 * This is a wrapper around the new startForeground method, using the older
	 * APIs if it is not available.
	 */
	void startForegroundCompat(int id, Notification notification) {
		// If we have the new startForeground API, then use it.
		if (mStartForeground != null) {
			mStartForegroundArgs[0] = Integer.valueOf(id);
			mStartForegroundArgs[1] = notification;
			try {
				mStartForeground.invoke(this, mStartForegroundArgs);
			} catch (InvocationTargetException e) {
				// Should not happen.
				Log.w("ApiDemos", "Unable to invoke startForeground", e);
			} catch (IllegalAccessException e) {
				// Should not happen.
				Log.w("ApiDemos", "Unable to invoke startForeground", e);
			}
		} else {
			// Fall back on the old API.
			setForeground(true);
			mNM.notify(id, notification);
		}
	}

	/**
	 * This is a wrapper around the new stopForeground method, using the older
	 * APIs if it is not available.
	 */
	void stopForegroundCompat(int id) {
		// If we have the new stopForeground API, then use it.
		if (mStopForeground != null) {
			mStopForegroundArgs[0] = Boolean.TRUE;
			try {
				mStopForeground.invoke(this, mStopForegroundArgs);
			} catch (InvocationTargetException e) {
				// Should not happen.
				Log.w("ApiDemos", "Unable to invoke stopForeground", e);
			} catch (IllegalAccessException e) {
				// Should not happen.
				Log.w("ApiDemos", "Unable to invoke stopForeground", e);
			}
		} else {
			// Fall back on the old API. Note to cancel BEFORE changing the
			// foreground state, since we could be killed at that point.
			mNM.cancel(id);
			setForeground(false);
		}
	}

}
