package edu.upenn.cis542;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

public class MapViewActivity extends MapActivity {
	private TextView tv;
	private Date curDate;
	private GeoPoint curPos;
	private GPSPoints itemizedoverlay;
	private List<Overlay> mapOverlays;
	private Projection project;
	public static String num_directions = "";
	public static String frequency = "";
	private String ipAddr;
	private String port;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent i = getIntent();
		ipAddr = i.getStringExtra("IP_ADDR");
		port = i.getStringExtra("PORT_NUM");
        
        tv = (TextView)findViewById(R.id.msg);
        
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        mapOverlays = mapView.getOverlays();
        project = mapView.getProjection();
        
        Drawable drawable = this.getResources().getDrawable(R.drawable.androidmarker);
        itemizedoverlay = new GPSPoints(drawable, this);
        
        GeoPoint point = new GeoPoint(19240000,-99120000);
        OverlayItem overlayitem = new OverlayItem(point, "Hola, Mundo!", "I'm in Mexico City!");
        
        new GetArduinoPos().execute("");
        
        itemizedoverlay.addOverlay(overlayitem);
        mapOverlays.add(itemizedoverlay);
    }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
	
	private class GetArduinoPos extends AsyncTask<String, Void, String>{

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			Socket s;
			String str = null;
			try {
				s = new Socket(ipAddr, Integer.parseInt(port));
				InputStream in = s.getInputStream();
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				if(num_directions.equals("") || frequency.equals("")) {
					out.println("5,0.");
				} else {
					out.println(num_directions + "," + frequency + ".");
				}
		        while(in.available() == 0);
		        
		        byte[] buf = new byte[in.available()];
		        in.read(buf);

		        str = new String(buf);

		        //System.out.println(str);

				s.close();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return str;
		}
		
		protected void onPostExecute(String result){
			float totalDistance = 0f;
			DateFormat formatter;
			if (result.equals("stop\n")){
				tv.setText(result);
				return;
			}
			else{
				String[] array = result.split("\n");
				int entryCnt = Integer.parseInt(array[0]) * 4;
				GeoPoint prePos = null;
				
				for (int i = 0; i < entryCnt; i += 4)
				{
					String aDate = array[i + 1] + " " + array[i + 2];
					formatter = new SimpleDateFormat("mm/dd/yy hh:mm:ss");
					formatter.setTimeZone(TimeZone.getTimeZone("GMT-1"));
					try {
						curDate = (Date)formatter.parse(aDate);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
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
					
					double latD = (double)Integer.parseInt(lat[0]) + 
							laSign*(((double)Integer.parseInt(lat[1]))*60 + Double.parseDouble(lat[2]) + (double)i/10)/3600;
					String[] lon = lo.split("\\*|'|\"");
	
					double lonD = (double)Integer.parseInt(lon[0]) + 
							loSign*(((double)Integer.parseInt(lon[1]))*60 + Double.parseDouble(lon[2]) + (double)i/(10+i))/3600;
					curPos = new GeoPoint((int)(latD*1E6), (int)(lonD*1E6));
					tv.setText(curDate+"\n"+curPos);
					
					if (prePos != null){
						mapOverlays.add(new LineOnMap(prePos, curPos, project));
						totalDistance += CalculateDistance(prePos, curPos);
					}
						
					
					OverlayItem overlayitem = new OverlayItem(curPos, "Zi", "Home" + i/4);
					itemizedoverlay.addOverlay(overlayitem);
					
					prePos = curPos;
					
				}
				
				tv.setText(curDate+"\n"+curPos+"\n"+totalDistance+"meters");
				
				MapView mapView = (MapView) findViewById(R.id.mapview);
				MapController mc = mapView.getController();
				mc.animateTo(curPos);
				mc.setZoom(30);
				mapView.invalidate();
			}
		}
		
		private float CalculateDistance(GeoPoint s, GeoPoint e){
			float [] results = new float[1];
			Location.distanceBetween(s.getLatitudeE6()/1E6, s.getLongitudeE6()/1E6, e.getLatitudeE6()/1E6, e.getLongitudeE6()/1E6, results);
			return results[results.length - 1];
			
		}
		
	}
	
}