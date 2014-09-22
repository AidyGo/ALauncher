package com.aidy.launcher3.ui.listener;

import android.view.KeyEvent;
import android.view.View;

/**
 * A keyboard listener we set on all the workspace icons.
 */

public class FolderKeyEventListener implements View.OnKeyListener {
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		return FocusHelper.handleFolderKeyEvent(v, keyCode, event);
	}
}
