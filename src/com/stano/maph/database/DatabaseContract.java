package com.stano.maph.database;

import android.provider.BaseColumns;

public class DatabaseContract {
	
	
	public interface Tables {
		public static final String PHOTOS = "photos";
	}
	
	public class PhotoColumns {
		public static final String ID = BaseColumns._ID;
		public static final String FILENAME = "filename";
		public static final String TIMESTAMP = "timestamp";
		public static final String TITLE = "title";
		public static final String DESCRIPTION = "description";
		public static final String LOCATION = "location";
		
		public static final int INDEX_ID = 0;
		public static final int INDEX_FILENAME = 1;
		public static final int INDEX_TIMESTAMP = 2;
		public static final int INDEX_TITLE = 3;
		public static final int INDEX_DESCRIPTION = 4;
		public static final int INDEX_LOCATION = 5;
	}
}
