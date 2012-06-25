package edu.upenn.cis542;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class GPSPoints extends ItemizedOverlay<OverlayItem> {

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private Drawable histMarker;
	private MapView mapView = null;
	private int isPOI = 0;
	private GPSPoints POIs = null;

	public GPSPoints(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		// TODO Auto-generated constructor stub
	}

	public GPSPoints(Drawable defaultMarker, Drawable oldMarker, Context context) {
		super(boundCenterBottom(defaultMarker));
		populate();
		histMarker = oldMarker;
		mContext = context;
		isPOI = 0;
	}

	public GPSPoints(Drawable defaultMarker, Context context) {
		super(defaultMarker);
		populate();

		histMarker = null;
		mContext = context;
		isPOI = 1;
	}

	public void getMapView(MapView aView, GPSPoints aPOI) {
		mapView = aView;
		POIs = aPOI;
	}

	public void addOverlay(OverlayItem overlay) {
		if (!mOverlays.isEmpty() && histMarker != null) {
			mOverlays.get(mOverlays.size() - 1).setMarker(histMarker);
		}
		mOverlays.add(overlay);
		// for item updating
		setLastFocusedIndex(-1);
		populate();
	}

	public void clearAll() {
		mOverlays.clear();
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
		final OverlayItem item = mOverlays.get(index);
		if (isPOI == 0) {

			if (index == mOverlays.size() - 1) {
				AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
				dialog.setTitle(item.getTitle());
				dialog.setMessage(item.getSnippet());
				dialog.setPositiveButton(R.string.chase,
						new DialogInterface.OnClickListener() {
							// This is the method to call when the button is
							// clicked
							public void onClick(DialogInterface dialog, int id) {
								POIs.clearAll();

								Toast.makeText(
										mContext,
										"Arduino is at:"
												+ item.getPoint().toString(),
										Toast.LENGTH_LONG).show();

								Navigation ng = new Navigation();
								try {
									ng.performSearch(MapViewActivity.phonePos,
											item.getPoint());
									ng.drawPath(mapView);

									MapController mc = mapView.getController();
									mc.animateTo(MapViewActivity.phonePos);
									mc.setZoom(15);
									mapView.invalidate();

								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});

				dialog.setNegativeButton(R.string.nchase,
						new DialogInterface.OnClickListener() {
							// This is the method to call when the button is
							// clicked
							public void onClick(DialogInterface dialog, int id) {
								// provide restaurant at curPos
								PlacesSearch ps = new PlacesSearch();
								try {
									ps.performSearch((double) item.getPoint()
											.getLatitudeE6() / 1E6,
											(double) item.getPoint()
													.getLongitudeE6() / 1E6);
									PlacesList res = ps.getPlaces();

									if (res != null) {
										String msg = "STATUS:" + res.status
												+ ", Find "
												+ res.results.size() + " POIs";
										Toast.makeText(mContext, msg,
												Toast.LENGTH_LONG).show();
										POIs.clearAll();
										for (Place pl : res.results) {
											GeoPoint thisPos = pl.getGeo();
											String thisName = "Name:" + pl.name;
											String thisRating = "Rating is:"
													+ pl.rating;
											OverlayItem aPOI = new OverlayItem(
													thisPos, thisName,
													thisRating);
											POIs.addOverlay(aPOI);
										}

									}
								} catch (Exception e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						});

				dialog.show();
			} else {
				AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
				dialog.setTitle(item.getTitle());
				dialog.setMessage(item.getSnippet());
				dialog.show();
			}

		} else {// poi
			AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
			dialog.setTitle(item.getTitle());
			dialog.setMessage(item.getSnippet());
			dialog.setPositiveButton(R.string.navito,
					new DialogInterface.OnClickListener() {
						// This is the method to call when the button is
						// clicked
						public void onClick(DialogInterface dialog, int id) {
							Toast.makeText(mContext,
									"It is at:" + item.getPoint().toString(),
									Toast.LENGTH_LONG).show();

							System.out.println("begin to find restaurant");
							Navigation ng = new Navigation();
							try {
								ng.performSearch(MapViewActivity.phonePos,
										item.getPoint());

								ng.drawPath(mapView);

								MapController mc = mapView.getController();
								mc.animateTo(MapViewActivity.phonePos);
								mc.setZoom(15);
								mapView.invalidate();

							} catch (Exception e) {
								// TODO Auto-generated catch block
								System.out.println("find restaurant error");
								e.printStackTrace();
							}
						}
					});
			dialog.show();
		}
		return true;
	}

}
