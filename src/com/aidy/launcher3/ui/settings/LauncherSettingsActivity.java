package com.aidy.launcher3.ui.settings;

import com.aidy.launcher3.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * 设置界面
 * 
 * @author mingxiaoli
 * 
 */
public class LauncherSettingsActivity extends PreferenceActivity {

	public static void actionActivity(Context context) {
		Intent intent = new Intent(context, LauncherSettingsActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference_settings);
	}
}
