import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import processing.core.PApplet;


public class CA
{
  PApplet parent;
  public String name;
  public List<Double>  emigrants2011 = new ArrayList<Double>();
  public List<Double>  emigrants2010 = new ArrayList<Double>();
  
  int totalMigrants2011 = 0;
  int minMigrants2011 = 0;
  int maxMigrants2011 = 0;

  int totalMigrants2010 = 0;
  int minMigrants2010 = 0;
  int maxMigrants2010 = 0;
  
  CA(PApplet p)
  {
    parent = p;
  }
  
  public void generateTotals()
  {
    int i;
    for(i=0; i<emigrants2011.size(); i++)
      totalMigrants2011 += emigrants2011.get(i);
    
    for(i=0; i<emigrants2010.size(); i++)
      totalMigrants2010 += emigrants2010.get(i);
    
    minMigrants2011 = Collections.min(emigrants2011).intValue();
    maxMigrants2011 = Collections.max(emigrants2011).intValue();
    
    minMigrants2010 = Collections.min(emigrants2010).intValue();
    maxMigrants2010 = Collections.max(emigrants2010).intValue();
  }
}
