// +-------------------------------------------------------------+
// | Class that contains all weather information for the program |
// | By Bradley He                                               |
// +-------------------------------------------------------------+

import java.net.*;
import java.io.*;
import com.google.gson.*;
import javax.swing.ImageIcon;

public class WeatherInfo
{
  private String inputName, apiKey = "insert api key here", currentConditions, observationTime, cityName, windDir, humidity, feelsLikeF, feelsLikeC, radar;
  private String[] fiveDayConditions, fiveDayName;
  private int[] fiveDayChance, highLowF, highLowC;
  private int[][] fiveDayTempF, fiveDayTempC;
  private double windSpeed, currentTempF, currentTempC, precip;
  private ImageIcon currentIcon;
  private ImageIcon[] fiveDayIcons;
  private JsonObject observation, forecast;
  
  // WeatherInfo method
  // Constructor for the WeatherInfo class
  public WeatherInfo()
  {
    inputName = "";
    currentConditions = "";
    observationTime = "";
    cityName = "";
    windDir = "";
    humidity = "";
    feelsLikeF = "";
    feelsLikeC = "";
    currentTempF = 0;
    currentTempC = 0;
    windSpeed = 0;
    currentIcon = new ImageIcon();
    fiveDayIcons = new ImageIcon[5];
    fiveDayTempF = new int[5][2];
    fiveDayTempC = new int[5][2];
    highLowF = new int[2];
    highLowC =  new int[2];
    fiveDayConditions = new String[5];
    fiveDayName = new String[5];
    fiveDayChance = new int[5];
  }
  
  // setInputName method
  // Sets a value to all of the variables in the weatherInfo class based on a string inputted as a parameter
  public void setInputName(String i) throws Exception
  {
    // Setting up connection to weather api
    inputName = i;
    URL url = new URL("http://api.wunderground.com/api/" + apiKey + "/conditions/q/" + inputName + ".json");
    URLConnection c = url.openConnection();
    InputStream is = c.getInputStream();
    InputStreamReader isr = new InputStreamReader(is, "UTF-8");

    // Putting all of the data into a JsonObject
    observation = ((JsonObject)new JsonParser().parse(isr)).getAsJsonObject("current_observation"); 
    
    // Setting all of the variables based upon data from the observation JsonObject
    observationTime = observation.get("observation_time").getAsString();
    currentTempF = observation.get("temp_f").getAsDouble();
    currentTempC = observation.get("temp_c").getAsDouble();
    feelsLikeF = observation.get("feelslike_f").getAsString();
    feelsLikeC = observation.get("feelslike_c").getAsString();
    humidity = observation.get("relative_humidity").getAsString();
    windDir = observation.get("wind_dir").getAsString();
    windSpeed = observation.get("wind_mph").getAsDouble();
    currentConditions = observation.get("weather").getAsString();
    cityName = observation.getAsJsonObject("display_location").get("full").getAsString();
    currentIcon = new ImageIcon(new URL(observation.get("icon_url").getAsString()));
    
    // Making a new url and resetting other variables needed to get data
    URL aurl = new URL("http://api.wunderground.com/api/" + apiKey + "/forecast10day/q/" + inputName + ".json");
    c = aurl.openConnection();
    is = c.getInputStream();
    isr = new InputStreamReader(is, "UTF-8");
    
    // Thic time, we create a JsonArray in order to obtain the data from the api
    forecast = (JsonObject)new JsonParser().parse(isr);
    JsonArray array = forecast.getAsJsonObject("forecast").getAsJsonObject("simpleforecast").getAsJsonArray("forecastday");
    
    // Using a for loop here because we need to process an array of data
    for(int x = 0; x < 5; x++)
    {
      // Getting the data from the JsonArray. Note here that we use x+1 as the index in order to skip day 1, which would be today.
      fiveDayConditions[x] = array.get(x + 1).getAsJsonObject().get("conditions").getAsString();
      fiveDayTempF[x][0] = array.get(x + 1).getAsJsonObject().getAsJsonObject("high").get("fahrenheit").getAsInt();
      fiveDayTempF[x][1] = array.get(x + 1).getAsJsonObject().getAsJsonObject("low").get("fahrenheit").getAsInt();
      fiveDayTempC[x][0] = array.get(x + 1).getAsJsonObject().getAsJsonObject("high").get("celsius").getAsInt();
      fiveDayTempC[x][1] = array.get(x + 1).getAsJsonObject().getAsJsonObject("low").get("celsius").getAsInt();
      fiveDayChance[x] = array.get(x + 1).getAsJsonObject().get("pop").getAsInt();
      
      // Getting the date from the JsonArray
      String name = array.get(x + 1).getAsJsonObject().getAsJsonObject("date").get("weekday").getAsString();
      String month = array.get(x + 1).getAsJsonObject().getAsJsonObject("date").get("month").getAsString();
      String day = array.get(x + 1).getAsJsonObject().getAsJsonObject("date").get("day").getAsString();
      fiveDayName[x] = name + " " + month + "/" + day;
      
      // Getting the image associated with the conditions of the forecasted day
      fiveDayIcons[x] = new ImageIcon(new URL(array.get(x + 1).getAsJsonObject().get("icon_url").getAsString()));
    }
    
    highLowF[0] = array.get(0).getAsJsonObject().getAsJsonObject("high").get("fahrenheit").getAsInt();
    highLowF[1] = array.get(0).getAsJsonObject().getAsJsonObject("low").get("fahrenheit").getAsInt();
    highLowC[0] = array.get(0).getAsJsonObject().getAsJsonObject("high").get("celsius").getAsInt();
    highLowC[1] = array.get(0).getAsJsonObject().getAsJsonObject("low").get("celsius").getAsInt();
    precip = array.get(0).getAsJsonObject().getAsJsonObject("qpf_allday").get("in").getAsDouble();
  }
  
  // getRadar method
  // Gets a radar image based upon the city location
  public String getRadar()
  {
    // Gets the latitude and longitude from the observation JsonObject
    double latitude = observation.getAsJsonObject("observation_location").get("latitude").getAsDouble();
    double longitude = observation.getAsJsonObject("observation_location").get("longitude").getAsDouble();
    
    // Setting radar according to latitude and longitude
    radar = "http://api.wunderground.com/api/" + apiKey + "/animatedradar/q/" + inputName + ".gif?centerlat=" + latitude + "&centerlon=" + longitude 
      + "&radius=30&width=320&height=300&timelabel=1&timelabel.y=15&delay=15&newmaps=1&num=10";
    
    return radar;
  }
  
  // search method
  // Will call setInputName based on a string inputted as a parameter
  public void search(String i) throws Exception
  {
    // Replace every space with the respective url equivalent
    i = i.replace(" ", "%20");
    
    // Create a URL that contains the search query and set everything up
    URL url = new URL("http://autocomplete.wunderground.com/aq?query=" + i);
    URLConnection c = url.openConnection();
    InputStream is = c.getInputStream();
    InputStreamReader isr = new InputStreamReader(is, "UTF-8");
    
    // Make a JsonArray based on the output from the api
    JsonArray array = ((JsonObject)new JsonParser().parse(isr)).getAsJsonObject().getAsJsonArray("RESULTS"); 
    boolean exit = false;
    int x = 0;
    
    // This loop will run through the entire array until it gets a valid city or it throws an OutOfBoundsException. 
    while(!exit)
    {
      // If the element of the array is a city,
      if(array.get(x).getAsJsonObject().get("type").getAsString().equals("city"))
      {
        // then we call setInputName to reconfigure the entire WeatherInfo class based on the city.
        setInputName(array.get(x).getAsJsonObject().get("l").getAsString());
        exit = true;
      }
 
      x++;
    }
  }
  
  // Get methods for every single instance variable
  public String getInputName()
  {
    return inputName;
  }
  
  public String getFeelsLikeF()
  {
    return feelsLikeF;
  }
  
  public String getFeelsLikeC()
  {
    return feelsLikeC;
  }
  
  public int[] getHighLowF()
  {
    return highLowF;
  }
  
  public int[] getHighLowC()
  {
    return highLowC;
  }
  
  public ImageIcon getCurrentIcon()
  {
    return currentIcon;
  }
  
  public String getHumidity()
  {
    return humidity;
  }
  
  public String getObservationTime()
  {
    return observationTime;
  }
  
  public String getCurrentConditions()
  {
    return currentConditions;
  }
  
  public double getCurrentTempF()
  {
    return currentTempF;
  }
  
  public double getCurrentTempC()
  {
    return currentTempC;
  }
  
  public double getWindSpeed()
  {
    return windSpeed;
  }
  
  public double getPrecip()
  {
    return precip;
  }
  
  public String getWindDir()
  {
    return windDir;
  }
  
  public String getCityName()
  {
    return cityName;
  }
  
  public String[] getFiveDayConditions()
  {
    return fiveDayConditions;
  }
  
  public String[] getFiveDayName()
  {
    return fiveDayName;
  }
  
  public int[][] getFiveDayTempF()
  {
    return fiveDayTempF;
  }
  
  public int[][] getFiveDayTempC()
  {
    return fiveDayTempC;
  }
  
  public ImageIcon[] getFiveDayIcons()
  {
    return fiveDayIcons;
  }
  
  public int[] getFiveDayChance()
  {
    return fiveDayChance;
  }
}
