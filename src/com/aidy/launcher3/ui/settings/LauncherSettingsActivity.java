package com.aidy.launcher3.ui.settings;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.MenuItem;

import com.aidy.launcher3.R;
import com.aidy.launcher3.ui.Launcher;

/**
 * 设置界面
 * 
 * @author mingxiaoli
 * 
 */
public class LauncherSettingsActivity extends PreferenceActivity implements OnPreferenceClickListener, OnPreferenceChangeListener {

	private static final String KEY_DEFAULT_LAUNCHER = "preference_default_launcher";
	private static final String KEY_LAUNCHER_LAYOUT = "preferencet_launcher_layout";
	private static final String KEY_THEME = "preferencet_theme";
	private static final String KEY_WALLPAPER = "preferencet_wallpaper";
	private static final String KEY_ICON = "preferencet_icon";
	private static final String KEY_EFFECT = "preferencet_effect";
	private static final String KEY_GESTURE = "preferencet_gesture";
	private static final String KEY_SYSTEM_SETTINGS = "preferencet_about_system_settings";
	private static final String KEY_ABOUT_LAUNCHER = "preferencet_about_launcher";

	private CheckBoxPreference mDefaultLauncher;

	public static void actionActivity(Context context) {
		Intent intent = new Intent(context, LauncherSettingsActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference_settings);
		setActionBar();
		mDefaultLauncher = (CheckBoxPreference) findPreference(KEY_DEFAULT_LAUNCHER);
		mDefaultLauncher.setOnPreferenceClickListener(this);
		findPreference(KEY_LAUNCHER_LAYOUT).setOnPreferenceClickListener(this);
		findPreference(KEY_THEME).setOnPreferenceClickListener(this);
		findPreference(KEY_WALLPAPER).setOnPreferenceClickListener(this);
		findPreference(KEY_ICON).setOnPreferenceClickListener(this);
		findPreference(KEY_EFFECT).setOnPreferenceClickListener(this);
		findPreference(KEY_GESTURE).setOnPreferenceClickListener(this);
		findPreference(KEY_SYSTEM_SETTINGS).setOnPreferenceClickListener(this);
		findPreference(KEY_ABOUT_LAUNCHER).setOnPreferenceClickListener(this);

		;
		mDefaultLauncher.setChecked(mDefaultLauncher.getSharedPreferences().getBoolean(KEY_DEFAULT_LAUNCHER, false));
	}

	private void setActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayUseLogoEnabled(true);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setHomeButtonEnabled(true);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		// TODO Auto-generated method stub
		Log.i("aidy", "onPreferenceClick() -- key = " + preference.getKey());
		// 设为默认壁纸
		if (KEY_DEFAULT_LAUNCHER.equals(preference.getKey())) {
			if (!mDefaultLauncher.getSharedPreferences().getBoolean(KEY_DEFAULT_LAUNCHER, false)) {
//				setDefaultLauncher();
			}
		} else if (KEY_LAUNCHER_LAYOUT.equals(preference.getKey())) {

		} else if (KEY_THEME.equals(preference.getKey())) {

		} else if (KEY_WALLPAPER.equals(preference.getKey())) {
			// mLauncher.startWallpaper();
		} else if (KEY_ICON.equals(preference.getKey())) {

		} else if (KEY_EFFECT.equals(preference.getKey())) {

		} else if (KEY_GESTURE.equals(preference.getKey())) {

		} else if (KEY_SYSTEM_SETTINGS.equals(preference.getKey())) {
			startActivity(new Intent("android.settings.SETTINGS"));
		} else if (KEY_ABOUT_LAUNCHER.equals(preference.getKey())) {

		}
		return false;
	}

	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		// TODO Auto-generated method stub
		Log.i("aidy", "onPreferenceChange() -- key = " + preference.getKey());
		return false;
	}

	private void setDefaultLauncher() {
		Log.i("aidy", "setDefaultLauncher()");
		PackageManager pm = getPackageManager();

		// 清除当前默认launcher
		ArrayList<IntentFilter> intentList = new ArrayList<IntentFilter>();
		ArrayList<ComponentName> componentNameList = new ArrayList<ComponentName>();
		pm.getPreferredActivities(intentList, componentNameList, null);
		IntentFilter intentFilter;
		for (int i = 0; i < componentNameList.size(); i++) {
			intentFilter = intentList.get(i);
			if (intentFilter.hasAction(Intent.ACTION_MAIN) && intentFilter.hasCategory(Intent.CATEGORY_HOME)) {
				pm.clearPackagePreferredActivities(componentNameList.get(i).getPackageName());
			}
		}

		// 获取所有launcher activity
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		List<ResolveInfo> list = new ArrayList<ResolveInfo>();
		list = pm.queryIntentActivities(intent, 0);

		// 获取所有launcher activity
		final int N = list.size();
		ComponentName[] set = new ComponentName[N];
		int bestMatch = 0;
		for (int i = 0; i < N; i++) {
			ResolveInfo r = list.get(i);
			set[i] = new ComponentName(r.activityInfo.packageName, r.activityInfo.name);
			if (r.match > bestMatch)
				bestMatch = r.match;
		}

		IntentFilter filter = new IntentFilter();
		filter.addAction("android.intent.action.MAIN");
		filter.addCategory("android.intent.category.HOME");
		filter.addCategory("android.intent.category.DEFAULT");

		ComponentName defaultLauncher = new ComponentName(getPackageName(), Launcher.class.getName());
		// 设置默认launcher
		pm.addPreferredActivity(filter, bestMatch, set, defaultLauncher);
	}
}
