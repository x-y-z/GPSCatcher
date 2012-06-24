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

public class Navigation {
	private static final String PLACES_SEARCH_URL = "http://maps.google.com/maps?f=d&hl=en";

	public void performSearch(double lat, double lng) throws Exception {
		try {
			System.out.println("Perform Search ....");
			System.out.println("-------------------");
			HttpRequestFactory httpRequestFactory = createRequestFactory(transport);
			HttpRequest request = httpRequestFactory
					.buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));

			//http://blog.synyx.de/2010/06/routing-driving-directions-on-android-part-1-get-the-route/
			request.getUrl().put("saddr", lat + "," + lng);
			request.getUrl().put("daddr", (lat + 0.1) + "," + (lng + 0.1));
			request.getUrl().put("ie", "UTF8");
			request.getUrl().put("output", "kml");


			@SuppressWarnings("unused")
			String res = request.getUrl().toString();

			res = request.execute().parseAsString();

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
