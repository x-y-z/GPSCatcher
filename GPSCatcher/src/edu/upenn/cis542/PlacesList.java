package edu.upenn.cis542;

import java.util.List;

import com.google.api.client.util.Key;

//Place list structure for JSON parsing
public class PlacesList {

	@Key
	public String status;

	@Key
	public List<Place> results;
}
