package gpsproject;

public class Gpsproject {
	
	public String[] parse(String message) {
		if(message.equals("stop\n")) {
			return null;
		}
		String[] array = message.split("\n");
		/*for(int i = 0; i < Integer.getInteger(array[0]); i++) {
			if(i == 0) {
				array[i] = "Date: " + array[i] + "\n";
			} else if(i == 1) {
				array[i] = "Time: " + array[i] + "\n";
			} else if(i == 2) {
				array[i] = "Latitude: " + array[i] + "\n";
			} else {
				array[i] = "Longitude: " + array[i] + "\n";
			}
		}*/
		return array;
	}
}
