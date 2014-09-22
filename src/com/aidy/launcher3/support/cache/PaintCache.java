package com.aidy.launcher3.support.cache;

import android.graphics.Paint;

public class PaintCache extends SoftReferenceThreadLocal<Paint> {
	@Override
	protected Paint initialValue() {
		return null;
	}
}
