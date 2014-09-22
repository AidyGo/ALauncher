package com.aidy.launcher3.support.cache;

import java.lang.ref.SoftReference;

abstract class SoftReferenceThreadLocal<T> {
	private ThreadLocal<SoftReference<T>> mThreadLocal;

	public SoftReferenceThreadLocal() {
		mThreadLocal = new ThreadLocal<SoftReference<T>>();
	}

	abstract T initialValue();

	public void set(T t) {
		mThreadLocal.set(new SoftReference<T>(t));
	}

	public T get() {
		SoftReference<T> reference = mThreadLocal.get();
		T obj;
		if (reference == null) {
			obj = initialValue();
			mThreadLocal.set(new SoftReference<T>(obj));
			return obj;
		} else {
			obj = reference.get();
			if (obj == null) {
				obj = initialValue();
				mThreadLocal.set(new SoftReference<T>(obj));
			}
			return obj;
		}
	}
}
