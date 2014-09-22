package com.aidy.launcher3.bean;

import android.graphics.PointF;

public class DeviceProfileQuery {
	public float widthDps;
	public float heightDps;
	public float value;
	public PointF dimens;

	public DeviceProfileQuery(float w, float h, float v) {
		widthDps = w;
		heightDps = h;
		value = v;
		dimens = new PointF(w, h);
	}
}
