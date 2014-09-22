package com.aidy.launcher3.ui.listener;

import android.view.KeyEvent;
import android.view.View;

/**
 * A keyboard listener we set on the last tab button in AppsCustomize to jump to
 * then market icon and vice versa.
 */

public class AppsCustomizeTabKeyEventListener implements View.OnKeyListener {
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		return FocusHelper.handleAppsCustomizeTabKeyEvent(v, keyCode, event);
	}
}