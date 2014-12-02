/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aidy.launcher3.ui;

import android.app.Application;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

public class LauncherApplication extends Application {

	private static int screenWidth;
	private static int screenHeight;

	/**
	 * 单例模式
	 */
	private static LauncherApplication mInstance = null;

	// private LauncherApplication() {
	//
	// }

	public static LauncherApplication getInstance() {
		if (null == mInstance) {
			mInstance = new LauncherApplication();
		}
		return mInstance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		LauncherAppState.setApplicationContext(this);
		LauncherAppState.getInstance();
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		LauncherAppState.getInstance().onTerminate();
	}

	public static int getScreenWidth() {
		return screenWidth;
	}

	public static int getScreenHeight() {
		return screenHeight;
	}
}