package com.stano.maph.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import com.stano.maph.App;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;

public class ImageResizer {

	private final static String IMAGE_EXT = ".png";
	private final static long MAX_SIZE_MEGAPIXELS = 1024 * 1024;
	private final static int THUMB_SIZE_PX = 360;
	public static final String TYPE_PHOTOS = "photos";
	public static final String TYPE_THUMBS = "thumbs";

	public static void resizePhoto(Context context, String srcFilePath, String fileName) {
		if (fileName == null) {
			fileName = new File(srcFilePath).getName();
		}
		String pathResizedPhoto = App.getPhotosDirPath() + fileName;
		Bitmap sourceBitmap = BitmapFactory.decodeFile(srcFilePath);
		long imageArea = sourceBitmap.getWidth() * sourceBitmap.getHeight();
		double scale = imageArea > MAX_SIZE_MEGAPIXELS ? Math.sqrt((double) MAX_SIZE_MEGAPIXELS / imageArea) : 1f;
		int scaledWidth = (int) (sourceBitmap.getWidth() * scale);
		int scaledHeight = (int) (sourceBitmap.getHeight() * scale);
		Bitmap resizedBitmap = Bitmap.createScaledBitmap(sourceBitmap, scaledWidth, scaledHeight, false);

		saveBitmapToFile(pathResizedPhoto, resizedBitmap);
	}

	public static void createThumbnail(Context context, String srcFilePath, String fileName) {
		if (fileName == null) {
			fileName = new File(srcFilePath).getName();
		}
		String pathThumbnail = App.getThumbsDirPath() + fileName;
		Bitmap sourceBitmap = BitmapFactory.decodeFile(srcFilePath);
		Bitmap thumbBitmap = ThumbnailUtils.extractThumbnail(sourceBitmap, THUMB_SIZE_PX, THUMB_SIZE_PX);
		saveBitmapToFile(pathThumbnail, thumbBitmap);
	}

	private static void saveBitmapToFile(String path, Bitmap bitmap) {
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(path);
			bitmap.compress(CompressFormat.PNG, 100, fos);
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getRandomFilename() {
		UUID uuid = UUID.randomUUID();

		return uuid.toString() + IMAGE_EXT;
	}

}
