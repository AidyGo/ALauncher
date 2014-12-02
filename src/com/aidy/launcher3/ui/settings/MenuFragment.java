package com.aidy.launcher3.ui.settings;

import com.aidy.launcher3.R;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class MenuFragment extends DialogFragment {

	private static MenuFragment mInstance;

	private MenuFragment() {
	}

	public static MenuFragment getInstance() {
		if (null == mInstance) {
			mInstance = new MenuFragment();
		}
		return mInstance;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return inflater.inflate(R.layout.fragment_bottom_menu, null);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		Window window = getDialog().getWindow();
		window.setGravity(Gravity.BOTTOM);
//		WindowManager.LayoutParams layoutParams = window.getAttributes();
		return super.onCreateDialog(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

}
