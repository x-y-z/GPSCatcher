/*
 * Draw lines between points
 * 
 * Code is modified from: 
 * http://stackoverflow.com/questions/2176397/drawing-a-line-path-on-google-maps
 * 
 */

package edu.upenn.cis542;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

public class LineOnMap extends Overlay {
	private GeoPoint start, end;
	private Projection projection;
	
	public LineOnMap(GeoPoint start, GeoPoint end, Projection projection){
		this.start = start;
		this.end = end;
		this.projection = projection;
	}
	public void draw(Canvas canvas, MapView mapv, boolean shadow){
		super.draw(canvas, mapv, shadow);

        Paint mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);

        Point p1 = new Point();
        Point p2 = new Point();
        Path path = new Path();

		projection.toPixels(start, p1);
        projection.toPixels(end, p2);

        path.moveTo(p2.x, p2.y);
        path.lineTo(p1.x,p1.y);

        canvas.drawPath(path, mPaint);
	}
}
