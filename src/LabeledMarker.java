
import java.awt.Color;

import processing.core.PFont;
import processing.core.PGraphics;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.SimplePointMarker;

/**
 * Extends point marker to additionally display label. 
 */
public class LabeledMarker extends SimplePointMarker 
{

	private PFont font;
	protected int space = 10;
	float size = 10;
	SimpleBezierMarker simpleBezierMarker;
	String name;
	//int migrants;
	public String totalFormatted;

	public LabeledMarker(Location location, SimpleBezierMarker simpleBezierMarker, PFont font, String totalFormatted) 
	{
		this.location = location;
		this.font = font;
		this.simpleBezierMarker = simpleBezierMarker;
		this.totalFormatted = totalFormatted;
		name = simpleBezierMarker.countryData.name;
		//migrants = simpleBezierMarker.countryData.maxMigrants;
		
		Color bezierColor = new Color(160, 18, 52);
		color = bezierColor.getRGB();
	}
	
	

	/**
	 * Displays this marker's name in a box.
	 */
	public void draw(PGraphics pg, float x, float y) 
	{
		pg.pushStyle();
		pg.pushMatrix();
			
			//visible point
			pg.fill(color, 255);
			pg.stroke(strokeColor, 8);
			pg.ellipse(x, y, 2, 2);
			
			//interactive area
			pg.fill(color, 5);
			pg.stroke(strokeColor, 8);
			pg.ellipse(x, y, size, size);
			
			if (selected && name != null) 
			{
				pg.textFont(font);
				pg.textSize(11);
				pg.fill(255, 190);
				pg.stroke(33, 90);
				
				
				//take care of screen limits
				float myx = x;
				float myy = y;
				if(myy < 30)
					myy += 30;
				if(myx > 900)
					myx = 850;
				
				
				pg.rect(myx + strokeWeight / 2, 
						myy - font.getSize() + strokeWeight / 2 - space,
						pg.textWidth(name + " (" + totalFormatted + ")") + space * 1.5f, 
						font.getSize() + space - 10);
				pg.fill(0, 0, 0);
				pg.text(name + " (" + totalFormatted + ")", Math.round(myx + space * 0.75f + strokeWeight / 2),
						Math.round(myy + strokeWeight / 2 - space * 0.75f)-8);
			}
			
		pg.popMatrix();
		pg.popStyle();
	}
	
	
	
	public void selectConnection(boolean state)
	{
		super.setSelected(state);
		simpleBezierMarker.setSelected(state);
	}
	
	
	public CountryData getCountryData()
	{
		return simpleBezierMarker.countryData;
	}
}
