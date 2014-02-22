package com.stano.maph;

import com.stano.maph.utils.ImageResizer;

import android.app.Application;

public class App extends Application {

	private static App instance;
	
	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
	}
	
	public final static String getThumbsDirPath() {
		String path = instance.getExternalFilesDir(ImageResizer.TYPE_THUMBS).getAbsolutePath() + "/";
		return path;
	}

	public final static String getPhotosDirPath() {
		String path = instance.getExternalFilesDir(ImageResizer.TYPE_PHOTOS).getAbsolutePath() + "/";
		return path;
	}
	
}
