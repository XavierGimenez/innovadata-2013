
import java.util.HashMap;
import java.util.List;

import processing.core.PApplet;
import processing.xml.XMLElement;
import codeanticode.glgraphics.GLConstants;
import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.utils.MapUtils;

/**
 * Visualizes population density of the world as a choropleth map. Countries are shaded in proportion to the population
 * density.
 * 
 * It loads the country shapes from a GeoJSON file via a data reader, and loads the population density values from
 * another CSV file (provided by the World Bank). The data value is encoded to transparency via a simplistic linear
 * mapping.
 */
public class ChoroplethMapApp extends PApplet {

	UnfoldingMap map;

	HashMap<String, DataEntry> dataEntriesMap;
	List<Marker> countryMarkers;

	public void setup() {
		size(800, 600, GLConstants.GLGRAPHICS);
		smooth();

		map = new UnfoldingMap(this, 50, 50, 700, 500);
		map.zoomToLevel(2);
		map.setBackgroundColor(240);
		MapUtils.createDefaultEventDispatcher(this, map);

		// Load country polygons and adds them as markers
		List<Feature> countries = GeoJSONReader.loadData(this, "countries.geo.json");
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		map.addMarkers(countryMarkers);

		// Load population data
		dataEntriesMap = loadPopulationDensityFromCSV("countries-population-density.csv");
		println("Loaded " + dataEntriesMap.size() + " data entries");

		// Country markers are shaded according to its population density (only once)
		shadeCountries();
	}

	public void draw() {
		background(240);

		// Draw map tiles and country markers
		map.draw();
	}

	public void shadeCountries() {
		for (Marker marker : countryMarkers) {
			// Find data for country of the current marker
			String countryId = marker.getId();
			DataEntry dataEntry = dataEntriesMap.get(countryId);

			if (dataEntry != null && dataEntry.value != null) {
				// Encode value as brightness (values range: 0-1000)
				float transparency = map(dataEntry.value, 0, 700, 10, 255);
				marker.setColor(color(255, 0, 0, transparency));
			} else {
				// No value available
				marker.setColor(color(100, 120));
			}
		}
	}

	public HashMap<String, DataEntry> loadPopulationDensityFromCSV(String fileName) {
		HashMap<String, DataEntry> dataEntriesMap = new HashMap<String, DataEntry>();

		String[] rows = loadStrings(fileName);
		for (String row : rows) {
			// Reads country name and population density value from CSV row
			String[] columns = row.split(";");
			if (columns.length >= 3) {
				DataEntry dataEntry = new DataEntry();
				dataEntry.countryName = columns[0];
				dataEntry.id = columns[1];
				dataEntry.value = Float.parseFloat(columns[2]);
				dataEntriesMap.put(dataEntry.id, dataEntry);
			}
		}

		return dataEntriesMap;
	}

	class DataEntry {
		String countryName;
		String id;
		Integer year;
		Float value;
	}

	// ------------------------------------------

	/**
	 * Test loading method to load from original XML file from WorldBank.
	 */
	private HashMap<String, DataEntry> loadPopulationDensityFromXML(String fileName) {
		HashMap<String, DataEntry> dataEntriesMap = new HashMap<String, DataEntry>();

		// Get all records
		XMLElement rss = new XMLElement(this, fileName);
		XMLElement[] records = rss.getChildren("data/record");
		for (int i = 0; i < records.length; i++) {
			DataEntry dataEntry = new DataEntry();

			XMLElement[] fields = records[i].getChildren("field");
			for (int j = 0; j < fields.length; j++) {
				XMLElement field = fields[j];
				String fieldName = field.getString("name");

				if (fieldName.equals("Country or Area")) {
					dataEntry.countryName = field.getContent();
				} else if (fieldName.equals("Year")) {
					dataEntry.year = Integer.parseInt(field.getContent());

				} else if (fieldName.equals("Value")) {
					String valueStr = field.getContent();
					if (valueStr != null) {
						dataEntry.value = Float.parseFloat(valueStr);
					}
				}
			}

			if (dataEntry.year == 2010) {
				dataEntriesMap.put(dataEntry.countryName, dataEntry);
			}
		}
		return dataEntriesMap;
	}

}
