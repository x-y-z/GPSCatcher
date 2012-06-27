package edu.upenn.cis542;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.text.format.DateFormat;

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
		if (getWritableDatabase() != null) {
			getWritableDatabase().delete("cserverTable", null, null);
			getWritableDatabase().delete("androidTable", null, null);
		}
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
			returnValue += c.getString(columnLong) + ", " + c.getString(columnLat) + "\n";
		}
		c.close();
		return returnValue;
	}
	
	public double get24hrDist(String table){
		double totalDist = 0;
		String columns[] = {"dateTime", "latitude", "longitude"};
		Cursor c = getWritableDatabase().query(table, columns, null, null, null, null, null);
		if (c == null)
			return 0;
		parent.startManagingCursor(c);
		int colDT = c.getColumnIndex("dateTime");
		int colLng = c.getColumnIndex("longitude");
		int colLat = c.getColumnIndex("latitude");
		
		if (!c.moveToNext())
			return 0;
		
		String dt = c.getString(colDT);
		String lat = c.getString(colLat);
		String lng = c.getString(colLng);
		
		SimpleDateFormat formatter = new SimpleDateFormat("mm/dd/yy hh:mm:ss");
		Date firstDate = null;
		try {
			firstDate = formatter.parse(dt);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return totalDist;
		}
		GeoPoint preGeo = new GeoPoint((int)(Double.parseDouble(lat)*1E6), (int)(Double.parseDouble(lng)*1E6));
		
		Date lastDate = null;
		while (c.moveToNext()){
			dt = c.getString(colDT);
			try {
				lastDate = formatter.parse(dt);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				break;
			}
			if (lastDate.getTime() - firstDate.getTime() > 24*3600*1000){
				break;
			}
			lat = c.getString(colLat);
			lng = c.getString(colLng);
			GeoPoint curGeo = new GeoPoint((int)(Double.parseDouble(lat)*1E6), (int)(Double.parseDouble(lng)*1E6));
			
			totalDist += CalculateDistance(preGeo, curGeo);
			preGeo = curGeo;
		}
		c.close();
		return totalDist;
	}
	
	private static float CalculateDistance(GeoPoint s, GeoPoint e) {
		float[] results = new float[1];
		Location.distanceBetween(s.getLatitudeE6() / 1E6,
				s.getLongitudeE6() / 1E6, e.getLatitudeE6() / 1E6,
				e.getLongitudeE6() / 1E6, results);
		return results[results.length - 1];
	}
}
