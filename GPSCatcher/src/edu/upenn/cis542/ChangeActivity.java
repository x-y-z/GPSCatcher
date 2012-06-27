package edu.upenn.cis542;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class ChangeActivity extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change);
		
		EditText freq = (EditText)findViewById(R.id.freq);
		freq.setText(MapViewActivity.frequency/1000 + "");
		
		EditText num = (EditText)findViewById(R.id.num);
		num.setText(MapViewActivity.num_directions + "");
	}

	public void onSubmitClick(View v){
		EditText freq = (EditText)findViewById(R.id.freq);
		String freqS = freq.getText().toString();
		
		EditText num = (EditText)findViewById(R.id.num);
		String numS = num.getText().toString();
		
		if (freqS != null && !freqS.equals(""))
			MapViewActivity.frequency = Integer.parseInt(freqS)*1000;
		
		if (numS != null && !numS.equals(""))
			MapViewActivity.num_directions = Integer.parseInt(numS);
		finish();
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
