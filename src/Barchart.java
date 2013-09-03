import processing.core.PGraphics;


public class Barchart
{
	PGraphics pg;
	
	public Barchart(PGraphics pg)
	{
		this.pg = pg;
	}
	
	public void draw(int x, int y, int width, int height)
	{
		pg.rect(x, y, width, (height == 0)? 1:height);
	}
	
	//public void
}
