package com.aidy.launcher3.support.cache;

import android.graphics.Canvas;

public class CanvasCache extends SoftReferenceThreadLocal<Canvas> {
	@Override
	protected Canvas initialValue() {
		return new Canvas();
	}
}
