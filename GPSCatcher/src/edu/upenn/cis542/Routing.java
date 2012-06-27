/*
 * Navigation structure
 * 
 * Code is modified from:
 * http://stackoverflow.com/questions/3109158/how-to-draw-a-path-on-a-map-using-kml-file/3109723#3109723
 * 
 */
package edu.upenn.cis542;

import java.util.ArrayList;
import java.util.Iterator;

public class Routing {

	private ArrayList<Placemark> placemarks = new ArrayList<Placemark>();
	private Placemark currentPlacemark;
	private Placemark routePlacemark;

	public String toString() {
		String s = "";
		for (Iterator<Placemark> iter = placemarks.iterator(); iter.hasNext();) {
			Placemark p = (Placemark) iter.next();
			s += p.getTitle() + "\n" + p.getDescription() + "\n\n";
		}
		return s;
	}

	public void addCurrentPlacemark() {
		placemarks.add(currentPlacemark);
	}

	public ArrayList<Placemark> getPlacemarks() {
		return placemarks;
	}

	public void setPlacemarks(ArrayList<Placemark> placemarks) {
		this.placemarks = placemarks;
	}

	public Placemark getCurrentPlacemark() {
		return currentPlacemark;
	}

	public void setCurrentPlacemark(Placemark currentPlacemark) {
		this.currentPlacemark = currentPlacemark;
	}

	public Placemark getRoutePlacemark() {
		return routePlacemark;
	}

	public void setRoutePlacemark(Placemark routePlacemark) {
		this.routePlacemark = routePlacemark;
	}

}