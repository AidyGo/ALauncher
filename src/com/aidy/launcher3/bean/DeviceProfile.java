package com.aidy.launcher3.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import android.appwidget.AppWidgetHostView;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.Paint.FontMetrics;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;

import com.aidy.launcher3.CellLayout;
import com.aidy.launcher3.R;
import com.aidy.launcher3.ui.Launcher;
import com.aidy.launcher3.ui.allapp.AppsCustomizePagedView;

public class DeviceProfile {

	public String name;
	public float minWidthDps;
	public float minHeightDps;
	public float numRows;
	public float numColumns;
	public float iconSize;
	public float iconTextSize;
	public float numHotseatIcons;
	public float hotseatIconSize;

	public boolean isLandscape;
	public boolean isTablet;
	public boolean isLargeTablet;
	public boolean transposeLayoutWithOrientation;

	public int desiredWorkspaceLeftRightMarginPx;
	public int edgeMarginPx;
	public Rect defaultWidgetPadding;

	public int widthPx;
	public int heightPx;
	public int availableWidthPx;
	public int availableHeightPx;
	public int iconSizePx;
	public int iconTextSizePx;
	public int cellWidthPx;
	public int cellHeightPx;
	public int folderBackgroundOffset;
	public int folderIconSizePx;
	public int folderCellWidthPx;
	public int folderCellHeightPx;
	public int hotseatCellWidthPx;
	public int hotseatCellHeightPx;
	public int hotseatIconSizePx;
	public int hotseatBarHeightPx;
	public int hotseatAllAppsRank;
	public int allAppsNumRows;
	public int allAppsNumCols;
	public int searchBarSpaceWidthPx;
	public int searchBarSpaceMaxWidthPx;
	public int searchBarSpaceHeightPx;
	public int searchBarHeightPx;
	public int pageIndicatorHeightPx;

	public DeviceProfile(String n, float w, float h, float r, float c, float is, float its, float hs, float his) {
		// Ensure that we have an odd number of hotseat items (since we need to
		// place all apps)
		if (!AppsCustomizePagedView.DISABLE_ALL_APPS && hs % 2 == 0) {
			throw new RuntimeException("All Device Profiles must have an odd number of hotseat spaces");
		}

		name = n;
		minWidthDps = w;
		minHeightDps = h;
		numRows = r;
		numColumns = c;
		iconSize = is;
		iconTextSize = its;
		numHotseatIcons = hs;
		hotseatIconSize = his;
	}

	public DeviceProfile(Context context, ArrayList<DeviceProfile> profiles, float minWidth, float minHeight, int wPx,
			int hPx, int awPx, int ahPx, Resources resources) {
		DisplayMetrics dm = resources.getDisplayMetrics();
		ArrayList<DeviceProfileQuery> points = new ArrayList<DeviceProfileQuery>();
		transposeLayoutWithOrientation = resources.getBoolean(R.bool.hotseat_transpose_layout_with_orientation);
		minWidthDps = minWidth;
		minHeightDps = minHeight;

		ComponentName cn = new ComponentName(context.getPackageName(), this.getClass().getName());
		defaultWidgetPadding = AppWidgetHostView.getDefaultPaddingForWidget(context, cn, null);
		edgeMarginPx = resources.getDimensionPixelSize(R.dimen.dynamic_grid_edge_margin);
		desiredWorkspaceLeftRightMarginPx = 2 * edgeMarginPx;
		pageIndicatorHeightPx = resources.getDimensionPixelSize(R.dimen.dynamic_grid_page_indicator_height);

		// Interpolate the rows
		for (DeviceProfile p : profiles) {
			points.add(new DeviceProfileQuery(p.minWidthDps, p.minHeightDps, p.numRows));
		}
		numRows = Math.round(invDistWeightedInterpolate(minWidth, minHeight, points));
		// Interpolate the columns
		points.clear();
		for (DeviceProfile p : profiles) {
			points.add(new DeviceProfileQuery(p.minWidthDps, p.minHeightDps, p.numColumns));
		}
		numColumns = Math.round(invDistWeightedInterpolate(minWidth, minHeight, points));
		// Interpolate the icon size
		points.clear();
		for (DeviceProfile p : profiles) {
			points.add(new DeviceProfileQuery(p.minWidthDps, p.minHeightDps, p.iconSize));
		}
		iconSize = invDistWeightedInterpolate(minWidth, minHeight, points);
		iconSizePx = DynamicGrid.pxFromDp(iconSize, dm);

		// Interpolate the icon text size
		points.clear();
		for (DeviceProfile p : profiles) {
			points.add(new DeviceProfileQuery(p.minWidthDps, p.minHeightDps, p.iconTextSize));
		}
		iconTextSize = invDistWeightedInterpolate(minWidth, minHeight, points);
		iconTextSizePx = DynamicGrid.pxFromSp(iconTextSize, dm);

		// Interpolate the hotseat size
		points.clear();
		for (DeviceProfile p : profiles) {
			points.add(new DeviceProfileQuery(p.minWidthDps, p.minHeightDps, p.numHotseatIcons));
		}
		numHotseatIcons = Math.round(invDistWeightedInterpolate(minWidth, minHeight, points));
		// Interpolate the hotseat icon size
		points.clear();
		for (DeviceProfile p : profiles) {
			points.add(new DeviceProfileQuery(p.minWidthDps, p.minHeightDps, p.hotseatIconSize));
		}
		// Hotseat
		hotseatIconSize = invDistWeightedInterpolate(minWidth, minHeight, points);
		hotseatIconSizePx = DynamicGrid.pxFromDp(hotseatIconSize, dm);
		hotseatAllAppsRank = (int) (numColumns / 2);

		// Calculate other vars based on Configuration
		updateFromConfiguration(resources, wPx, hPx, awPx, ahPx);

		// Search Bar
		searchBarSpaceMaxWidthPx = resources.getDimensionPixelSize(R.dimen.dynamic_grid_search_bar_max_width);
		searchBarHeightPx = resources.getDimensionPixelSize(R.dimen.dynamic_grid_search_bar_height);
		searchBarSpaceWidthPx = Math.min(searchBarSpaceMaxWidthPx, widthPx);
		searchBarSpaceHeightPx = searchBarHeightPx + 2 * edgeMarginPx;

		// Calculate the actual text height
		Paint textPaint = new Paint();
		textPaint.setTextSize(iconTextSizePx);
		FontMetrics fm = textPaint.getFontMetrics();
		cellWidthPx = iconSizePx;
		cellHeightPx = iconSizePx + (int) Math.ceil(fm.bottom - fm.top);

		// At this point, if the cells do not fit into the available height,
		// then we need
		// to shrink the icon size
		/*
		 * Rect padding = getWorkspacePadding(isLandscape ? CellLayout.LANDSCAPE
		 * : CellLayout.PORTRAIT); int h = (int) (numRows * cellHeightPx) +
		 * padding.top + padding.bottom; if (h > availableHeightPx) { float
		 * delta = h - availableHeightPx; int deltaPx = (int) Math.ceil(delta /
		 * numRows); iconSizePx -= deltaPx; iconSize =
		 * DynamicGrid.dpiFromPx(iconSizePx, dm); cellWidthPx = iconSizePx;
		 * cellHeightPx = iconSizePx + (int) Math.ceil(fm.bottom - fm.top); }
		 */

		// Hotseat
		hotseatBarHeightPx = iconSizePx + 4 * edgeMarginPx;
		hotseatCellWidthPx = iconSizePx;
		hotseatCellHeightPx = iconSizePx;

		// Folder
		folderCellWidthPx = cellWidthPx + 3 * edgeMarginPx;
		folderCellHeightPx = cellHeightPx + (int) ((3f / 2f) * edgeMarginPx);
		folderBackgroundOffset = -edgeMarginPx;
		folderIconSizePx = iconSizePx + 2 * -folderBackgroundOffset;
	}

	public void updateFromConfiguration(Resources resources, int wPx, int hPx, int awPx, int ahPx) {
		isLandscape = (resources.getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE);
		isTablet = resources.getBoolean(R.bool.is_tablet);
		isLargeTablet = resources.getBoolean(R.bool.is_large_tablet);
		widthPx = wPx;
		heightPx = hPx;
		availableWidthPx = awPx;
		availableHeightPx = ahPx;

		Rect padding = getWorkspacePadding(isLandscape ? CellLayout.LANDSCAPE : CellLayout.PORTRAIT);
		int pageIndicatorOffset = resources.getDimensionPixelSize(R.dimen.apps_customize_page_indicator_offset);
		if (isLandscape) {
			allAppsNumRows = (availableHeightPx - pageIndicatorOffset - 4 * edgeMarginPx)
					/ (iconSizePx + iconTextSizePx + 2 * edgeMarginPx);
		} else {
			allAppsNumRows = (int) numRows + 1;
		}
		allAppsNumCols = (availableWidthPx - padding.left - padding.right - 2 * edgeMarginPx)
				/ (iconSizePx + 2 * edgeMarginPx);
	}

	private float dist(PointF p0, PointF p1) {
		return (float) Math.sqrt((p1.x - p0.x) * (p1.x - p0.x) + (p1.y - p0.y) * (p1.y - p0.y));
	}

	private float weight(PointF a, PointF b, float pow) {
		float d = dist(a, b);
		if (d == 0f) {
			return Float.POSITIVE_INFINITY;
		}
		return (float) (1f / Math.pow(d, pow));
	}

	private float invDistWeightedInterpolate(float width, float height, ArrayList<DeviceProfileQuery> points) {
		float sum = 0;
		float weights = 0;
		float pow = 5;
		float kNearestNeighbors = 3;
		final PointF xy = new PointF(width, height);

		ArrayList<DeviceProfileQuery> pointsByNearness = points;
		Collections.sort(pointsByNearness, new Comparator<DeviceProfileQuery>() {
			public int compare(DeviceProfileQuery a, DeviceProfileQuery b) {
				return (int) (dist(xy, a.dimens) - dist(xy, b.dimens));
			}
		});

		for (int i = 0; i < pointsByNearness.size(); ++i) {
			DeviceProfileQuery p = pointsByNearness.get(i);
			if (i < kNearestNeighbors) {
				float w = weight(xy, p.dimens, pow);
				if (w == Float.POSITIVE_INFINITY) {
					return p.value;
				}
				weights += w;
			}
		}

		for (int i = 0; i < pointsByNearness.size(); ++i) {
			DeviceProfileQuery p = pointsByNearness.get(i);
			if (i < kNearestNeighbors) {
				float w = weight(xy, p.dimens, pow);
				sum += w * p.value / weights;
			}
		}

		return sum;
	}

	public Rect getWorkspacePadding(int orientation) {
		Rect padding = new Rect();
		if (orientation == CellLayout.LANDSCAPE && transposeLayoutWithOrientation) {
			// Pad the left and right of the workspace with search/hotseat bar
			// sizes
			padding.set(searchBarSpaceHeightPx, edgeMarginPx, hotseatBarHeightPx, edgeMarginPx);
		} else {
			if (isTablet()) {
				// Pad the left and right of the workspace to ensure consistent
				// spacing
				// between all icons
				int width = (orientation == CellLayout.LANDSCAPE) ? Math.max(widthPx, heightPx) : Math.min(widthPx,
						heightPx);
				// XXX: If the icon size changes across orientations, we will
				// have to take
				// that into account here too.
				int gap = (int) ((width - 2 * edgeMarginPx - (numColumns * cellWidthPx)) / (2 * (numColumns + 1)));
				padding.set(edgeMarginPx + gap, searchBarSpaceHeightPx, edgeMarginPx + gap, hotseatBarHeightPx
						+ pageIndicatorHeightPx);
			} else {
				// Pad the top and bottom of the workspace with search/hotseat
				// bar sizes
				padding.set(desiredWorkspaceLeftRightMarginPx - defaultWidgetPadding.left, searchBarSpaceHeightPx,
						desiredWorkspaceLeftRightMarginPx - defaultWidgetPadding.right, hotseatBarHeightPx
								+ pageIndicatorHeightPx);
			}
		}
		return padding;
	}

	// The rect returned will be extended to below the system ui that covers the
	// workspace
	public Rect getHotseatRect() {
		if (isVerticalBarLayout()) {
			return new Rect(availableWidthPx - hotseatBarHeightPx, 0, Integer.MAX_VALUE, availableHeightPx);
		} else {
			return new Rect(0, availableHeightPx - hotseatBarHeightPx, availableWidthPx, Integer.MAX_VALUE);
		}
	}

	public int calculateCellWidth(int width, int countX) {
		return width / countX;
	}

	public int calculateCellHeight(int height, int countY) {
		return height / countY;
	}

	public boolean isPhone() {
		return !isTablet && !isLargeTablet;
	}

	public boolean isTablet() {
		return isTablet;
	}

	public boolean isLargeTablet() {
		return isLargeTablet;
	}

	public boolean isVerticalBarLayout() {
		return isLandscape && transposeLayoutWithOrientation;
	}

	public void layout(Launcher launcher) {
		FrameLayout.LayoutParams lp;
		Resources res = launcher.getResources();
		boolean hasVerticalBarLayout = isVerticalBarLayout();

		// Layout the search bar space
		View searchBar = launcher.getSearchBar();
		lp = (FrameLayout.LayoutParams) searchBar.getLayoutParams();
		if (hasVerticalBarLayout) {
			// Vertical search bar
			lp.gravity = Gravity.TOP | Gravity.LEFT;
			lp.width = searchBarSpaceHeightPx;
			lp.height = LayoutParams.MATCH_PARENT;
			searchBar.setPadding(0, 2 * edgeMarginPx, 0, 2 * edgeMarginPx);
		} else {
			// Horizontal search bar
			lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
			lp.width = searchBarSpaceWidthPx;
			lp.height = searchBarSpaceHeightPx;
			searchBar.setPadding(2 * edgeMarginPx, 2 * edgeMarginPx, 2 * edgeMarginPx, 0);
		}
		searchBar.setLayoutParams(lp);

		// Layout the search bar
		View qsbBar = launcher.getQsbBar();
		LayoutParams vglp = qsbBar.getLayoutParams();
		vglp.width = LayoutParams.MATCH_PARENT;
		vglp.height = LayoutParams.MATCH_PARENT;
		qsbBar.setLayoutParams(vglp);

		// Layout the voice proxy
		View voiceButtonProxy = launcher.findViewById(R.id.voice_button_proxy);
		if (voiceButtonProxy != null) {
			if (hasVerticalBarLayout) {
				// TODO: MOVE THIS INTO SEARCH BAR MEASURE
			} else {
				lp = (FrameLayout.LayoutParams) voiceButtonProxy.getLayoutParams();
				lp.gravity = Gravity.TOP | Gravity.END;
				lp.width = (widthPx - searchBarSpaceWidthPx) / 2 + 2 * iconSizePx;
				lp.height = searchBarSpaceHeightPx;
			}
		}

		// Layout the workspace
		View workspace = launcher.findViewById(R.id.workspace);
		lp = (FrameLayout.LayoutParams) workspace.getLayoutParams();
		lp.gravity = Gravity.CENTER;
		Rect padding = getWorkspacePadding(isLandscape ? CellLayout.LANDSCAPE : CellLayout.PORTRAIT);
		workspace.setPadding(padding.left, padding.top, padding.right, padding.bottom);
		workspace.setLayoutParams(lp);

		// Layout the hotseat
		View hotseat = launcher.findViewById(R.id.hotseat);
		lp = (FrameLayout.LayoutParams) hotseat.getLayoutParams();
		if (hasVerticalBarLayout) {
			// Vertical hotseat
			lp.gravity = Gravity.RIGHT;
			lp.width = hotseatBarHeightPx;
			lp.height = LayoutParams.MATCH_PARENT;
			hotseat.setPadding(0, 2 * edgeMarginPx, 2 * edgeMarginPx, 2 * edgeMarginPx);
		} else if (isTablet()) {
			// Pad the hotseat with the grid gap calculated above
			int gridGap = (int) ((widthPx - 2 * edgeMarginPx - (numColumns * cellWidthPx)) / (2 * (numColumns + 1)));
			int gridWidth = (int) ((numColumns * cellWidthPx) + ((numColumns - 1) * gridGap));
			int hotseatGap = (int) Math.max(0, (gridWidth - (numHotseatIcons * hotseatCellWidthPx))
					/ (numHotseatIcons - 1));
			lp.gravity = Gravity.BOTTOM;
			lp.width = LayoutParams.MATCH_PARENT;
			lp.height = hotseatBarHeightPx;
			hotseat.setPadding(2 * edgeMarginPx + gridGap + hotseatGap, 0, 2 * edgeMarginPx + gridGap + hotseatGap,
					2 * edgeMarginPx);
		} else {
			// For phones, layout the hotseat without any bottom margin
			// to ensure that we have space for the folders
			lp.gravity = Gravity.BOTTOM;
			lp.width = LayoutParams.MATCH_PARENT;
			lp.height = hotseatBarHeightPx;
			hotseat.findViewById(R.id.layout).setPadding(2 * edgeMarginPx, 0, 2 * edgeMarginPx, 0);
		}
		hotseat.setLayoutParams(lp);

		// Layout the page indicators
		View pageIndicator = launcher.findViewById(R.id.page_indicator);
		if (pageIndicator != null) {
			if (hasVerticalBarLayout) {
				// Hide the page indicators when we have vertical search/hotseat
				pageIndicator.setVisibility(View.GONE);
			} else {
				// Put the page indicators above the hotseat
				lp = (FrameLayout.LayoutParams) pageIndicator.getLayoutParams();
				lp.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
				lp.width = LayoutParams.WRAP_CONTENT;
				lp.height = LayoutParams.WRAP_CONTENT;
				lp.bottomMargin = hotseatBarHeightPx;
				pageIndicator.setLayoutParams(lp);
			}
		}
	}

}
