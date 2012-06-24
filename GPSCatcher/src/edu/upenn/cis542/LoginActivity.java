package edu.upenn.cis542;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	public static AndroidDb android;
	public static CServerDb cserver;
	
	public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.login);
        
        EditText ipTyping = (EditText)findViewById(R.id.ipAddr);
        //code from http://stackoverflow.com/questions/5798140/press-many-times-validate-ip-address-in-edittext-while-typing
        InputFilter[] filters = new InputFilter[1];
        filters[0] = new InputFilter() {
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                if (end > start) {
                    String destTxt = dest.toString();
                    String resultingTxt = destTxt.substring(0, dstart) + source.subSequence(start, end) + destTxt.substring(dend);
                    if (!resultingTxt.matches ("^\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3}(\\.(\\d{1,3})?)?)?)?)?)?")) { 
                        return "";
                    } else {
                        String[] splits = resultingTxt.split("\\.");
                        for (int i=0; i<splits.length; i++) {
                            if (Integer.valueOf(splits[i]) > 255) {
                                return "";
                            }
                        }
                    }
                }
            return null;
            }
        };

        ipTyping.setFilters(filters);
        
	}
	
	public void loginClick(View view) {
		EditText ipTyping = (EditText)findViewById(R.id.ipAddr);
		String ip = ipTyping.getText().toString();
		
		EditText portTyping = (EditText)findViewById(R.id.portNum);
		String port = portTyping.getText().toString();
		
		if (ip.equals("") || port.equals(""))
		{
			Toast.makeText(this, "Please enter both ip address and port number", Toast.LENGTH_SHORT).show();
			return;
		}
				
		try {
			Socket s = new Socket(ip, Integer.parseInt(port));
			s.close();
		} catch (UnknownHostException e) {
			Toast.makeText(this, "This is not a valid host.", Toast.LENGTH_SHORT).show();
			return;
		} catch (IOException e) {
			Toast.makeText(this, "This is not a valid socket/port.", Toast.LENGTH_SHORT).show();
			return;
		}
		
		Intent i = new Intent(this, MainMenuActivity.class);
		i.putExtra("IP_ADDR", ip);
		i.putExtra("PORT_NUM", port);
		android = new AndroidDb(this); 
		cserver = new CServerDb(this);
		cserver.clear();
		startActivity(i);
	}

	public void quitClick(View view) {
		finish();
	}
	
	
}
