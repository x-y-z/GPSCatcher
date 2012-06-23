package edu.upenn.cis542;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class DbCheckActivity extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dbcheck);
		String locations = LoginActivity.cserver.getLocations();
		TextView tv = (TextView)findViewById(R.id.db);
		tv.setText("Longitude        " + "Latitude\n" + locations);
	}
	
}
