package edu.upenn.cis542;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DbCheckActivity extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gpsdbcheck);
		String locations = LoginActivity.db.getLocations("cserverTable");
		TextView tv = (TextView)findViewById(R.id.gpsdb);
		tv.setText("Longitude        " + "Latitude\n" + locations);
	}
	
}
