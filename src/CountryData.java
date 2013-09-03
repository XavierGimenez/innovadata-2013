 import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import processing.core.PApplet;


public class CountryData
{
	PApplet parent;
	public String name;
	public double lat;
	public double lng;
	List<Double> migrants_male2011 = new ArrayList<Double>();
	List<Double> migrants_female2011 = new ArrayList<Double>();
	List<Double> migrants_male2010 = new ArrayList<Double>();
  List<Double> migrants_female2010 = new ArrayList<Double>();
	
	int totalMigrantsMale2011 = 0;
	int totalMigrantsFemale2011 = 0;
	int maxMigrantsMale2011 = 0;
	int maxMigrantsFemale2011 = 0;
	int maxMigrants2011 = 0;
	
	int totalMigrantsMale2010 = 0;
  int totalMigrantsFemale2010 = 0;
  int maxMigrantsMale2010 = 0;
  int maxMigrantsFemale2010 = 0;
  int maxMigrants2010 = 0;
  
	
	CountryData(PApplet p)
	{
		parent = p;
	}
	
	public String toString()
	{
		return name + ", lat/lng: " + lat + "/" + lng;
	}
	
	public void generateTotals()
	{
		int i;
		for(i=0; i<migrants_male2011.size(); i++)
			totalMigrantsMale2011 += migrants_male2011.get(i);
		for(i=0; i<migrants_male2010.size(); i++)
      totalMigrantsMale2010 += migrants_male2010.get(i);
		
		for(i=0; i<migrants_female2011.size(); i++)
			totalMigrantsFemale2011 += migrants_female2011.get(i);
		for(i=0; i<migrants_female2010.size(); i++)
      totalMigrantsFemale2010 += migrants_female2010.get(i);

		maxMigrantsMale2011 = Collections.max(migrants_male2011).intValue();
		maxMigrantsFemale2011 = Collections.max(migrants_female2011).intValue();
		maxMigrants2011 = totalMigrantsMale2011 + totalMigrantsFemale2011;
		
		maxMigrantsMale2010 = Collections.max(migrants_male2010).intValue();
    maxMigrantsFemale2010 = Collections.max(migrants_female2010).intValue();
    maxMigrants2010 = totalMigrantsMale2010 + totalMigrantsFemale2010;
	}
	
	
	public int getMaxMigrantsByAgeAndGender(int year)
	{
	  int maxMigrants;
	  if(year == 2011)
	    maxMigrants = Math.max(maxMigrantsMale2011, maxMigrantsFemale2011);
	  else
      maxMigrants = Math.max(maxMigrantsMale2010, maxMigrantsFemale2010);
		//keep a minimum of value for the axis of nº of migrants
		
	  return (maxMigrants >= 10)?	maxMigrants:10;
	}
	
	
	
	public static Comparator<CountryData> CountryDataComparator2011 = new Comparator<CountryData>()
	{
		public int compare(CountryData c1, CountryData c2)
		{
			Integer migrants1 = new Integer(c1.maxMigrants2011);
			Integer migrants2 = new Integer(c2.maxMigrants2011);
			return migrants2.compareTo(migrants1);
		}
	};
	
	public static Comparator<CountryData> CountryDataComparator2010 = new Comparator<CountryData>()
	    {
	      public int compare(CountryData c1, CountryData c2)
	      {
	        Integer migrants1 = new Integer(c1.maxMigrants2010);
	        Integer migrants2 = new Integer(c2.maxMigrants2010);
	        return migrants2.compareTo(migrants1);
	      }
	    };
}
