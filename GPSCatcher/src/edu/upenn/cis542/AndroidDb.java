package edu.upenn.cis542;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AndroidDb extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "androiddb";
	private static final int DATABASE_VERSION = 1;
	private static final String CREATE_TABLE = "create table " +
			"androiddb (_id integer primary key autoincrement, dateTime text not null, " +
			"longitude text not null, latitude text not null);";
	
	public AndroidDb(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		getWritableDatabase();
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	public void insertLocation(String dateTime, String lat, String lng) {
		ContentValues values = new ContentValues();
		values.put("dateTime", dateTime);
		values.put("latitude", lat);
		values.put("longitude", lng);
		getWritableDatabase().insert(DATABASE_NAME, null, values);
	}
}
