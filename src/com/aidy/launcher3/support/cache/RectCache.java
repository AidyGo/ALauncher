package com.aidy.launcher3.support.cache;

import android.graphics.Rect;

public class RectCache extends SoftReferenceThreadLocal<Rect> {
	@Override
	protected Rect initialValue() {
		return new Rect();
	}
}
