package com.stano.maph.database;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.stano.maph.database.DatabaseContract.PhotoColumns;
import com.stano.maph.database.DatabaseContract.Tables;
import com.stano.maph.database.cmn.Photo;

public class PhotoDAO {
	private SQLiteDatabase database;
	private DatabaseHelper dbHelper;

	public PhotoDAO(Context context) {
		dbHelper = new DatabaseHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public ArrayList<Photo> getAllPhotos() {
		checkDb();
		ArrayList<Photo> photos = new ArrayList<Photo>();

		String orderBy = PhotoColumns.TIMESTAMP + " DESC";
		Cursor cursor = database.query(Tables.PHOTOS, null, null, null, null, null, orderBy);
		if (cursor.moveToFirst()) {
			while (!cursor.isAfterLast()) {
				Photo photo = cursorToPhoto(cursor);
				photos.add(photo);
				cursor.moveToNext();
			}
		}

		return photos;
	}

	public Photo createPhoto(String filename, String title, String description, long timestamp, String location) {
		checkDb();
		ContentValues values = new ContentValues();
		values.put(PhotoColumns.DESCRIPTION, description);
		values.put(PhotoColumns.TIMESTAMP, timestamp);
		values.put(PhotoColumns.TITLE, title);
		values.put(PhotoColumns.FILENAME, filename);
		values.put(PhotoColumns.LOCATION, location);

		long insertId = database.insert(Tables.PHOTOS, null, values);

		Cursor cursor = database.query(Tables.PHOTOS, null, PhotoColumns.ID + " = " + insertId, null, null, null, null);
		cursor.moveToFirst();
		Photo newPhoto = cursorToPhoto(cursor);
		cursor.close();
		return newPhoto;

	}

	public boolean deletePhoto(Photo photo) {
		checkDb();
		long id = photo.getId();
		int affectedRows = database.delete(Tables.PHOTOS, PhotoColumns.ID + "=" + id, null);

		return affectedRows > 0;
	}

	public boolean updatePhoto(Photo photo) {
		checkDb();
		long id = photo.getId();
		ContentValues values = new ContentValues();
		values.put(PhotoColumns.DESCRIPTION, photo.getDescription());
		values.put(PhotoColumns.TIMESTAMP, photo.getTimestamp());
		values.put(PhotoColumns.TITLE, photo.getTitle());
		values.put(PhotoColumns.FILENAME, photo.getFilename());
		values.put(PhotoColumns.LOCATION, photo.getLocation());

		int affectedRows = database.update(Tables.PHOTOS, values, PhotoColumns.ID + "=" + id, null);

		return affectedRows > 0;
	}

	private void checkDb() {
		if (database == null) {
			open();
		}
	}

	private Photo cursorToPhoto(Cursor cursor) {
		Photo photo = new Photo();
		photo.setId(cursor.getInt(PhotoColumns.INDEX_ID));
		photo.setFilename(cursor.getString(PhotoColumns.INDEX_FILENAME));
		photo.setTimestamp(cursor.getLong(PhotoColumns.INDEX_TIMESTAMP));
		photo.setTitle(cursor.getString(PhotoColumns.INDEX_TITLE));
		photo.setDescription(cursor.getString(PhotoColumns.INDEX_DESCRIPTION));
		photo.setLocation(cursor.getString(PhotoColumns.INDEX_LOCATION));

		return photo;
	}
}
