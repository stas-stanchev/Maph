package com.stano.maph.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.stano.maph.database.DatabaseContract.PhotoColumns;
import com.stano.maph.database.DatabaseContract.Tables;

public class DatabaseHelper extends SQLiteOpenHelper {

	private static String CREATE_QUERY = "CREATE TABLE " + Tables.PHOTOS + "("
			+ PhotoColumns.ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
			+ PhotoColumns.FILENAME + " TEXT NOT NULL, "
			+ PhotoColumns.TIMESTAMP + " INTEGER NOT NULL, "
			+ PhotoColumns.TITLE + " TEXT, "
			+ PhotoColumns.DESCRIPTION + " TEXT, "
			+ PhotoColumns.LOCATION + " TEXT);";
	
	public DatabaseHelper(Context context) {
		super(context, DatabaseContract.DB_NAME, null, DatabaseContract.VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_QUERY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
