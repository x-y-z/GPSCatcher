package edu.upenn.cis542;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ChangeActivity extends Activity {
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change);
	}

	/**
	 * Called when user clicks on the Submit button after typing.
	 * 
	 * @param view
	 */
	public void onSubmitClick(View view) {
		EditText typingDirections = (EditText)findViewById(R.id.typing_directions);
		String directions = typingDirections.getText().toString();
		
		EditText typingFrequency = (EditText)findViewById(R.id.typing_freq);
		String freq = typingFrequency.getText().toString();
		
		MapViewActivity.num_directions = directions;
		MapViewActivity.frequency = freq;
		
		finish();
	}
}
