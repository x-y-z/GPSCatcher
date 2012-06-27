/*
 * Chasing game activity
 * display Arduino location, Phone location,
 * POIs around Arduino, navigation
 * 
 */

package edu.upenn.cis542;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.TimeZone;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MapViewActivity extends MapActivity {
	private TextView tv;

	/** current date from arduino */
	private Date curDate;
	/** current gps location from arduino */
	private GeoPoint curPos;

	private GPSPoints itemizedoverlay;
	private List<Overlay> mapOverlays;
	private Projection project;
	private MapView mapView;
	private Context thisContext;

	/** number of gps location to be read */
	public static int num_directions = -1;
	/** interval between two reads */
	public static int frequency = -1;

	private String ipAddr;
	private String port;
	private GPSPoints POIs;
	/** restaurants */
	private GPSPoints phones;
	private double phoneToArd = 0;

	public static GeoPoint phonePos = null;
	public static int idle = 1000;
	private int slowDown = 0;

	private int inquiry = 1;

	private int winDist = 10;
	private int ignoreOffset = 5;

	private GeoPoint prePos = null;
	private double totalDistance = 0;
	private LocationManager locationManager;
	private LocationListener locationListener;
	public String provider;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		thisContext = this;

		Intent i = getIntent();
		ipAddr = i.getStringExtra("IP_ADDR");
		port = i.getStringExtra("PORT_NUM");

		tv = (TextView) findViewById(R.id.msg);

		mapView = (MapView) findViewById(R.id.mapview);
		mapView.setBuiltInZoomControls(true);

		mapOverlays = mapView.getOverlays();
		project = mapView.getProjection();

		// load icons from resources
		Drawable red_dot = getResources().getDrawable(R.drawable.red_dot);
		red_dot.setBounds(-red_dot.getIntrinsicWidth() / 20,
				-red_dot.getIntrinsicHeight() / 10,
				red_dot.getIntrinsicWidth() / 20, 0);

		Drawable star = getResources().getDrawable(R.drawable.star);
		star.setBounds(-star.getIntrinsicWidth() / 12,
				-star.getIntrinsicHeight() / 6, star.getIntrinsicWidth() / 12,
				0);

		Drawable pin = getResources().getDrawable(R.drawable.pin);
		pin.setBounds(-pin.getIntrinsicWidth() / 10,
				-pin.getIntrinsicHeight() / 10, 0, 0);

		Drawable drawable = this.getResources().getDrawable(
				R.drawable.androidmarker);
		itemizedoverlay = new GPSPoints(drawable, red_dot, this);
		POIs = new GPSPoints(star, this);
		phones = new GPSPoints(pin, this);

		// initial inquiry
		num_directions = 9;
		frequency = 1;

		new GetArduinoPos().execute("");

		itemizedoverlay.getMapView(mapView, POIs);
		POIs.getMapView(mapView, null);

		// register location manager
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		provider = LocationManager.GPS_PROVIDER;
		Location lastLoc = locationManager.getLastKnownLocation(provider);

		if (lastLoc != null) {
			phonePos = new GeoPoint((int) (lastLoc.getLatitude() * 1E6),
					(int) (lastLoc.getLongitude() * 1E6));
			OverlayItem aPhone = new OverlayItem(phonePos, "I",
					lastLoc.getTime() + "");
			phones.addOverlay(aPhone);
		}

		locationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location arg0) {
				Location loc = arg0;
				GeoPoint phoneCurPos = new GeoPoint(
						(int) (loc.getLatitude() * 1E6),
						(int) (loc.getLongitude() * 1E6));

				if (curPos != null)
					phoneToArd = CalculateDistance(phoneCurPos, curPos);

				// you win, stop gps listening, and stop arduino
				if (phoneToArd < winDist) {
					locationManager.removeUpdates(locationListener);
					inquiry = 0;
					AlertDialog.Builder dialog = new AlertDialog.Builder(
							thisContext);
					dialog.setTitle("Win!");
					dialog.setMessage("You have caught Arduino!\nYou win!");
					dialog.setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									finish();
								}
							});
					dialog.show();

				}

				if (phonePos == null)
					phonePos = phoneCurPos;
				else if (CalculateDistance(phonePos, phoneCurPos) < 5) {
					return;
				} else {
					phonePos = phoneCurPos;
				}

				OverlayItem overlayitem = new OverlayItem(phonePos, "I",
						curDate.toLocaleString());
				phones.clearAll();
				phones.addOverlay(overlayitem);

				Toast.makeText(
						getApplicationContext(),
						"Phone is at: (" + loc.getLatitude() + ", "
								+ loc.getLongitude() + ")", Toast.LENGTH_LONG)
						.show();

				if (loc != null) {
					LoginActivity.db.insertLocation("androidTable",
							loc.getTime() + "", loc.getLatitude() + "",
							loc.getLongitude() + "");

				}
			}

			@Override
			public void onProviderDisabled(String provider) {

				Toast.makeText(getApplicationContext(),
						"GPS provider is disabled.", Toast.LENGTH_LONG).show();

			}

			@Override
			public void onProviderEnabled(String provider) {

				Toast.makeText(getApplicationContext(),
						"GPS provider is enabled.", Toast.LENGTH_LONG).show();

			}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {

			}
		};
		// Currently defaults to 10 ms and 0.1 meter changes
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				(long) 10, (float) 0.1, locationListener);

		// add arduino, restaurant, phone into map layout
		mapOverlays.add(itemizedoverlay);
		mapOverlays.add(POIs);
		mapOverlays.add(phones);
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	// connect to the C server
	private String comServer() {
		Socket s;
		String str = null;
		try {
			s = new Socket(ipAddr, Integer.parseInt(port));
			InputStream in = s.getInputStream();
			PrintWriter out = new PrintWriter(s.getOutputStream(), true);

			// send command to server
			if (num_directions == -1 || frequency == -1) {
				out.println("1,0.");
			} else {
				out.println(num_directions + "," + frequency + ".");
			}

			// Wait for locations to be sent
			while (in.available() == 0)
				;

			byte[] buf = new byte[in.available()];
			in.read(buf);

			str = new String(buf);
			s.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			str = e.toString();
			e.printStackTrace();
		}

		return str;
	}

	private class GetArduinoPos extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {

			// idle for a while
			try {
				Thread.sleep(idle);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return comServer();
		}

		protected void onPostExecute(String result) {
			// parsing job
			DateFormat formatter;
			if (result.startsWith("java.net")) {
				Toast.makeText(getApplicationContext(), result,
						Toast.LENGTH_LONG).show();
			} else if (result.equals("stop\n")) {
				tv.setText(result);
				return;
			} else {
				num_directions = 1;
				String[] array = result.split("\n");
				int entryCnt = Integer.parseInt(array[0]) * 4;

				for (int i = 0; i < entryCnt; i += 4) {
					// parsing date, time
					String aDate = array[i + 1] + " " + array[i + 2];
					formatter = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
					formatter.setTimeZone(TimeZone.getTimeZone("GMT"));
					try {
						curDate = (Date) formatter.parse(aDate);
					} catch (ParseException e) {
						e.printStackTrace();
					}

					Date phoneDate = new Date();
					// check if the gps location is out of date
					if (Math.abs(phoneDate.getTime() - curDate.getTime()) > 10000) {
						Toast.makeText(
								getApplicationContext(),
								"Arduino GPS Location is out of date!\nPhone:"
										+ phoneDate.toLocaleString()
										+ "\nArduino:"
										+ curDate.toLocaleString(),
								Toast.LENGTH_SHORT).show();
					}

					// parsing GPS locations
					String la = new String(array[i + 3]);
					String lo = new String(array[i + 4]);
					la = la.replaceAll("\\s", "");
					lo = lo.replaceAll("\\s", "");

					double laSign = 1.0, loSign = 1.0;

					if (la.startsWith("-"))
						laSign = -1.0;

					if (lo.startsWith("-"))
						loSign = -1.0;

					if (la.startsWith("+"))
						la = la.substring(1);
					if (lo.startsWith("+"))
						lo = lo.substring(1);

					String[] lat = la.split("\\*|'|\"");

					double offset = 0;// new Random().nextDouble() * 2;

					double latD = (double) Integer.parseInt(lat[0])
							+ laSign
							* (((double) Integer.parseInt(lat[1])) * 60
									+ Double.parseDouble(lat[2]) + offset)
							/ 3600;
					String[] lon = lo.split("\\*|'|\"");

					offset = 0;// new Random().nextDouble() * 2;
					double lonD = (double) Integer.parseInt(lon[0])
							+ loSign
							* (((double) Integer.parseInt(lon[1])) * 60
									+ Double.parseDouble(lon[2]) + offset)
							/ 3600;
					curPos = new GeoPoint((int) (latD * 1E6),
							(int) (lonD * 1E6));

					// calculate total distance traveled in this running
					if (prePos != null) {
						double thisJump = CalculateDistance(prePos, curPos);
						// if arduino is moving far away
						if (thisJump > ignoreOffset) {
							slowDown = 0;
							mapOverlays.add(new LineOnMap(prePos, curPos,
									project));
							totalDistance += thisJump;
							OverlayItem overlayitem = new OverlayItem(curPos,
									"Arduino", curDate.toLocaleString());
							itemizedoverlay.addOverlay(overlayitem);
							mapView.invalidate();
							// store into database, in original format, GMT
							LoginActivity.db.insertLocation("cserverTable",
									formatter.format(curDate), latD + "", lonD
											+ "");

							if (frequency != 1 && frequency < 6000) {
								frequency = 1;
								Toast.makeText(
										getApplicationContext(),
										"Arduino is moving fast. Speed up GPS inquiry frequency!",
										Toast.LENGTH_SHORT).show();
							}
						} else {
							// if arduino is not moving, ignore new position and
							// slow down
							// inquiry frequency
							if (frequency < 6000) {
								slowDown++;
								if (slowDown > 5) {
									frequency += 1000;
									if (frequency > 5000)
										frequency = 5000;

									Toast.makeText(
											getApplicationContext(),
											"Arduino is not moving fast. Slow down GPS inquiry frequency!",
											Toast.LENGTH_SHORT).show();
								}
							}
						}
					} else {
						OverlayItem overlayitem = new OverlayItem(curPos,
								"Arduino", curDate.toLocaleString());
						itemizedoverlay.addOverlay(overlayitem);
						mapView.invalidate();
						// store into database, GMT
						LoginActivity.db
								.insertLocation("cserverTable",
										formatter.format(curDate), latD + "",
										lonD + "");
					}
					prePos = curPos;

				}

				// show current information
				tv.setText("Arduino is at:(" + (double) curPos.getLatitudeE6()
						/ 1E6 + "," + (double) curPos.getLongitudeE6() / 1E6
						+ ")\n" + "Dist. behind:"
						+ new DecimalFormat("#.##").format(phoneToArd) + "m.\n"
						+ "Arduino has traveled:"
						+ new DecimalFormat("#.##").format(totalDistance)
						+ " m.\n");

				

				if (inquiry == 1) {
					new GetArduinoPos().execute("");
				}

			}
		}

	}

	// calculate distance between two GPS points
	private static float CalculateDistance(GeoPoint s, GeoPoint e) {
		float[] results = new float[1];
		Location.distanceBetween(s.getLatitudeE6() / 1E6,
				s.getLongitudeE6() / 1E6, e.getLatitudeE6() / 1E6,
				e.getLongitudeE6() / 1E6, results);
		return results[results.length - 1];
	}

	// start arduino and stop it
	public void onRefreshClick(View v) {
		Button switcher = (Button) findViewById(R.id.refresh);
		if (inquiry == 1) {
			inquiry = 0;
			switcher.setText("Start");
			// frequency = 0;
			num_directions = 0;
			while (!comServer().contains("stop"))
				;

		} else if (inquiry == 0) {
			inquiry = 1;
			switcher.setText("Stop");
			// frequency = 1;
			num_directions = 9;
			new GetArduinoPos().execute("");
		}
	}

	// go to phone location
	public void onPhoneLocation(View v) {
		if (phonePos == null) {
			Toast.makeText(getApplicationContext(), "GPS not ready yet",
					Toast.LENGTH_LONG).show();
			return;
		}

		MapView mapView = (MapView) findViewById(R.id.mapview);
		MapController mc = mapView.getController();
		mc.animateTo(phonePos);
		mc.setZoom(20);
		mapView.invalidate();
	}

	// go to arduino location
	public void onArdLocation(View v) {
		MapView mapView = (MapView) findViewById(R.id.mapview);
		MapController mc = mapView.getController();
		mc.animateTo(curPos);
		mc.setZoom(20);
		mapView.invalidate();
	}

	@Override
	public void onBackPressed() {
		locationManager.removeUpdates(locationListener);
		inquiry = 0;
		finish();

	}
}
