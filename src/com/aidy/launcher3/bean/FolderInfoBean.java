/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.aidy.launcher3.bean;

import java.util.ArrayList;

import com.aidy.launcher3.db.LauncherSettings;
import com.aidy.launcher3.db.LauncherSettings.Favorites;

import android.content.ContentValues;

/**
 * Represents a folder containing shortcuts or apps.
 */
public class FolderInfoBean extends ItemInfoBean {

	/**
	 * Whether this folder has been opened
	 */
	public boolean opened;

	/**
	 * The apps and shortcuts
	 */
	public ArrayList<ShortcutInfo> contents = new ArrayList<ShortcutInfo>();

	public ArrayList<FolderListener> listeners = new ArrayList<FolderListener>();

	public FolderInfoBean() {
		itemType = LauncherSettings.Favorites.ITEM_TYPE_FOLDER;
	}

	/**
	 * Add an app or shortcut
	 * 
	 * @param item
	 */
	public void add(ShortcutInfo item) {
		contents.add(item);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).onAdd(item);
		}
		itemsChanged();
	}

	/**
	 * Remove an app or shortcut. Does not change the DB.
	 * 
	 * @param item
	 */
	public void remove(ShortcutInfo item) {
		contents.remove(item);
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).onRemove(item);
		}
		itemsChanged();
	}

	public void setTitle(CharSequence title) {
		this.title = title;
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).onTitleChanged(title);
		}
	}

	@Override
	public void onAddToDatabase(ContentValues values) {
		super.onAddToDatabase(values);
		values.put(LauncherSettings.Favorites.TITLE, title.toString());
	}

	public void addListener(FolderListener listener) {
		listeners.add(listener);
	}

	public void removeListener(FolderListener listener) {
		if (listeners.contains(listener)) {
			listeners.remove(listener);
		}
	}

	public void itemsChanged() {
		for (int i = 0; i < listeners.size(); i++) {
			listeners.get(i).onItemsChanged();
		}
	}

	@Override
	public void unbind() {
		super.unbind();
		listeners.clear();
	}

	public interface FolderListener {
		public void onAdd(ShortcutInfo item);

		public void onRemove(ShortcutInfo item);

		public void onTitleChanged(CharSequence title);

		public void onItemsChanged();
	}

	@Override
	public String toString() {
		return "FolderInfo(id=" + this.id + " type=" + this.itemType + " container=" + this.container + " screen="
				+ screenId + " cellX=" + cellX + " cellY=" + cellY + " spanX=" + spanX + " spanY=" + spanY
				+ " dropPos=" + dropPos + ")";
	}
}
