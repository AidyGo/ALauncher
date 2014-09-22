package com.aidy.launcher3.support.cache;

import android.graphics.BitmapFactory;

public class BitmapFactoryOptionsCache extends SoftReferenceThreadLocal<BitmapFactory.Options> {
	@Override
	protected BitmapFactory.Options initialValue() {
		return new BitmapFactory.Options();
	}
}
