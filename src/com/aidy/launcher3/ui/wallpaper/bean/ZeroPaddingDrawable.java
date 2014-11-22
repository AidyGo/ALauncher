package com.aidy.launcher3.ui.wallpaper.bean;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;

public class ZeroPaddingDrawable extends LevelListDrawable {
	public ZeroPaddingDrawable(Drawable d) {
		super();
		addLevel(0, 0, d);
		setLevel(0);
	}

	@Override
	public boolean getPadding(Rect padding) {
		padding.set(0, 0, 0, 0);
		return true;
	}
}