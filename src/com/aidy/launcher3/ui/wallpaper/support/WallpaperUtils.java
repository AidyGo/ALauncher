package com.aidy.launcher3.ui.wallpaper.support;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;

import android.app.WallpaperManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.aidy.launcher3.R;
import com.aidy.launcher3.ui.wallpaper.WallpaperCropActivity;
import com.aidy.launcher3.ui.wallpaper.bean.ZeroPaddingDrawable;
import com.android.gallery3d.exif.ExifInterface;

public class WallpaperUtils {

	public static Point getDefaultThumbnailSize(Resources res) {
		return new Point(res.getDimensionPixelSize(R.dimen.wallpaperThumbnailWidth),
				res.getDimensionPixelSize(R.dimen.wallpaperThumbnailHeight));

	}

	public static Bitmap createThumbnail(Point size, Context context, Uri uri, byte[] imageBytes, Resources res, int resId, int rotation,
			boolean leftAligned) {
		int width = size.x;
		int height = size.y;

		BitmapCropTask cropTask;
		if (uri != null) {
			cropTask = new BitmapCropTask(context, uri, null, rotation, width, height, false, true, null);
		} else if (imageBytes != null) {
			cropTask = new BitmapCropTask(imageBytes, null, rotation, width, height, false, true, null);
		} else {
			cropTask = new BitmapCropTask(context, res, resId, null, rotation, width, height, false, true, null);
		}
		Point bounds = cropTask.getImageBounds();
		if (bounds == null || bounds.x == 0 || bounds.y == 0) {
			return null;
		}

		Matrix rotateMatrix = new Matrix();
		rotateMatrix.setRotate(rotation);
		float[] rotatedBounds = new float[] { bounds.x, bounds.y };
		rotateMatrix.mapPoints(rotatedBounds);
		rotatedBounds[0] = Math.abs(rotatedBounds[0]);
		rotatedBounds[1] = Math.abs(rotatedBounds[1]);

		RectF cropRect = WallpaperUtils.getMaxCropRect((int) rotatedBounds[0], (int) rotatedBounds[1], width, height, leftAligned);
		cropTask.setCropBounds(cropRect);

		if (cropTask.cropBitmap()) {
			return cropTask.getCroppedBitmap();
		} else {
			return null;
		}
	}

	public static void setWallpaperItemPaddingToZero(FrameLayout frameLayout) {
		frameLayout.setPadding(0, 0, 0, 0);
		frameLayout.setForeground(new ZeroPaddingDrawable(frameLayout.getForeground()));
	}

	public static View createImageTileView(LayoutInflater layoutInflater, int position, View convertView, ViewGroup parent, Drawable thumb) {
		View view;

		if (convertView == null) {
			view = layoutInflater.inflate(R.layout.wallpaper_picker_item, parent, false);
		} else {
			view = convertView;
		}

		WallpaperUtils.setWallpaperItemPaddingToZero((FrameLayout) view);

		ImageView image = (ImageView) view.findViewById(R.id.wallpaper_image);

		if (thumb != null) {
			image.setImageDrawable(thumb);
			thumb.setDither(true);
		}

		return view;
	}

	public static void suggestWallpaperDimension(Resources res, final SharedPreferences sharedPrefs, WindowManager windowManager,
			final WallpaperManager wallpaperManager) {
		final Point defaultWallpaperSize = WallpaperUtils.getDefaultWallpaperSize(res, windowManager);

		new Thread("suggestWallpaperDimension") {
			public void run() {
				// If we have saved a wallpaper width/height, use that instead
				int savedWidth = sharedPrefs.getInt(WallpaperConstant.WALLPAPER_WIDTH_KEY, defaultWallpaperSize.x);
				int savedHeight = sharedPrefs.getInt(WallpaperConstant.WALLPAPER_HEIGHT_KEY, defaultWallpaperSize.y);
				wallpaperManager.suggestDesiredDimensions(savedWidth, savedHeight);
			}
		}.start();
	}

	public static RectF getMaxCropRect(int inWidth, int inHeight, int outWidth, int outHeight, boolean leftAligned) {
		RectF cropRect = new RectF();
		// Get a crop rect that will fit this
		if (inWidth / (float) inHeight > outWidth / (float) outHeight) {
			cropRect.top = 0;
			cropRect.bottom = inHeight;
			cropRect.left = (inWidth - (outWidth / (float) outHeight) * inHeight) / 2;
			cropRect.right = inWidth - cropRect.left;
			if (leftAligned) {
				cropRect.right -= cropRect.left;
				cropRect.left = 0;
			}
		} else {
			cropRect.left = 0;
			cropRect.right = inWidth;
			cropRect.top = (inHeight - (outHeight / (float) outWidth) * inWidth) / 2;
			cropRect.bottom = inHeight - cropRect.top;
		}
		return cropRect;
	}

	public static CompressFormat convertExtensionToCompressFormat(String extension) {
		return extension.equals("png") ? CompressFormat.PNG : CompressFormat.JPEG;
	}

	public static String getFileExtension(String requestFormat) {
		String outputFormat = (requestFormat == null) ? "jpg" : requestFormat;
		outputFormat = outputFormat.toLowerCase();
		return (outputFormat.equals("png") || outputFormat.equals("gif")) ? "png" : "jpg";
	}

	public static String getSharedPreferencesKey() {
		return WallpaperCropActivity.class.getName();
	}

	// As a ratio of screen height, the total distance we want the parallax
	// effect to span
	// horizontally
	public static float wallpaperTravelToScreenWidthRatio(int width, int height) {
		float aspectRatio = width / (float) height;

		// At an aspect ratio of 16/10, the wallpaper parallax effect should
		// span 1.5 * screen width
		// At an aspect ratio of 10/16, the wallpaper parallax effect should
		// span 1.2 * screen width
		// We will use these two data points to extrapolate how much the
		// wallpaper parallax effect
		// to span (ie travel) at any aspect ratio:

		final float ASPECT_RATIO_LANDSCAPE = 16 / 10f;
		final float ASPECT_RATIO_PORTRAIT = 10 / 16f;
		final float WALLPAPER_WIDTH_TO_SCREEN_RATIO_LANDSCAPE = 1.5f;
		final float WALLPAPER_WIDTH_TO_SCREEN_RATIO_PORTRAIT = 1.2f;

		// To find out the desired width at different aspect ratios, we use the
		// following two
		// formulas, where the coefficient on x is the aspect ratio
		// (width/height):
		// (16/10)x + y = 1.5
		// (10/16)x + y = 1.2
		// We solve for x and y and end up with a final formula:
		final float x = (WALLPAPER_WIDTH_TO_SCREEN_RATIO_LANDSCAPE - WALLPAPER_WIDTH_TO_SCREEN_RATIO_PORTRAIT)
				/ (ASPECT_RATIO_LANDSCAPE - ASPECT_RATIO_PORTRAIT);
		final float y = WALLPAPER_WIDTH_TO_SCREEN_RATIO_PORTRAIT - x * ASPECT_RATIO_PORTRAIT;
		return x * aspectRatio + y;
	}

	public static Point getDefaultWallpaperSize(Resources res, WindowManager windowManager) {
		Point minDims = new Point();
		Point maxDims = new Point();
		windowManager.getDefaultDisplay().getCurrentSizeRange(minDims, maxDims);

		int maxDim = Math.max(maxDims.x, maxDims.y);
		int minDim = Math.max(minDims.x, minDims.y);

		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
			Point realSize = new Point();
			windowManager.getDefaultDisplay().getRealSize(realSize);
			maxDim = Math.max(realSize.x, realSize.y);
			minDim = Math.min(realSize.x, realSize.y);
		}

		// We need to ensure that there is enough extra space in the wallpaper
		// for the intended
		// parallax effects
		final int defaultWidth, defaultHeight;
		if (WallpaperUtils.isScreenLarge(res)) {
			defaultWidth = (int) (maxDim * wallpaperTravelToScreenWidthRatio(maxDim, minDim));
			defaultHeight = maxDim;
		} else {
			defaultWidth = Math.max((int) (minDim * WallpaperConstant.WALLPAPER_SCREENS_SPAN), maxDim);
			defaultHeight = maxDim;
		}
		return new Point(defaultWidth, defaultHeight);
	}

	public static int getRotationFromExif(String path) {
		return getRotationFromExifHelper(path, null, 0, null, null);
	}

	public static int getRotationFromExif(Context context, Uri uri) {
		return getRotationFromExifHelper(null, null, 0, context, uri);
	}

	public static int getRotationFromExif(Resources res, int resId) {
		return getRotationFromExifHelper(null, res, resId, null, null);
	}

	private static int getRotationFromExifHelper(String path, Resources res, int resId, Context context, Uri uri) {
		ExifInterface ei = new ExifInterface();
		try {
			if (path != null) {
				ei.readExif(path);
			} else if (uri != null) {
				InputStream is = context.getContentResolver().openInputStream(uri);
				BufferedInputStream bis = new BufferedInputStream(is);
				ei.readExif(bis);
			} else {
				InputStream is = res.openRawResource(resId);
				BufferedInputStream bis = new BufferedInputStream(is);
				ei.readExif(bis);
			}
			Integer ori = ei.getTagIntValue(ExifInterface.TAG_ORIENTATION);
			if (ori != null) {
				return ExifInterface.getRotationForOrientationValue(ori.shortValue());
			}
		} catch (IOException e) {
		}
		return 0;
	}

	public static boolean isScreenLarge(Resources res) {
		Configuration config = res.getConfiguration();
		return config.smallestScreenWidthDp >= 720;
	}

}
