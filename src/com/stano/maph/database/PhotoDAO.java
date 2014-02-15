package com.stano.maph.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

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
}
