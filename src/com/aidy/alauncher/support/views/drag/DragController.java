package com.aidy.alauncher.support.views.drag;

import android.view.KeyEvent;

public class DragController {

	private boolean mDragging;

	public boolean dispatchKeyEvent(KeyEvent event) {
		return mDragging;
	}

	public boolean ismDragging() {
		return mDragging;
	}

}
