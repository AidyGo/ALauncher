package com.aidy.launcher3.ui.wallpaper.bean;

import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.aidy.launcher3.CropView;
import com.aidy.launcher3.ui.wallpaper.WallpaperCropActivity;
import com.aidy.launcher3.ui.wallpaper.WallpaperPickerActivity;
import com.aidy.launcher3.ui.wallpaper.support.WallpaperUtils;
import com.android.photos.BitmapRegionTileSource;

public class ResourceWallpaperInfo extends WallpaperTileInfo {
	private Resources mResources;
	private int mResId;
	private Drawable mThumb;

	public ResourceWallpaperInfo(Resources res, int resId, Drawable thumb) {
		mResources = res;
		mResId = resId;
		mThumb = thumb;
	}

	@Override
	public void onClick(WallpaperPickerActivity a) {
		int rotation = WallpaperUtils.getRotationFromExif(mResources, mResId);
		BitmapRegionTileSource source = new BitmapRegionTileSource(mResources, a, mResId, 1024, rotation);
		CropView v = a.getCropView();
		v.setTileSource(source, null);
		Point wallpaperSize = WallpaperUtils.getDefaultWallpaperSize(a.getResources(), a.getWindowManager());
		RectF crop = WallpaperUtils
				.getMaxCropRect(source.getImageWidth(), source.getImageHeight(), wallpaperSize.x, wallpaperSize.y, false);
		v.setScale(wallpaperSize.x / crop.width());
		v.setTouchEnabled(false);
	}

	@Override
	public void onSave(WallpaperPickerActivity a) {
		boolean finishActivityWhenDone = true;
		a.cropImageAndSetWallpaper(mResources, mResId, finishActivityWhenDone);
	}

	@Override
	public boolean isSelectable() {
		return true;
	}

	@Override
	public boolean isNamelessWallpaper() {
		return true;
	}

	public Drawable getmThumb() {
		return mThumb;
	}

}