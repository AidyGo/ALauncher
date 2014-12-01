package com.aidy.launcher3.ui.wallpaper.bean;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;

import com.aidy.launcher3.photos.BitmapRegionTileSource;
import com.aidy.launcher3.support.CropView;
import com.aidy.launcher3.ui.wallpaper.WallpaperCropActivity.OnBitmapCroppedHandler;
import com.aidy.launcher3.ui.wallpaper.WallpaperPickerActivity;
import com.aidy.launcher3.ui.wallpaper.support.WallpaperUtils;

public class UriWallpaperInfo extends WallpaperTileInfo {
	private Uri mUri;

	public UriWallpaperInfo(Uri uri) {
		mUri = uri;
	}

	@Override
	public void onClick(WallpaperPickerActivity a) {
		CropView v = a.getCropView();
		int rotation = WallpaperUtils.getRotationFromExif(a, mUri);
		v.setTileSource(new BitmapRegionTileSource(a, mUri, 1024, rotation), null);
		v.setTouchEnabled(true);
	}

	@Override
	public void onSave(final WallpaperPickerActivity a) {
		boolean finishActivityWhenDone = true;
		OnBitmapCroppedHandler h = new OnBitmapCroppedHandler() {
			public void onBitmapCropped(byte[] imageBytes) {
				Point thumbSize = WallpaperUtils.getDefaultThumbnailSize(a.getResources());
				// rotation is set to 0 since imageBytes has already been
				// correctly rotated
				Bitmap thumb = WallpaperUtils.createThumbnail(thumbSize, null, null, imageBytes, null, 0, 0, true);
				a.getSavedImages().writeImage(thumb, imageBytes);
			}
		};
		a.cropImageAndSetWallpaper(mUri, h, finishActivityWhenDone);
	}

	@Override
	public boolean isSelectable() {
		return true;
	}

	@Override
	public boolean isNamelessWallpaper() {
		return true;
	}
}
