package edu.upenn.cis542;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbTableCreator extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "gpsdb";
	private static final int DATABASE_VERSION = 1;
	private static final String CREATE_TABLE_ONE = "create table " +
			"cserverTable (_id integer primary key autoincrement, dateTime text not null, " +
			"longitude text not null, latitude text not null);";
	private static final String CREATE_TABLE_TWO = "create table " +
			"androidTable (_id integer primary key autoincrement, dateTime text not null, " +
			"longitude text not null, latitude text not null);";	
	private Activity parent;
	
	public DbTableCreator(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		getWritableDatabase();
		parent = (Activity)context;
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_TABLE_ONE);
		db.execSQL(CREATE_TABLE_TWO);
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
	}

	public void clear() {
		if (getWritableDatabase() != null)
			getWritableDatabase().execSQL("DELETE FROM " + DATABASE_NAME);
	}
	
	public void close() {

		if (getWritableDatabase() != null)
			getWritableDatabase().close();

		super.close();
	}
	
	public void insertLocation(String table, String dateTime, String lat, String lng) {
		ContentValues values = new ContentValues();
		values.put("dateTime", dateTime);
		values.put("latitude", lat);
		values.put("longitude", lng);
		getWritableDatabase().insert(table, null, values);
	}
	
	public String getLocations(String table) {
		String columns[] = {"longitude", "latitude"};
		Cursor c = getWritableDatabase().query(table, columns, null, null, null, null, null);
		if(c == null) { 
			return "" ; 
		}
		parent.startManagingCursor(c);
		int columnLong = c.getColumnIndex("longitude");
		int columnLat = c.getColumnIndex("latitude");
		String returnValue = "";
		while (c.moveToNext()) {
			returnValue += c.getString(columnLong) + " " + c.getString(columnLat) + "\n";
		}
		c.close();
		return returnValue;
	}
}