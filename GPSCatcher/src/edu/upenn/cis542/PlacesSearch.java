package edu.upenn.cis542;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;

/*
 * The code is modified from: http://ddewaele.blogspot.com/2011/05/introducing-google-places-api.html
 * 
 * 
 */
public class PlacesSearch {
	private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";

	// private static final boolean PRINT_AS_STRING = false;
	private static final String API_KEY = "AIzaSyC5LOYZg67bGQTuutTYGnrCLEGkYE5MoM0";
	
	private PlacesList pl = null;
	
	private int radius = 10;

	public PlacesList getPlaces(){
		return pl;
	}
	
	public void performSearch(double lat, double lng) throws Exception {
		try {
			System.out.println("Perform Search ....");
			System.out.println("-------------------");
			HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
			HttpRequest request = httpRequestFactory
					.buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
			request.getUrl().put("key", API_KEY);
			request.getUrl().put("location", lat + "," + lng);
			request.getUrl().put("radius", radius);
			request.getUrl().put("sensor", "true");
			request.getUrl().put("types", "food");

			
			pl = request.execute().parseAs(PlacesList.class);
			
			while(pl.results.size() < 3){
				radius *= 2;
				request.getUrl().put("radius", radius);
				
				pl = request.execute().parseAs(PlacesList.class);
			}
				
			
//			System.out.println("STATUS = " + places.status);
//			for (Place place : places.results) {
//				System.out.println(place);
//			}

		} catch (HttpResponseException e) {
			System.err.println(e);
			throw e;
		}
	}

	private static final HttpTransport transport = new ApacheHttpTransport();

	public static HttpRequestFactory createRequestFactory(
			final HttpTransport transport) {

		return transport.createRequestFactory(new HttpRequestInitializer() {
			public void initialize(HttpRequest request) {
				GoogleHeaders headers = new GoogleHeaders();
				headers.setApplicationName("GPSCatcher");
				request.setHeaders(headers);
				request.setParser(new JsonObjectParser(new JacksonFactory()));
			}
		});
	}

}