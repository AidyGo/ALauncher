package com.aidy.alauncher.support.views.drag;

import com.aidy.alauncher.ui.MainActivity;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class DragLayer extends FrameLayout implements ViewGroup.OnHierarchyChangeListener {

	private MainActivity mMainActivity;
	private DragController mDragController;

	public DragLayer(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public void setup(MainActivity mainActivity, DragController dragController) {
		this.mMainActivity = mainActivity;
		this.mDragController = dragController;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		return mDragController.dispatchKeyEvent(event);
	}

	@Override
	@Deprecated
	protected boolean fitSystemWindows(Rect insets) {
		// TODO Auto-generated method stub
		return super.fitSystemWindows(insets);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onInterceptHoverEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		return super.onInterceptHoverEvent(event);
	}

	@Override
	public void onChildViewAdded(View arg0, View arg1) {
		// TODO Auto-generated method stub
		updateChildIndices();
	}

	@Override
	public void onChildViewRemoved(View arg0, View arg1) {
		// TODO Auto-generated method stub
		updateChildIndices();
	}

	private void updateChildIndices() {

	}

	public interface TouchCompleteListener {
		public void onTouchComplete();
	}

}
