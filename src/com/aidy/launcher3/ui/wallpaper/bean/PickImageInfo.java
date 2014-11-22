package com.aidy.launcher3.ui.wallpaper.bean;

import android.content.Intent;

import com.aidy.launcher3.support.utils.Utilities;
import com.aidy.launcher3.ui.wallpaper.WallpaperPickerActivity;
import com.aidy.launcher3.ui.wallpaper.support.WallpaperConstant;

public class PickImageInfo extends WallpaperTileInfo {
	@Override
	public void onClick(WallpaperPickerActivity a) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
		Utilities.startActivityForResultSafely(a, intent, WallpaperConstant.IMAGE_PICK);
	}
}
