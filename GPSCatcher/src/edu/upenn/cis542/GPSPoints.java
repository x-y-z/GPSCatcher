package edu.upenn.cis542;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class GPSPoints extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private Drawable histMarker;

	public GPSPoints(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		// TODO Auto-generated constructor stub
	}

	public GPSPoints(Drawable defaultMarker, Drawable oldMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		histMarker = oldMarker;
		mContext = context;
	}

	public void addOverlay(OverlayItem overlay) {
		if (!mOverlays.isEmpty()) {
			mOverlays.get(mOverlays.size() - 1).setMarker(histMarker);
		}
		mOverlays.add(overlay);
		// for item updating
		setLastFocusedIndex(-1);
		populate();
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return mOverlays.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return mOverlays.size();
	}

	@Override
	protected boolean onTap(int index) {
		OverlayItem item = mOverlays.get(index);
		if (index == mOverlays.size() - 1) {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle(item.getTitle());
			dialog.setMessage(item.getSnippet());
			dialog.setPositiveButton(R.string.chase,
					new DialogInterface.OnClickListener() {
						// This is the method to call when the button is clicked
						public void onClick(DialogInterface dialog, int id) {
							
						}
					});
			dialog.show();
		} else {
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle(item.getTitle());
			dialog.setMessage(item.getSnippet());
			dialog.show();
		}
		return true;
	}

}
