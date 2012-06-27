/*
 * Find a route between two GPS points
 * 
 * Code is modified from:
 * http://stackoverflow.com/questions/3109158/how-to-draw-a-path-on-a-map-using-kml-file/3109723#3109723
 * 
 */

package edu.upenn.cis542;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.graphics.Color;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.api.client.http.HttpResponseException;

public class Navigation {
	private Routing navSet;
	private int color = Color.parseColor("#add331");

	public void performSearch(GeoPoint saddr, GeoPoint daddr)
			throws Exception {
		
		if (saddr == null || daddr == null)
			throw new Exception("One of address is empty.");
			
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.google.com/maps?f=d&hl=en");
		urlString.append("&saddr=");// from
		urlString.append((double)saddr.getLatitudeE6()/1E6);
		urlString.append(",");
		urlString.append((double)saddr.getLongitudeE6()/1E6);
		urlString.append("&daddr=");// to
		urlString.append((double)daddr.getLatitudeE6()/1E6);
		urlString.append(",");
		urlString.append((double)daddr.getLongitudeE6()/1E6);
		urlString.append("&ie=UTF8&0&om=0&output=kml");

		try {
			System.out.println("Navigation Perform Search ....");
			System.out.println("-------------------");
			// HttpRequestFactory httpRequestFactory =
			// createRequestFactory(transport);
			// HttpRequest request = httpRequestFactory
			// .buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
			//
			// //
			// http://blog.synyx.de/2010/06/routing-driving-directions-on-android-part-1-get-the-route/
			// request.getUrl().put("saddr", lat + "," + lng);
			// request.getUrl().put("daddr", (lat + 0.1) + "," + (lng + 0.1));
			// request.getUrl().put("ie", "UTF8");
			// request.getUrl().put("output", "kml");
			//
			// String res = request.execute().parseAsString();

			// setup the url
			URL url = new URL(urlString.toString());

			/* Get a SAXParser from the SAXPArserFactory. */
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();

			/* Get the XMLReader of the SAXParser we created. */
			XMLReader xr = sp.getXMLReader();

			/* Create a new ContentHandler and apply it to the XML-Reader */
			KMLParser navSax2Handler = new KMLParser();
			xr.setContentHandler(navSax2Handler);

			InputSource is = new InputSource(url.openStream());
			// perform the synchronous parse
			xr.parse(is);

			/* Our NavigationSaxHandler now provides the parsed data to us. */
			navSet = navSax2Handler.getParsedData();

		} catch (HttpResponseException e) {
			System.err.println(e);
			throw e;
		}
	}

	/**
	 * Does the actual drawing of the route, based on the geo points provided in
	 * the nav set
	 * 
	 * @param navSet
	 *            Navigation set bean that holds the route information, incl.
	 *            geo pos
	 * @param color
	 *            Color in which to draw the lines
	 * @param mMapView01
	 *            Map view to draw onto
	 */
	public void drawPath(MapView mMapView) {

		// color correction for dining, make it darker
		if (color == Color.parseColor("#add331"))
			color = Color.parseColor("#6C8715");

		Collection<Overlay> overlaysToAddAgain = new ArrayList<Overlay>();
		for (Iterator<Overlay> iter = mMapView.getOverlays().iterator(); iter
				.hasNext();) {
			Overlay o = iter.next();

			if (!RouteOverlay.class.getName().equals(o.getClass().getName())) {
				// mMapView01.getOverlays().remove(o);
				overlaysToAddAgain.add(o);
			}
		}
		mMapView.getOverlays().clear();
		mMapView.getOverlays().addAll(overlaysToAddAgain);

		String path = navSet.getRoutePlacemark().getCoordinates();

		if (path != null && path.trim().length() > 0) {
			String[] pairs = path.trim().split(" ");

			String[] lngLat = pairs[0].split(","); // lngLat[0]=longitude
													// lngLat[1]=latitude
													// lngLat[2]=height

			if (lngLat.length < 3)
				lngLat = pairs[1].split(","); // if first pair is not
												// transferred completely, take
												// seconds pair //TODO

			try {
				GeoPoint startGP = new GeoPoint(
						(int) (Double.parseDouble(lngLat[1]) * 1E6),
						(int) (Double.parseDouble(lngLat[0]) * 1E6));

				mMapView.getOverlays().add(
						new RouteOverlay(startGP, startGP, 1));

				GeoPoint gp1;
				GeoPoint gp2 = startGP;

				for (int i = 1; i < pairs.length; i++) // the last one would be crash
				{
					lngLat = pairs[i].split(",");

					gp1 = gp2;

					if (lngLat.length >= 2 && gp1.getLatitudeE6() != 0
							&& gp1.getLongitudeE6() != 0
							&& gp2.getLatitudeE6() != 0
							&& gp2.getLongitudeE6() != 0) {

						// for GeoPoint, first:latitude, second:longitude
						gp2 = new GeoPoint(
								(int) (Double.parseDouble(lngLat[1]) * 1E6),
								(int) (Double.parseDouble(lngLat[0]) * 1E6));

						if (gp2.getLatitudeE6() != 22200000) {
							mMapView.getOverlays().add(
									new RouteOverlay(gp1, gp2, 2, color));

						}
					}
					// Log.d(myapp.APP,"pair:" + pairs[i]);
				}
				mMapView.getOverlays().add(new RouteOverlay(gp2, gp2, 3));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		mMapView.setEnabled(true);
		mMapView.invalidate();
	}

}
