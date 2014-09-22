package com.aidy.launcher3.ui.interfaces;

import android.view.View;

import com.aidy.launcher3.ui.Launcher;

public interface LauncherTransitionable {
	public View getContent();

	public void onLauncherTransitionPrepare(Launcher l, boolean animated, boolean toWorkspace);

	public void onLauncherTransitionStart(Launcher l, boolean animated, boolean toWorkspace);

	public void onLauncherTransitionStep(Launcher l, float t);

	public void onLauncherTransitionEnd(Launcher l, boolean animated, boolean toWorkspace);
}
