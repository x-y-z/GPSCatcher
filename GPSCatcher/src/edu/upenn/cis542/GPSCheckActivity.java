package edu.upenn.cis542;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

public class GPSCheckActivity extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gpsdbcheck);
		String locations = LoginActivity.db.getLocations("cserverTable");
		double td = LoginActivity.db.get24hrDist("cserverTable");
		TextView tv = (TextView)findViewById(R.id.gpsdb);
		tv.setMovementMethod(new ScrollingMovementMethod());
		tv.setText("Total distance in 24hr:\n"+ td +"\nLongitude, Latitude\n" + locations);
	}
	
	/**
	 * Called when user clicks on the Back button after typing.
	 * 
	 * @param view
	 */
	public void onBackClick(View view) {
		finish();
	}
}
