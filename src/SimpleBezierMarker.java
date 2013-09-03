import java.awt.Color;
import java.util.List;

import processing.core.PGraphics;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimpleLinesMarker;
import de.fhpotsdam.unfolding.utils.GeoUtils;
import de.fhpotsdam.unfolding.utils.MapPosition;


public class SimpleBezierMarker extends SimpleLinesMarker
{
	int offset = 0;
	float radius;
	protected int space = 10;
	CountryData countryData;
	Location loc1;
	Location loc2;
	float bearing;
	UnfoldingMap mymap;
	
	public SimpleBezierMarker()
	{
		super();
	}
	
	public SimpleBezierMarker(List<Location> locations) 
	{
		super(locations);
	}
	
	public SimpleBezierMarker(Location startLocation, Location endLocation, CountryData countryData, int color, float radius, float bearing, UnfoldingMap mymap) 
	{
		super.addLocations(startLocation, endLocation);
		this.loc1 = startLocation;
		this.loc2 = endLocation;
		this.bearing = bearing;
		this.mymap = mymap;
		
		this.countryData = countryData;
		//define color for bezier and points
		Color bezierColor = new Color(255, 194, 104);//(160, 18, 52);
		//color = bezierColor.getRGB();
		this.color = color;
		//here radius is really the diameter for the ellipse
		this.radius = radius*2;
		
		bezierColor = new Color(255, 194, 104); //(48, 100, 247);
		highlightColor = bezierColor.getRGB();
		strokeWeight = 1;
	}
	
	public void changeColor(int newcolor)
	{
	  color = newcolor;
	}
	
	@Override
	public void draw(PGraphics pg, List<MapPosition> mapPositions) 
	{
		if (mapPositions.isEmpty())
			return;

		//start drawing
		pg.smooth();
		pg.pushStyle();
		pg.noFill();
		pg.stroke(isSelected()?	highlightColor:color, isSelected()? 200:20);

		pg.beginShape();
		MapPosition last = mapPositions.get(0);
		for (int i = 1; i < mapPositions.size(); ++i) 
		{
			MapPosition mp = mapPositions.get(i);
			pg.strokeWeight(isSelected()?strokeWeight*4:strokeWeight);
			pg.fill(isSelected()?	highlightColor:color, 150);
			pg.ellipse(mp.x, mp.y, radius, radius);
			//pg.ellipse(last.x, last.y, radius, radius); //center is always Spain
			pg.noFill();
			pg.strokeWeight(strokeWeight*2);
			/*pg.bezier( mp.x, mp.y, 
          mp.x+offset, mp.y+offset, 
          last.x-offset, last.y-offset, 
          last.x, last.y);*/
			last = mp;
		}
		pg.endShape();
		pg.popStyle();
		
      double dist = GeoUtils.getDistance(loc2, loc1);
      pg.noFill();
      pg.smooth();
      pg.strokeWeight(isSelected()?strokeWeight*2:strokeWeight);
      pg.stroke(isSelected()? highlightColor:color, isSelected()? 200:25);
      pg.beginShape();
      for (float d = 0; d < dist; d += 200) {
              Location tweenLocation = GeoUtils.getDestinationLocation(loc2, bearing,
                              (float) d);
              float[] tweenPos = mymap.getScreenPositionFromLocation(tweenLocation);
              pg.vertex(tweenPos[0], tweenPos[1]);
      }
      pg.endShape();
	}
}
