package edu.upenn.cis542;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class ChangeActivity extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change);
		
		TextView freq = (TextView)findViewById(R.id.frequency);
		freq.setText(MapViewActivity.frequency + "");
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
