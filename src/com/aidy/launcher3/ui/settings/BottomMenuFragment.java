package com.aidy.launcher3.ui.settings;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aidy.launcher3.R;
import com.aidy.launcher3.ui.Launcher;
import com.aidy.launcher3.ui.allapp.AppsCustomizePagedView;

public class BottomMenuFragment extends Fragment implements OnClickListener {

	private TextView mLightAuto;
	private SeekBar mLightSeekbar;
	
	private TextView mTvEditMode;
	private TextView mTvWallpaper;
	private TextView mTvWidget;
	private TextView mTvScreenManager;
	private TextView mTvFeedback;
	private TextView mTvUpdate;
	private TextView mTvLauncherSettigns;
	private TextView mTvSystemSettings;
	private Launcher mLauncher;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.bottom_layout_menu, null);
		mLightAuto = (TextView) view.findViewById(R.id.menu_light_auto);
		mLightSeekbar = (SeekBar) view.findViewById(R.id.menu_light_seekbar);
		
		mTvEditMode = (TextView) view.findViewById(R.id.menu_tv_edit_mode);
		mTvWallpaper = (TextView) view.findViewById(R.id.menu_tv_wallpaper);
		mTvWidget = (TextView) view.findViewById(R.id.menu_tv_widget);
		mTvScreenManager = (TextView) view.findViewById(R.id.menu_tv_screen_management);
		mTvFeedback = (TextView) view.findViewById(R.id.menu_tv_problem_feedback);
		mTvUpdate = (TextView) view.findViewById(R.id.menu_tv_version_update);
		mTvLauncherSettigns = (TextView) view.findViewById(R.id.menu_tv_launcher_settings);
		mTvSystemSettings = (TextView) view.findViewById(R.id.menu_tv_system_settings);

		mTvEditMode.setOnClickListener(this);
		mTvWallpaper.setOnClickListener(this);
		mTvWidget.setOnClickListener(this);
		mTvScreenManager.setOnClickListener(this);
		mTvFeedback.setOnClickListener(this);
		mTvUpdate.setOnClickListener(this);
		mTvLauncherSettigns.setOnClickListener(this);
		mTvSystemSettings.setOnClickListener(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		mLauncher = (Launcher) getActivity();

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.menu_tv_edit_mode:
			if(!mLauncher.getWorkspace().isInOverviewMode()) {				
				mLauncher.getWorkspace().enterOverviewMode();
			}
			break;
		case R.id.menu_tv_wallpaper:
			mLauncher.startWallpaper();
			break;
		case R.id.menu_tv_widget:
			mLauncher.showAllApps(true, AppsCustomizePagedView.ContentType.Widgets, true);
			break;
		case R.id.menu_tv_screen_management:
			Toast.makeText(getActivity(), "屏幕管理", Toast.LENGTH_SHORT).show();
			break;
		case R.id.menu_tv_problem_feedback:
			Toast.makeText(getActivity(), "问题反馈", Toast.LENGTH_SHORT).show();
			break;
		case R.id.menu_tv_version_update:
			Toast.makeText(getActivity(), "版本更新", Toast.LENGTH_SHORT).show();
			break;
		case R.id.menu_tv_launcher_settings:
			LauncherSettingsActivity.actionActivity(mLauncher);
			break;
		case R.id.menu_tv_system_settings:
			startActivity(new Intent("android.settings.SETTINGS"));
			break;
		default:
			break;
		}
		mLauncher.exitBottomMenu();
	}
}
