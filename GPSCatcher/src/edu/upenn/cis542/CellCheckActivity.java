package edu.upenn.cis542;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

public class CellCheckActivity extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.celldbcheck);
		String locations = LoginActivity.db.getLocations("androidTable");
		TextView tv = (TextView)findViewById(R.id.celldb);
		tv.setMovementMethod(new ScrollingMovementMethod());
		tv.setText("Longitude, Latitude\n" + locations);
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
