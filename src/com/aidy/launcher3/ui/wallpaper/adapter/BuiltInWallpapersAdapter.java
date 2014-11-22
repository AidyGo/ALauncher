package com.aidy.launcher3.ui.wallpaper.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import com.aidy.launcher3.ui.wallpaper.bean.ResourceWallpaperInfo;
import com.aidy.launcher3.ui.wallpaper.support.WallpaperUtils;

public class BuiltInWallpapersAdapter extends BaseAdapter implements ListAdapter {
	private LayoutInflater mLayoutInflater;
	private ArrayList<ResourceWallpaperInfo> mWallpapers;

	public BuiltInWallpapersAdapter(Activity activity, ArrayList<ResourceWallpaperInfo> wallpapers) {
		mLayoutInflater = activity.getLayoutInflater();
		mWallpapers = wallpapers;
	}

	public int getCount() {
		return mWallpapers.size();
	}

	public ResourceWallpaperInfo getItem(int position) {
		return mWallpapers.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Drawable thumb = mWallpapers.get(position).getmThumb();
		return WallpaperUtils.createImageTileView(mLayoutInflater, position, convertView, parent, thumb);
	}
}