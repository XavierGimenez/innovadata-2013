
/**
 * Author:  Xavi gimenez (xavi@xavigimenez.net) - http://www.xavigimenez.net/blog
 * Date:    19/06/2013 
 */


import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import au.com.bytecode.opencsv.CSVReader;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.providers.MapBox;
import de.fhpotsdam.unfolding.utils.GeoUtils;
import de.fhpotsdam.unfolding.utils.MapUtils;

public class InnovaData extends PApplet
{
  UnfoldingMap mymap;

  // fonts
  PFont font;

  // csv's
  CSVReader readerMale2011, readerFemale2011, readerEmigrantsCCAA2011;
  CSVReader readerMale2010, readerFemale2010, readerEmigrantsCCAA2010;
  
  String[] nextLineMale2011, nextLineFemale2011, nextLineEmigrantsCCAA2011;
  String[] nextLineMale2010, nextLineFemale2010, nextLineEmigrantsCCAA2010;

  // data
  ArrayList<CountryData> countries = new ArrayList<CountryData>();
  CountryData selectedCountryData;
  LabeledMarker selectedLabeledMarker;
  int selectedYear;
  
  int maxCountryMigrantsMale2011 = 0;
  int maxCountryMigrantsFemale2011 = 0;
  float maxMigrantByGenderAndAge2011 = 0;
  int maxCountryMigrantsMale2010 = 0;
  int maxCountryMigrantsFemale2010 = 0;
  float maxMigrantByGenderAndAge2010 = 0;
  
  int maxMigrants2011 = 0;
  int maxMigrants2010 = 0;
  ArrayList<CA> CCAA = new ArrayList<CA>();
  int maxEmigrantCA = 0;
  int minEmigrantCA = Integer.MAX_VALUE;
  int maxEmigrantCAacc2011 = 0;
  int minEmigrantCAacc2011 = Integer.MAX_VALUE;
  int maxEmigrantCAacc2010 = 0;
  int minEmigrantCAacc2010 = Integer.MAX_VALUE;
  
  int totalEmigrantsSpain2011 = 0;
  int totalEmigrantsSpain2010 = 0;
  int totalEmigrantsWorld2011 = 0;
  int totalEmigrantsWorld2010 = 0;

  // markers
  List<Marker> markers = new ArrayList<Marker>();
  List<Marker> markersInteractive = new ArrayList<Marker>();
  int from = color(81, 218, 255); //color(247, 247, 10);
  int to = color(153, 7, 7);

  // app size
  int appWidth = 500;
  int appHeight = 500;
  int appMargin = 10;

  // positions of bar graphs
  int originX;
  int originY;
  int gapVerticalAxis = 7;
  int gapBars = 1;

  // dimension of bar graphs
  int barGraphHeight;
  int barWidth;
  int offsetFromCenter = 5;
  
  //settings for heat map
  int heatMapOriginX = 95;
  int heatMapOriginY = appHeight - 65;
  int heatMapCellWidth = 4;
  int heatMapCellHeight = 8;
  int heatMapCellColorFrom = color(81, 218, 255);
  int heatMapCellColorTo = color(186, 59, 14);
  int heatMapGap = 2;
  
  // data for country bar graph
  int originXCountryGraph = 20;
  int originYCountryGraph = 45;                          
  int barGraphHeigthCountry = 70;
  int barWidthCountry = 5;
  int gapBarsCountry = 1;

  // colors
  float greyTicks = 55;
  float colorBackground = 255;
  int colorBar = color(166, 50, 47);
  int colorBarOver = color(148, 0, 62);
  int colorBarSelected = color(51, 57, 97);
  int colorBarFemale = color(34, 82, 97);

  // some constants
  final int GENDER_MALE = 0;
  final int GENDER_FEMALE = 1;
  final int AGE_MIN = 18;
  final int AGE_MAX = 65;
  final int GRAPH_COUNTRIES = 0;
  final int GRAPH_COUNTRY_MIGRANTS = 1;
  final int SIZE_H1 = 30;
  final int SIZE_H2 = 14;
  final int SIZE_BODY = 12;
  final int SIZE_SMALL = 10;
  final int FOOTER_HEIGHT = 265;
  final int YEAR_2011 = 2011;
  final int YEAR_2010 = 2010;

  // images
  PImage imgManIsotype;
  PImage imgWomanIsotype;
  
//sizes for markers
  int circleAreaFrom = 15;
  int circleAreaTo = 2000;
  
  boolean firstDraw = true;
  

  public static void main(String args[])
  {
    PApplet.main(new String[] { "--present", "InnovaData" });
  }

  
  
  
  boolean sketchFullScreen()
  {
    return true;
  }

  
  
  
  public void setup()
  {
    size(1000, 650);
    appWidth = 1000;
    appHeight = 650;
    originX = 690;
    originY = appHeight - 110;
    selectedYear = YEAR_2011;
    
    int lineCount = 0;
    int x;
    int i;
    
    try 
    {
      //read emigrants to other countries (comunidades autónomas, 2011 and 2010)
      readerEmigrantsCCAA2011 = new CSVReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("emigrantes_provs_2011.csv"), "UTF8"));
      readerEmigrantsCCAA2010 = new CSVReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("emigrantes_provs_2010.csv"), "UTF8"));

      lineCount = 0;
      CA ca;
      
      while((nextLineEmigrantsCCAA2011 = readerEmigrantsCCAA2011.readNext()) != null)
      {
        nextLineEmigrantsCCAA2010 = readerEmigrantsCCAA2010.readNext();
        
        if(nextLineEmigrantsCCAA2011 == null || nextLineEmigrantsCCAA2010 == null)
          break;
        
        ca = new CA(this);
        ca.name = nextLineEmigrantsCCAA2011[0];
        for(x = 1; x < nextLineEmigrantsCCAA2011.length; x++)
          ca.emigrants2011.add(Double.parseDouble(nextLineEmigrantsCCAA2011[x]));
        
        for(x = 1; x < nextLineEmigrantsCCAA2010.length; x++)
          ca.emigrants2010.add(Double.parseDouble(nextLineEmigrantsCCAA2010[x]));
        
        //look for the maximum and minimum
        //maxEmigrantCA = (Collections.max(ca.emigrants2011).intValue() > maxEmigrantCA)? Collections.max(ca.emigrants2011).intValue() : maxEmigrantCA;
        //minEmigrantCA = (Collections.min(ca.emigrants2011).intValue() < minEmigrantCA)? Collections.min(ca.emigrants2011).intValue() : minEmigrantCA;
        
        ca.generateTotals();
        maxEmigrantCAacc2011 = (ca.totalMigrants2011 > maxEmigrantCAacc2011)? ca.totalMigrants2011 : maxEmigrantCAacc2011;
        minEmigrantCAacc2011 = (ca.totalMigrants2011 < minEmigrantCAacc2011)? ca.totalMigrants2011 : minEmigrantCAacc2011;
        totalEmigrantsSpain2011 += ca.totalMigrants2011;
        
        maxEmigrantCAacc2010 = (ca.totalMigrants2010 > maxEmigrantCAacc2010)? ca.totalMigrants2010 : maxEmigrantCAacc2010;
        minEmigrantCAacc2010 = (ca.totalMigrants2010 < minEmigrantCAacc2010)? ca.totalMigrants2010 : minEmigrantCAacc2010;
        totalEmigrantsSpain2010 += ca.totalMigrants2010;
        
        CCAA.add(ca);
        lineCount++;
      }
      
      
      //read emigrants from spain to other countries (2011)
      readerMale2011 = new CSVReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("2011_emigracion_hombres_04.csv"), "UTF8"));
      readerFemale2011 = new CSVReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("2011_emigracion_mujeres_04.csv"), "UTF8"));
      readerMale2010 = new CSVReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("2010_emigracion_hombres_04.csv"), "UTF8"));
      readerFemale2010 = new CSVReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream("2010_emigracion_mujeres_04.csv"), "UTF8"));
      
      lineCount = 0;
      CountryData countryData;
      while ((nextLineMale2011 = readerMale2011.readNext()) != null) 
      {
        nextLineMale2010 = readerMale2010.readNext();
            
        nextLineFemale2011 = readerFemale2011.readNext();
        nextLineFemale2010 = readerFemale2010.readNext();

        // nextLineMale[] is an array of values from the line
        if (lineCount >= 2) 
        {
          countryData = new CountryData(this);
          countryData.name = nextLineMale2011[0];
          try 
          {
            // read data from a line of csv
            countryData.lat = Double.parseDouble(nextLineMale2011[1]);
            countryData.lng = Double.parseDouble(nextLineMale2011[2]);

            for (i = 3; i < nextLineMale2011.length; i++) 
            {
              countryData.migrants_male2011.add(Double.parseDouble(nextLineMale2011[i]));
              countryData.migrants_female2011.add(Double.parseDouble(nextLineFemale2011[i]));
            }
            for (i = 3; i < nextLineMale2010.length; i++) 
            {
              countryData.migrants_male2010.add(Double.parseDouble(nextLineMale2010[i]));
              countryData.migrants_female2010.add(Double.parseDouble(nextLineFemale2010[i]));
            }
            
            // get derivated data
            countryData.generateTotals();
            countries.add(countryData);
            totalEmigrantsWorld2011 += countryData.maxMigrants2011;
            totalEmigrantsWorld2010 += countryData.maxMigrants2010;
            
            // get some derivated data (totals from all countries);
            if (countryData.maxMigrantsMale2011 > maxCountryMigrantsMale2011)
              maxCountryMigrantsMale2011 = countryData.maxMigrantsMale2011;
            if (countryData.maxMigrantsFemale2011 > maxCountryMigrantsFemale2011)
              maxCountryMigrantsFemale2011 = countryData.maxMigrantsFemale2011;
            if (countryData.maxMigrantsMale2010 > maxCountryMigrantsMale2010)
              maxCountryMigrantsMale2010 = countryData.maxMigrantsMale2010;
            if (countryData.maxMigrantsFemale2010 > maxCountryMigrantsFemale2010)
              maxCountryMigrantsFemale2010 = countryData.maxMigrantsFemale2010;
            
          } catch (Exception ex) 
          {
            System.out.println("Error al parsear lat/lng");
          }
        }
        lineCount++;
      }

      maxMigrantByGenderAndAge2011 = (maxCountryMigrantsMale2011 > maxCountryMigrantsFemale2011) ? maxCountryMigrantsMale2011 : maxCountryMigrantsFemale2011;
      maxMigrantByGenderAndAge2010 = (maxCountryMigrantsMale2010 > maxCountryMigrantsFemale2010) ? maxCountryMigrantsMale2010 : maxCountryMigrantsFemale2010;

      // sort countries by amount of migrants and get country with highest amount of migrants
      // (*a)trick... first countries are much higher than the rest. This leads
      // to a poor color difference. so do not pick the first country
      Collections.sort(countries, CountryData.CountryDataComparator2011);
      maxMigrants2011 = countries.get(4).maxMigrants2011;
      Collections.sort(countries, CountryData.CountryDataComparator2010);
      maxMigrants2010 = countries.get(4).maxMigrants2010;
      
    } catch (IOException e) 
    {
      e.printStackTrace();
    }

    font = loadFont("OpenSans-18.vlw");

    // locations for Spain
    Location startLocation = new Location(40.46366700000001, -3.749220);
    mymap = new UnfoldingMap(this, new MapBox.WorldLightProvider());
    mymap.setZoomRange(2, 4);
    mymap.zoomOut();
    mymap.panTo(startLocation);
    mymap.panBy(0, -200);
    mymap.setBackgroundColor(255);
    MapUtils.createDefaultEventDispatcher(this, mymap);

    // create markers
    SimpleBezierMarker simpleBezierMarker;
    for (i = 0; i < countries.size(); i++) 
    {
      Location endLocation = new Location(countries.get(i).lat, countries.get(i).lng);

      // decide color based on the migrations
      // why this line? read comment (*a)
      int m;
      if(selectedYear == YEAR_2011)
        m = (countries.get(i).maxMigrants2011 > maxMigrants2011) ? maxMigrants2011 : countries.get(i).maxMigrants2011;
      else
        m = (countries.get(i).maxMigrants2010 > maxMigrants2010) ? maxMigrants2010 : countries.get(i).maxMigrants2010;
      
      float amt = map(m, 0, (selectedYear == YEAR_2010)? maxMigrants2010:maxMigrants2011, 0, 1);

      // get proportion of the area of circle. Then get the radius (A = PI * r2)
      float circleArea = map((selectedYear == YEAR_2010)? countries.get(i).maxMigrants2010:countries.get(i).maxMigrants2011, 
                              0, 
                              (selectedYear == YEAR_2010)? maxMigrants2010:maxMigrants2011, 
                              circleAreaFrom, 
                              circleAreaTo);
      float radius = sqrt(circleArea / PI);
      
      simpleBezierMarker = new SimpleBezierMarker(startLocation, endLocation, countries.get(i), lerpColor(from, to, amt), radius, degrees((float)GeoUtils.getAngleBetween(endLocation, startLocation)), mymap);
      markers.add(simpleBezierMarker);
      markersInteractive.add(new LabeledMarker(endLocation, simpleBezierMarker,font, nfc(simpleBezierMarker.countryData.maxMigrants2011)));
    }

    mymap.addMarkers(markers);
    mymap.addMarkers(markersInteractive);
    
    //load images
    imgManIsotype = loadImage("man.png");
    imgWomanIsotype = loadImage("woman.png");
    
    //use font
    textFont(font);
    
    //autoselect country by default
    if(selectedCountryData == null)
    {
      selectedCountryData = countries.get(0);
      selectedLabeledMarker = (LabeledMarker) markersInteractive.get(0);
      selectedLabeledMarker.selectConnection(true);
    }
  }

  
  
  
  public void draw()
  {
    barGraphHeight = 70;
    barWidth = 5;

    background(colorBackground);
    stroke(0, 0);
    fill(240, 240, 240, 190);
    smooth();
    mymap.draw();

    //draw footer
    stroke(0, 0);
    fill(240, 240, 240, 190);
    rect(0, appHeight - FOOTER_HEIGHT, appWidth, FOOTER_HEIGHT);

    //draw data for Spain
    fill(greyTicks);
    textSize(SIZE_H1);
    text("Desde España...", 20, appHeight - FOOTER_HEIGHT);
    textSize(SIZE_BODY);
    text("Emigración por edades y CCAA (personas)", 20, appHeight - FOOTER_HEIGHT + 25);
    drawHeatMap();
    
    //draw header
    fill(255, 100);
    rect(10, 0, 615, 45);
    
    //draw data for countries bar chart
    fill(greyTicks);
    textSize(SIZE_H2);
    text("Principales receptores de inmigración proveniente de España (nacionales y no nacionales)", 20, 20);
    textSize(SIZE_BODY-1);
    fill(color(1, 133, 113));
    text("En destacado los países que copan el 50%", 20, 37);
    fill(greyTicks);
    text("del total de flujos migratorios", 242, 37);
    drawBarCountries();
    if (selectedCountryData != null) 
    {
      drawBarChart();
      drawPieChart();
      
      textSize(SIZE_H1);
      fill(greyTicks);
      text("...hacia " + selectedCountryData.name, appWidth - 400, appHeight - FOOTER_HEIGHT);
    }

     //draw buttons for years
    int buttonWidth = 80;
    int buttonHeight = 35;
    int buttonMargin = 2;
    int button1Xpos = appWidth - (buttonWidth*2) - 3;
    int button2Xpos = appWidth - (buttonWidth) -2;
    int buttonColor = color(255, 255, 255, 100);
    int selectedButtonColor = color(100, 200, 100, 200);
    
    textSize(SIZE_H1-5);
    fill((selectedYear == YEAR_2010)? selectedButtonColor : buttonColor);
    rect(button1Xpos, buttonMargin, buttonWidth, buttonHeight);
    fill(greyTicks);
    text(YEAR_2010, appWidth - (buttonWidth*2) + 12, buttonMargin + 27);
    
    fill((selectedYear == YEAR_2011)? selectedButtonColor : buttonColor);
    rect(button2Xpos, buttonMargin, buttonWidth, buttonHeight);
    fill(greyTicks);
    text(YEAR_2011, appWidth - (buttonWidth) + 12, buttonMargin + 27);
    
    //check for clicks on button 2010
    boolean yearChanged = false;
    if ((mouseX > button1Xpos) && (mouseX < button1Xpos + buttonWidth) && (mouseY > buttonMargin) && (mouseY < buttonMargin + buttonHeight)) 
    {
      if(mousePressed)
      {
        selectedYear = YEAR_2010;
        yearChanged = true;
      }
    }
    //check for clicks on button 2011
    if ((mouseX > button2Xpos) && (mouseX < button2Xpos + buttonWidth) && (mouseY > buttonMargin) && (mouseY < buttonMargin + buttonHeight)) 
    {
      if(mousePressed)
      {
        selectedYear = YEAR_2011;
        yearChanged = true;
      }
    }
    
    if(yearChanged == true|| firstDraw == true)
    {
      firstDraw = false;
      
      float circleArea;
      float radius;
      for (int i = 0; i < countries.size(); i++) 
      {
        //  get proportion of the area of circle. Then get the radius (A = PI * r2)
        circleArea = map((selectedYear == YEAR_2010)? countries.get(i).maxMigrants2010:countries.get(i).maxMigrants2011, 
                                0, 
                                (selectedYear == YEAR_2010)? maxMigrants2010:maxMigrants2011, 
                                circleAreaFrom, 
                                circleAreaTo);
        radius = sqrt(circleArea / PI);
        SimpleBezierMarker mi = (SimpleBezierMarker) markers.get(i);
        mi.radius = radius;
        
        //get new color
        int m;
        if(selectedYear == YEAR_2011)
          m = (countries.get(i).maxMigrants2011 > maxMigrants2011) ? maxMigrants2011 : countries.get(i).maxMigrants2011;
        else
          m = (countries.get(i).maxMigrants2010 > maxMigrants2010) ? maxMigrants2010 : countries.get(i).maxMigrants2010;
        
        float amt = map(m, 0, (selectedYear == YEAR_2010)? maxMigrants2010:maxMigrants2011, 0, 1);
        mi.changeColor(lerpColor(from, to, amt));
        
        //change data in tooltip
        LabeledMarker mi2 = (LabeledMarker) markersInteractive.get(i);
        mi2.totalFormatted = (selectedYear == YEAR_2010)? nfc(mi.countryData.maxMigrants2010) : nfc(mi.countryData.maxMigrants2011);
      }
    }
  }

  

  public void drawBarCountries()
  {
    int isActive = 0;
    int activeIndex = -1;
    int i;
    float barHeight;
    int accMigrants = 0;
    int colorBarCountry;
    int countryMaxMigrants;
    int totalEmigrantsWorld;

    if(selectedYear == YEAR_2011)
      Collections.sort(countries, CountryData.CountryDataComparator2011);
    else
      Collections.sort(countries, CountryData.CountryDataComparator2010);
    
    totalEmigrantsWorld = (selectedYear == YEAR_2010)? totalEmigrantsWorld2010 : totalEmigrantsWorld2011;
    for (i = 0; i < /* countries.size() */50; i++) 
    {
      countryMaxMigrants = (selectedYear == YEAR_2010)? countries.get(i).maxMigrants2010:countries.get(i).maxMigrants2011;
      if (countryMaxMigrants > 0) 
      {
        barHeight = map(countryMaxMigrants, 
                        0, 
                        (selectedYear == YEAR_2010)?  maxMigrants2010:maxMigrants2011, 
                        0, 
                        (float) barGraphHeigthCountry);
        
        accMigrants += countryMaxMigrants;
        
        if(accMigrants < totalEmigrantsWorld / 2)
          colorBarCountry = color(90, 180, 172); //color(216, 179, 101);
        else
          colorBarCountry = color(75);
        
        isActive = drawBar(originXCountryGraph, 
                          originYCountryGraph + i * (barWidthCountry + gapBarsCountry), 
                          barHeight, 
                          barWidthCountry,
                          countryMaxMigrants,
                          colorBarCountry,
                          countries.get(i));
      }
      if (isActive == 1) 
      {
        activeIndex = i;
      }
    }

    if (activeIndex > -1) 
    {
      barHeight = map((selectedYear == YEAR_2010)?  countries.get(activeIndex).maxMigrants2010 : countries.get(activeIndex).maxMigrants2011, 
                      0, 
                      (selectedYear == YEAR_2010)?  maxMigrants2010:maxMigrants2011, 
                      0, 
                      (float) barGraphHeigthCountry);
      fill(255, 190);
      stroke(33, 90);
      int nfcMaxMigrants =  (selectedYear == YEAR_2010)? countries.get(activeIndex).maxMigrants2010 : countries.get(activeIndex).maxMigrants2011;
      rect(mouseX, 
          originYCountryGraph + activeIndex * (barWidthCountry + gapBarsCountry) - font.getSize() + 2,
          textWidth(countries.get(activeIndex).name + " - " + nfc(nfcMaxMigrants) + " inmigrantes"), 
          font.getSize());
      textSize(10);
      fill(0, 0, 0);
      
      text(countries.get(activeIndex).name + " - " + nfc(nfcMaxMigrants) + " inmigrantes", 
          mouseX + 5,
          originYCountryGraph + activeIndex * (barWidthCountry + gapBarsCountry) - 2);
      
      if(mousePressed == true)
      {
        //select the country
        for (int x = 0; x < markersInteractive.size(); x++) 
        {
          LabeledMarker mi = (LabeledMarker) markersInteractive.get(x);
          if (mi.getCountryData().name == countries.get(activeIndex).name) 
          {
            if(selectedLabeledMarker != null)
            {
              selectedLabeledMarker.selectConnection(false);
            }
            selectedLabeledMarker = mi;
            mi.selectConnection(true);
            selectedCountryData = mi.getCountryData();
          }
        }
      }
    }
  }

  
  
  
  public void drawBarChart()
  {
    fill(33);
    textSize(SIZE_BODY);
    int maxM = (selectedYear == YEAR_2010)? selectedCountryData.maxMigrants2010 : selectedCountryData.maxMigrants2011;
    
    text(nfc(maxM) + " emigrantes de España hacia " + selectedCountryData.name + "\nSegún sexo y edad (18-65 años)",
        originX - 40, 
        appHeight - FOOTER_HEIGHT + 25);
    drawTicks( (selectedYear == YEAR_2010)? 
                selectedCountryData.migrants_male2010.size() * (barWidth + gapBars) : selectedCountryData.migrants_male2011.size() * (barWidth + gapBars)
              );
    drawLabels();
    drawMigrantByGender((selectedYear == YEAR_2010)? selectedCountryData.migrants_male2010:selectedCountryData.migrants_male2011, GENDER_MALE);
    drawMigrantByGender((selectedYear == YEAR_2010)? selectedCountryData.migrants_female2010:selectedCountryData.migrants_female2011, GENDER_FEMALE);
  }

  
  
  
  public void drawPieChart()
  {
    if(selectedYear == YEAR_2010)
    {
      if(selectedCountryData.maxMigrants2010 == 0)
        return;
    }
    else
    {
      if(selectedCountryData.maxMigrants2011 == 0)
        return;
    }
    
    float lastAngle = HALF_PI;
    //get total males, total females, calculate percentages, angles and draw
    int malePercentage;
    if(selectedYear == YEAR_2010)
      malePercentage = selectedCountryData.totalMigrantsMale2010*100/selectedCountryData.maxMigrants2010;
    else
      malePercentage = selectedCountryData.totalMigrantsMale2011*100/selectedCountryData.maxMigrants2011;
    
    int femalePercentage = 100 - malePercentage;
    
    
    //map percentages onto angles (only from 0 to 180, we draw the half of a pie chart)
    int[] angles = { (int)map(femalePercentage, 0, 100, 0, 180), 
                      (int)map(malePercentage, 0, 100, 0, 180)
                   };
    int []fills = {colorBarSelected, colorBar};
    for (int i = 0; i < angles.length; i++)
    {
      fill(fills[i]);
      arc((float)(originX - 35), (float)(originY), 40, 40, lastAngle, lastAngle + radians(angles[i]));
      lastAngle = lastAngle + radians(angles[i]);
    }
  }
  
  
  
  
  public void drawTicks(int barchartWidth)
  {
    float tickYmapped;

    strokeWeight(1);
    stroke(greyTicks, greyTicks, greyTicks, 35);
    fill(greyTicks);
    textSize(11);

    // draw ticks for male migrants
    line(originX - 10, originY - gapVerticalAxis, originX - 2, originY
        - gapVerticalAxis);

    tickYmapped = map(
        (float) selectedCountryData.getMaxMigrantsByAgeAndGender(selectedYear) / 2, 0,
        (float) selectedCountryData.getMaxMigrantsByAgeAndGender(selectedYear), 0,
        (float) barGraphHeight);
    line(originX - 10, originY - gapVerticalAxis - tickYmapped, originX
        + barchartWidth, originY - gapVerticalAxis - tickYmapped);
    text(nfc(selectedCountryData.getMaxMigrantsByAgeAndGender(selectedYear) / 2),
        originX - 10 - 30, originY - gapVerticalAxis - tickYmapped + 3);

    tickYmapped = map(
        (float) selectedCountryData.getMaxMigrantsByAgeAndGender(selectedYear), 0,
        (float) selectedCountryData.getMaxMigrantsByAgeAndGender(selectedYear), 0,
        (float) barGraphHeight);
    line(originX - 10, originY - gapVerticalAxis - tickYmapped, originX
        + barchartWidth, originY - gapVerticalAxis - tickYmapped);
    text(nfc(selectedCountryData.getMaxMigrantsByAgeAndGender(selectedYear)), originX - 10 - 30,
        originY - gapVerticalAxis - tickYmapped + 3);

    // draw ticks for female migrants
    line(originX - 10, originY + gapVerticalAxis, originX - 2, originY
        + gapVerticalAxis);

    tickYmapped = map(
        (float) selectedCountryData.getMaxMigrantsByAgeAndGender(selectedYear) / 2, 0,
        (float) selectedCountryData.getMaxMigrantsByAgeAndGender(selectedYear), 0,
        (float) barGraphHeight);
    line(originX - 10, originY + gapVerticalAxis + tickYmapped, originX
        + barchartWidth, originY + gapVerticalAxis + tickYmapped);
    text(nfc(selectedCountryData.getMaxMigrantsByAgeAndGender(selectedYear) / 2),
        originX - 10 - 30, originY + gapVerticalAxis + tickYmapped + 3);

    tickYmapped = map(
        (float) selectedCountryData.getMaxMigrantsByAgeAndGender(selectedYear), 0,
        (float) selectedCountryData.getMaxMigrantsByAgeAndGender(selectedYear), 0,
        (float) barGraphHeight);
    line(originX - 10, originY + gapVerticalAxis + tickYmapped, originX
        + barchartWidth, originY + gapVerticalAxis + tickYmapped);
    text(nfc(selectedCountryData.getMaxMigrantsByAgeAndGender(selectedYear)), originX - 10 - 30,
        originY + gapVerticalAxis + tickYmapped + 3);
  }

  
  
  
  public void drawLabels()
  {
    textSize(9);
    for (int i = AGE_MIN; i <= AGE_MAX; i++) 
    {
      if (i % 5 == 0/* || i == 18 */)
        text(i, originX - 3 + (i - AGE_MIN) * (barWidth + gapBars), originY + 3);
    }
  }

  
  
  
  public int drawBar(float x, float y, float width, float height, float value, int color,CountryData countryData)
  {
    int isActive = 0;

    if ((mouseX > x) && (mouseX < x + width) && (mouseY > y) && (mouseY < y + height)) 
    {
      fill(colorBarOver);
      isActive = 1;
    }
    else if (countryData != null && countryData == selectedCountryData) 
    {
      fill(color(255, 194, 104));
    }
    else
      fill(color);

    stroke(0, 0);
    rect(x, y, width, (height == 0) ? 1 : height);
    return isActive;
  }

  
  
  
  
  protected void drawMigrantByGender(List<Double> migrants, int gender)
  {
    int isActive = 0;
    int activeIndex = -1;
    int i;
    float barHeight;
    
    //get the median
    List<Double> newList = new ArrayList<Double>();
    for(int d=0; d<migrants.size(); d++)
    {
      if(migrants.get(d) > 0)
        newList.add(migrants.get(d));
    }
    if(newList.size() > 0)
    {
      Collections.sort(newList);
      double medianValue = newList.get((1+newList.size()/2)-1);
      
      //find the position of the median value
      for(i = 0; i < migrants.size(); i++)
      {
        if(migrants.get(i) == medianValue)
        {
          drawBar( originX + i * (barWidth + gapBars), 
              (gender == GENDER_MALE) ? originY - gapVerticalAxis - barGraphHeight-10 : originY + gapVerticalAxis, 
              1, 
              barGraphHeight+10, 
              migrants.get(i).floatValue(),
              color(153, 250),
              null);
          textSize(SIZE_SMALL);
          fill(153, 250);
          text( "mediana: " + nfs((float)medianValue, 2, 0) + " emigrantes", 
                originX + 5 + i * (barWidth + gapBars), 
                (gender == GENDER_MALE) ? originY - gapVerticalAxis - barGraphHeight - 5 : originY + gapVerticalAxis + barGraphHeight+12);
          break;
        }
      }
    }
    
    
    
    for (i = 0; i < migrants.size(); i++) 
    {
      // Morocco has the proportionally highest from all the countries. Too high to display all countries based by maxMigrantByGenderAndAge
      // barHeight = map(migrants.get(i).floatValue(), 0, maxMigrantByGenderAndAge, 0, (float)barGraphHeight);
      barHeight = map(migrants.get(i).floatValue(), 0, (float) selectedCountryData.getMaxMigrantsByAgeAndGender(selectedYear), 0, (float) barGraphHeight);
      isActive = drawBar( originX + i * (barWidth + gapBars), 
                          (gender == GENDER_MALE) ? originY - gapVerticalAxis - barHeight : originY + gapVerticalAxis, 
                          barWidth, barHeight, 
                          migrants.get(i).floatValue(),
                          (gender == GENDER_MALE) ? colorBar:colorBarFemale,
                          null);

      if (isActive == 1) 
        activeIndex = i;
    }

    //rollovers
    if (activeIndex > -1) 
    {
      barHeight = map(migrants.get(activeIndex).floatValue(), 
                      0, 
                      (float) selectedCountryData.getMaxMigrantsByAgeAndGender(selectedYear), 0, (float) barGraphHeight);
      textSize(12);
      fill(0, 0, 0);
      text(nfc(migrants.get(activeIndex).intValue()), originX + activeIndex * (barWidth + gapBars) - 2, (gender == GENDER_MALE) ? originY - gapVerticalAxis - barHeight - 7 : originY + gapVerticalAxis + barHeight + 14);
    }
    
    
    if(gender == GENDER_MALE)
      image(imgManIsotype, originX - 30, originY - gapVerticalAxis - imgManIsotype.height);
    else
      image(imgWomanIsotype, originX - 30, originY + gapVerticalAxis);
  }

  
  
  protected void drawHeatMap()
  {
    int ca=0;
    for(ca=0; ca<CCAA.size(); ca++)
    {
      for(int age=0; age<CCAA.get(0).emigrants2011.size(); age++)
      {
        //draw all the cells
        drawHeatMapCell(heatMapOriginX + age * (heatMapCellWidth + heatMapGap), 
                        heatMapOriginY + ca *(heatMapCellHeight + heatMapGap),
                        heatMapCellWidth,
                        heatMapCellHeight,                        
                        //lerp color does not generate enough difference between colors
                        //lerpColor(heatMapCellColorFrom, heatMapCellColorTo, map(CCAA.get(ca).emigrants2011.get(age).floatValue(), (float)minEmigrantCA, (float)maxEmigrantCA, 0, 1))
                        lerpColor(heatMapCellColorFrom, 
                                  heatMapCellColorTo, 
                                  map((selectedYear==YEAR_2010)?  CCAA.get(ca).emigrants2010.get(age).floatValue() : CCAA.get(ca).emigrants2011.get(age).floatValue(), 
                                      (selectedYear==YEAR_2010)?  CCAA.get(ca).minMigrants2010 : CCAA.get(ca).minMigrants2011, 
                                      (selectedYear==YEAR_2010)?  CCAA.get(ca).maxMigrants2010 : CCAA.get(ca).maxMigrants2011, 
                                      0, 
                                      1))
                        //use a gradient instead?
                        //heatMapCellColors[(int)map(CCAA.get(ca).emigrants2011.get(age).floatValue(), (float)minEmigrantCA, (float)maxEmigrantCA, 0, heatMapCellColors.length-1)]
                        );
      }
    }
    //draw labels for ages and CCAA
    drawHeatMapLabelsVAxis();
    drawHeatMapLabelsHAxis();
    
    //draw totals for each ca
    textSize(9);
    for(ca=0; ca<CCAA.size(); ca++)
    {
      drawHeatMapCell(heatMapOriginX + CCAA.get(ca).emigrants2011.size()* (heatMapCellWidth + heatMapGap) + 37, 
                      heatMapOriginY + ca *(heatMapCellHeight + heatMapGap), 
                      map((selectedYear==YEAR_2010)?  CCAA.get(ca).totalMigrants2010 : CCAA.get(ca).totalMigrants2011, 
                          (selectedYear==YEAR_2010)?  (float)minEmigrantCAacc2010 : (float)minEmigrantCAacc2011, 
                          (selectedYear==YEAR_2010)?  (float)maxEmigrantCAacc2010 : (float)maxEmigrantCAacc2011, 
                          0, 
                          70), 
                      heatMapCellHeight,
                      color(136));
      fill(greyTicks);
      int totalMigrantsYear = (selectedYear==YEAR_2010)?  CCAA.get(ca).totalMigrants2010 : CCAA.get(ca).totalMigrants2011;
      text( (selectedYear==YEAR_2010)?  nfc(CCAA.get(ca).totalMigrants2010) : nfc(CCAA.get(ca).totalMigrants2011), 
            heatMapOriginX + CCAA.get(ca).emigrants2011.size()* (heatMapCellWidth + heatMapGap) + 10 + heatMapCellWidth*5 - textWidth(Integer.toString(totalMigrantsYear)),
            heatMapOriginY + ca *(heatMapCellHeight + heatMapGap) + 7
          );
    }
    
    //draw total for spain
    int totalMigrantsSpainYear = (selectedYear==YEAR_2010)? totalEmigrantsSpain2010 : totalEmigrantsSpain2011; 
    textSize(14);
    text(nfc(totalMigrantsSpainYear), 
        heatMapOriginX + CCAA.get(0).emigrants2011.size()* (heatMapCellWidth + heatMapGap) + 40 + heatMapCellWidth*5 - textWidth(Integer.toString(totalMigrantsSpainYear)) + 2,
        heatMapOriginY + CCAA.size() *(heatMapCellHeight + heatMapGap) + 15);
    text("Total de emigración exterior:", 
        heatMapOriginX + CCAA.get(0).emigrants2011.size()* (heatMapCellWidth + heatMapGap) + heatMapCellWidth*5 - textWidth("Total de emigración exterior:")-5,
        heatMapOriginY + CCAA.size() *(heatMapCellHeight + heatMapGap) + 15);
  }
  
  
  
  
  public void drawHeatMapCell(float x, float y, float width, float height, int color)
  {
    fill(color);
    stroke(0, 0);
    rect(x, y, width, height);
  }
  
  public void drawHeatMapLabelsVAxis()
  {
    fill(greyTicks);
    textSize(9);
    for (int i = AGE_MIN; i <= AGE_MAX; i++) 
    {
      if (i % 5 == 0/* || i == 18 */)
        text(i, heatMapOriginX + (i-AGE_MIN) * (heatMapCellWidth + heatMapGap) - 2, heatMapOriginY - 5);
    }
  }
  
  public void drawHeatMapLabelsHAxis()
  {
    fill(greyTicks);
    textSize(9);
    for(int i = 0; i < CCAA.size(); i++)
    {
      text( trim(CCAA.get(i).name), 
            heatMapOriginX - textWidth(CCAA.get(i).name), 
            heatMapOriginY + i *(heatMapCellHeight + heatMapGap) + 5
            );
    }
  }
  
  

  public void mouseMoved()
  {
    for (int i = 0; i < markersInteractive.size(); i++) 
    {
      LabeledMarker mi = (LabeledMarker) markersInteractive.get(i);
      boolean isInside = mi.isInside(mymap, mouseX, mouseY);
      if(isInside == false && mi == selectedLabeledMarker)
      {
        //do not deselect because of a rollout
      }
      else
      {
        mi.selectConnection(isInside);
      }
    }
  }

  public void mousePressed()
  {
    for (int i = 0; i < markersInteractive.size(); i++) 
    {
      LabeledMarker mi = (LabeledMarker) markersInteractive.get(i);
      if (mi.isInside(mymap, mouseX, mouseY)) 
      {
        if(selectedLabeledMarker != null)
        {
          selectedLabeledMarker.selectConnection(false);
        }
        selectedLabeledMarker = mi;
        mi.selectConnection(true);
        selectedCountryData = mi.getCountryData();
      }
    }
  }
}
