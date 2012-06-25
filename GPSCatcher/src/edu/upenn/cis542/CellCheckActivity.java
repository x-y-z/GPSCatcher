package edu.upenn.cis542;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class CellCheckActivity extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.celldbcheck);
		String locations = LoginActivity.db.getLocations("androidTable");
		TextView tv = (TextView)findViewById(R.id.celldb);
		tv.setText("Longitude        " + "Latitude\n" + locations);
	}
	
}
