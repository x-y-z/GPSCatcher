package edu.upenn.cis542;

import java.util.List;

import com.google.api.client.util.Key;


public class PlacesList {
	public PlacesList(){		
	}

	@Key
	public String status;

	@Key
	public List<Place> results;
}
