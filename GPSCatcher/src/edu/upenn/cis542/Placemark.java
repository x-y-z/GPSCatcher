/*
 * Placemark structure in navigation KML file
 * 
 * code from
 * http://stackoverflow.com/questions/3109158/how-to-draw-a-path-on-a-map-using-kml-file/3109723#3109723
 * 
 */
package edu.upenn.cis542;

public class Placemark {

	String title;
	String description;
	String coordinates;
	String address;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getCoordinates() {
		return coordinates;
	}

	public void setCoordinates(String coordinates) {
		this.coordinates = coordinates;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
