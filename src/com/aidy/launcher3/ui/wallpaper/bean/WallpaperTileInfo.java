package com.aidy.launcher3.ui.wallpaper.bean;

import android.view.View;

import com.aidy.launcher3.ui.wallpaper.WallpaperPickerActivity;

public abstract class WallpaperTileInfo {
	protected View mView;

	public void setView(View v) {
		mView = v;
	}

	public void onClick(WallpaperPickerActivity a) {
	}

	public void onSave(WallpaperPickerActivity a) {
	}

	public void onDelete(WallpaperPickerActivity a) {
	}

	public boolean isSelectable() {
		return false;
	}

	public boolean isNamelessWallpaper() {
		return false;
	}

	public void onIndexUpdated(CharSequence label) {
		if (isNamelessWallpaper()) {
			mView.setContentDescription(label);
		}
	}
}
