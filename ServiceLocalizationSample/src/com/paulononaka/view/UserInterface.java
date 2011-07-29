package com.paulononaka.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.paulononaka.R;
import com.paulononaka.service.LocateService;

public class UserInterface extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.foreground_service_controller);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);

		Button button = (Button) findViewById(R.id.start_foreground);
		button.setOnClickListener(mForegroundListener);
		button = (Button) findViewById(R.id.start_background);
		button.setOnClickListener(mBackgroundListener);
		button = (Button) findViewById(R.id.stop);
		button.setOnClickListener(mStopListener);
	}
	
	private OnClickListener mForegroundListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(LocateService.ACTION_FOREGROUND);
			intent.setClass(UserInterface.this, LocateService.class);
			startService(intent);
		}
	};

	private OnClickListener mBackgroundListener = new OnClickListener() {
		public void onClick(View v) {
			Intent intent = new Intent(LocateService.ACTION_BACKGROUND);
			intent.setClass(UserInterface.this, LocateService.class);
			startService(intent);
		}
	};

	private OnClickListener mStopListener = new OnClickListener() {
		public void onClick(View v) {
			stopService(new Intent(UserInterface.this, LocateService.class));
		}
	};
}