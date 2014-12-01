/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aidy.launcher3.ui.wallpaper;

import android.app.ActionBar;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;

import com.aidy.launcher3.R;
import com.aidy.launcher3.photos.BitmapRegionTileSource;
import com.aidy.launcher3.support.CropView;
import com.aidy.launcher3.ui.wallpaper.support.BitmapCropTask;
import com.aidy.launcher3.ui.wallpaper.support.WallpaperConstant;
import com.aidy.launcher3.ui.wallpaper.support.WallpaperUtils;

public class WallpaperCropActivity extends Activity {
	private static final String LOGTAG = "Launcher3.CropActivity";

	/**
	 * The maximum bitmap size we allow to be returned through the intent.
	 * Intents have a maximum of 1MB in total size. However, the Bitmap seems to
	 * have some overhead to hit so that we go way below the limit here to make
	 * sure the intent stays below 1MB.We should consider just returning a byte
	 * array instead of a Bitmap instance to avoid overhead.
	 */

	protected CropView mCropView;
	protected Uri mUri;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
		if (!enableRotation()) {
			setRequestedOrientation(Configuration.ORIENTATION_PORTRAIT);
		}
	}

	protected void init() {
		setContentView(R.layout.wallpaper_cropper);

		mCropView = (CropView) findViewById(R.id.cropView);

		Intent cropIntent = getIntent();
		final Uri imageUri = cropIntent.getData();

		if (imageUri == null) {
			Log.e(LOGTAG, "No URI passed in intent, exiting WallpaperCropActivity");
			finish();
			return;
		}

		int rotation = WallpaperUtils.getRotationFromExif(this, imageUri);
		mCropView.setTileSource(new BitmapRegionTileSource(this, imageUri, 1024, rotation), null);
		mCropView.setTouchEnabled(true);
		// Action bar
		// Show the custom action bar view
		final ActionBar actionBar = getActionBar();
		actionBar.setCustomView(R.layout.actionbar_set_wallpaper);
		actionBar.getCustomView().setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean finishActivityWhenDone = true;
				cropImageAndSetWallpaper(imageUri, null, finishActivityWhenDone);
			}
		});
	}

	public boolean enableRotation() {
		return getResources().getBoolean(R.bool.allow_rotation);
	}

	public void setWallpaper(String filePath, final boolean finishActivityWhenDone) {
		int rotation = WallpaperUtils.getRotationFromExif(filePath);
		BitmapCropTask cropTask = new BitmapCropTask(this, filePath, null, rotation, 0, 0, true, false, null);
		final Point bounds = cropTask.getImageBounds();
		Runnable onEndCrop = new Runnable() {
			public void run() {
				updateWallpaperDimensions(bounds.x, bounds.y);
				if (finishActivityWhenDone) {
					setResult(Activity.RESULT_OK);
					finish();
				}
			}
		};
		cropTask.setOnEndRunnable(onEndCrop);
		cropTask.setNoCrop(true);
		cropTask.execute();
	}

	public void cropImageAndSetWallpaper(Resources res, int resId, final boolean finishActivityWhenDone) {
		// crop this image and scale it down to the default wallpaper size for
		// this device
		int rotation = WallpaperUtils.getRotationFromExif(res, resId);
		Point inSize = mCropView.getSourceDimensions();
		Point outSize = WallpaperUtils.getDefaultWallpaperSize(getResources(), getWindowManager());
		RectF crop = WallpaperUtils.getMaxCropRect(inSize.x, inSize.y, outSize.x, outSize.y, false);
		Runnable onEndCrop = new Runnable() {
			public void run() {
				// Passing 0, 0 will cause launcher to revert to using the
				// default wallpaper size
				updateWallpaperDimensions(0, 0);
				if (finishActivityWhenDone) {
					setResult(Activity.RESULT_OK);
					finish();
				}
			}
		};
		BitmapCropTask cropTask = new BitmapCropTask(this, res, resId, crop, rotation, outSize.x, outSize.y, true, false, onEndCrop);
		cropTask.execute();
	}

	public void cropImageAndSetWallpaper(Uri uri, OnBitmapCroppedHandler onBitmapCroppedHandler, final boolean finishActivityWhenDone) {
		// Get the crop
		boolean ltr = mCropView.getLayoutDirection() == View.LAYOUT_DIRECTION_LTR;

		Point minDims = new Point();
		Point maxDims = new Point();
		Display d = getWindowManager().getDefaultDisplay();
		d.getCurrentSizeRange(minDims, maxDims);

		Point displaySize = new Point();
		d.getSize(displaySize);

		int maxDim = Math.max(maxDims.x, maxDims.y);
		final int minDim = Math.min(minDims.x, minDims.y);
		int defaultWallpaperWidth;
		if (WallpaperUtils.isScreenLarge(getResources())) {
			defaultWallpaperWidth = (int) (maxDim * WallpaperUtils.wallpaperTravelToScreenWidthRatio(maxDim, minDim));
		} else {
			defaultWallpaperWidth = Math.max((int) (minDim * WallpaperConstant.WALLPAPER_SCREENS_SPAN), maxDim);
		}

		boolean isPortrait = displaySize.x < displaySize.y;
		int portraitHeight;
		if (isPortrait) {
			portraitHeight = mCropView.getHeight();
		} else {
			// TODO: how to actually get the proper portrait height?
			// This is not quite right:
			portraitHeight = Math.max(maxDims.x, maxDims.y);
		}
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Point realSize = new Point();
			d.getRealSize(realSize);
			portraitHeight = Math.max(realSize.x, realSize.y);
		}
		// Get the crop
		RectF cropRect = mCropView.getCrop();
		int cropRotation = mCropView.getImageRotation();
		float cropScale = mCropView.getWidth() / (float) cropRect.width();

		Point inSize = mCropView.getSourceDimensions();
		Matrix rotateMatrix = new Matrix();
		rotateMatrix.setRotate(cropRotation);
		float[] rotatedInSize = new float[] { inSize.x, inSize.y };
		rotateMatrix.mapPoints(rotatedInSize);
		rotatedInSize[0] = Math.abs(rotatedInSize[0]);
		rotatedInSize[1] = Math.abs(rotatedInSize[1]);

		// ADJUST CROP WIDTH
		// Extend the crop all the way to the right, for parallax
		// (or all the way to the left, in RTL)
		float extraSpace = ltr ? rotatedInSize[0] - cropRect.right : cropRect.left;
		// Cap the amount of extra width
		float maxExtraSpace = defaultWallpaperWidth / cropScale - cropRect.width();
		extraSpace = Math.min(extraSpace, maxExtraSpace);

		if (ltr) {
			cropRect.right += extraSpace;
		} else {
			cropRect.left -= extraSpace;
		}

		// ADJUST CROP HEIGHT
		if (isPortrait) {
			cropRect.bottom = cropRect.top + portraitHeight / cropScale;
		} else { // LANDSCAPE
			float extraPortraitHeight = portraitHeight / cropScale - cropRect.height();
			float expandHeight = Math.min(Math.min(rotatedInSize[1] - cropRect.bottom, cropRect.top), extraPortraitHeight / 2);
			cropRect.top -= expandHeight;
			cropRect.bottom += expandHeight;
		}
		final int outWidth = (int) Math.round(cropRect.width() * cropScale);
		final int outHeight = (int) Math.round(cropRect.height() * cropScale);

		Runnable onEndCrop = new Runnable() {
			public void run() {
				updateWallpaperDimensions(outWidth, outHeight);
				if (finishActivityWhenDone) {
					setResult(Activity.RESULT_OK);
					finish();
				}
			}
		};
		BitmapCropTask cropTask = new BitmapCropTask(this, uri, cropRect, cropRotation, outWidth, outHeight, true, false, onEndCrop);
		if (onBitmapCroppedHandler != null) {
			cropTask.setOnBitmapCropped(onBitmapCroppedHandler);
		}
		cropTask.execute();
	}

	public interface OnBitmapCroppedHandler {
		public void onBitmapCropped(byte[] imageBytes);
	}

	protected void updateWallpaperDimensions(int width, int height) {
		String spKey = WallpaperUtils.getSharedPreferencesKey();
		SharedPreferences sp = getSharedPreferences(spKey, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		if (width != 0 && height != 0) {
			editor.putInt(WallpaperConstant.WALLPAPER_WIDTH_KEY, width);
			editor.putInt(WallpaperConstant.WALLPAPER_HEIGHT_KEY, height);
		} else {
			editor.remove(WallpaperConstant.WALLPAPER_WIDTH_KEY);
			editor.remove(WallpaperConstant.WALLPAPER_HEIGHT_KEY);
		}
		editor.commit();

		WallpaperUtils.suggestWallpaperDimension(getResources(), sp, getWindowManager(), WallpaperManager.getInstance(this));
	}

}
