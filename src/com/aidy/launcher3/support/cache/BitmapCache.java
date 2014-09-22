package com.aidy.launcher3.support.cache;

import android.graphics.Bitmap;

public class BitmapCache extends SoftReferenceThreadLocal<Bitmap> {
	@Override
	protected Bitmap initialValue() {
		return null;
	}
}
