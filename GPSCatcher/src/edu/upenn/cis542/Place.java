package edu.upenn.cis542;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.api.client.util.Key;

public class Place {
	@Key("geometry")
	public Geometry geometry;

	@Key("id")
	public String id;

	@Key("name")
	public String name;

	@Key("reference")
	public String reference;

	@Key("rating")
	public float rating;
	
	@Key("types")
	public List<String> types;
	
	
	
	public static class Geometry{
		@Key("location")
		public Location location;
		
		public String toString(){
			return "At:" + location;
		}
		
		public GeoPoint getGeo(){
			return new GeoPoint((int)(location.lat*1E6), (int)(location.lng*1E6));
		}
		
	}
	public static class Location{
		@Key("lat")
		public float lat;
		@Key("lng")
		public float lng;
		
		public String toString(){
			return "("+lat+","+lng+")";
		}
	}
	
	@Override
	public String toString() {
		return "Name:" + name + "\nRating:" + rating + "\nTypes:" + types +"\nLocation:"+geometry.getGeo();
	}

	public GeoPoint getGeo(){
		return geometry.getGeo();
	}

}
