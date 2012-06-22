package edu.upenn.cis542;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainMenuActivity extends Activity {
	public static final int MapViewActivity_ID = 1;
	public static final int ChangeActivity_ID = 2;
	private String ipAddr;
	private String port;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.menu);
		Intent i = getIntent();
		ipAddr = i.getStringExtra("IP_ADDR");
		port = i.getStringExtra("PORT_NUM");
	}

	public void onViewClick(View view) {
		Intent i = new Intent(this, MapViewActivity.class);
		i.putExtra("IP_ADDR", ipAddr);
		i.putExtra("PORT_NUM", port);
		startActivityForResult(i, MapViewActivity_ID);
	}

	public void onChangeClick(View view) {
		Intent i = new Intent(this, ChangeActivity.class);
		startActivityForResult(i, ChangeActivity_ID);
	}

	public void onQuitClick(View view) {
		finish();
	}
}
