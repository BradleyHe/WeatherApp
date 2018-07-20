// +---------------------------------+
// | Panel class for the weather app |
// | By Bradley He                   |
// +---------------------------------+

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.*;

public class WeatherAppPanel extends JPanel
{
  private JTextField searchBar;
  private WeatherInfo weatherInfo;
  private JPanel[] fiveDayForecast;
  private JPanel topPanel, bottomPanel, centerPanel;
  private JButton tempF, tempC, search, refresh;
  private JLabel topLabel;
  private boolean isF;
  private Font font;
  
  // WeatherAppPanel method
  // Constructor for the WeatherAppPanel
  public WeatherAppPanel(WeatherInfo info)
  {
    // Initializing every private value and setting up things like buttons, font, dimensions, etc.
    isF = true;
    font = new Font("LiberationSerif", Font.BOLD, 15);
    weatherInfo = info;
    searchBar = new JTextField(10);
    searchBar.setFont(font);
    fiveDayForecast = new JPanel[5];
    
    topLabel = new JLabel("Enter a location", SwingConstants.CENTER);
    topLabel.setVerticalAlignment(SwingConstants.CENTER);
    topLabel.setFont(font);
    topPanel = new JPanel(new GridLayout(2, 1));
    topPanel.setBackground(Color.WHITE);
    
    bottomPanel = new JPanel();
    bottomPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
    bottomPanel.setBackground(Color.WHITE);
    
    tempF = new JButton("F");
    tempC = new JButton("C");
    search = new JButton("Search");
    refresh = new JButton("Refresh");
    tempF.setEnabled(false);
    refresh.setEnabled(false);
    tempF.addActionListener(new ButtonListener());
    tempC.addActionListener(new ButtonListener());
    search.addActionListener(new ButtonListener());
    refresh.addActionListener(new ButtonListener());
    
    // Adding search bar and buttons to the search panel
    JPanel searchPanel = new JPanel();
    searchPanel.add(searchBar);
    searchPanel.add(search);
    searchPanel.add(tempF);
    searchPanel.add(tempC);
    searchPanel.add(refresh);
    searchPanel.setBackground(Color.WHITE);
    
    topPanel.add(searchPanel);
    topPanel.add(topLabel);
    
    centerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
    centerPanel.setBackground(Color.WHITE);
    
    // Setting up five day forecast panels
    for(int x = 0; x < 5; x++)
    {
      fiveDayForecast[x] = new JPanel();
      fiveDayForecast[x].setLayout(new GridLayout(5, 1));
      fiveDayForecast[x].setPreferredSize(new Dimension(160, 220));
    }
    
    setLayout(new BorderLayout(5, 5));
    setPreferredSize(new Dimension(1000, 620));
    add(topPanel, BorderLayout.NORTH);
    add(bottomPanel, BorderLayout.SOUTH);
    add(centerPanel, BorderLayout.CENTER);
    setBackground(Color.WHITE);
  }
  
  // update method
  // Places all information related to weather in the panel. Also can update information in the panel
  public void update()
  {
    // Setting up the center panel. First, we clear the entire panel so that we can rebuild it from new.
    centerPanel.removeAll();
    
    // Create leftPanel, which is necessary in order to arrange the JLabel in a GridLayout.
    JPanel leftPanel = new JPanel(new GridLayout(4, 1));
  
    // Create all JLabels that will be placed into the leftPanel
    JLabel cityName = new JLabel(weatherInfo.getCityName(), SwingConstants.CENTER);
    JLabel time = new JLabel(weatherInfo.getObservationTime(), SwingConstants.CENTER);
    JLabel conditions = new JLabel(weatherInfo.getCurrentConditions(), SwingConstants.CENTER);
    JLabel icon = new JLabel(weatherInfo.getCurrentIcon(), SwingConstants.CENTER);
    
    cityName.setVerticalAlignment(SwingConstants.CENTER);
    icon.setVerticalAlignment(SwingConstants.CENTER);
    conditions.setVerticalAlignment(SwingConstants.CENTER);
    
    cityName.setFont(new Font("LiberationSerif", Font.BOLD, 20));
    time.setFont(font);
    conditions.setFont(new Font("LiberationSerif", Font.BOLD, 18));
    
    // Adding JLabels to leftPanel
    leftPanel.add(cityName);
    leftPanel.add(time);
    leftPanel.add(conditions);
    leftPanel.add(icon);
    leftPanel.setBackground(Color.WHITE);
    
    centerPanel.add(leftPanel);
    
    // Getting statistical information
    String windDir = weatherInfo.getWindDir();
    String humidity = weatherInfo.getHumidity();
    double windSpeed = weatherInfo.getWindSpeed();
    double precip = weatherInfo.getPrecip();
    
    // Setting the temperature label up based on the unit of measure for temperature.
    if(isF)
    {
      // Get the temperature from the respective temperature get and set methods.
      String feelsLike = weatherInfo.getFeelsLikeF();
      double currentTemp = weatherInfo.getCurrentTempF();
      
      // Creating the temp JLabel, which contains all statistical information
      JLabel temp = new JLabel("<html>Conditions:<br>-----------------------------------<br>Current Temperature: " + currentTemp + (char)176 + "F<br>High: " + weatherInfo.getHighLowF()[0] + (char)176 + "F<br>Low: " 
                                 + weatherInfo.getHighLowF()[1] + (char)176 + "F<br>Feels Like: " + feelsLike + (char)176 + "F<br><br>Wind: From the " + windDir + " at " + windSpeed + " MPH"
                                 + "<br>Humidity: " + humidity + "<br>Precipitation: " + precip + " in</html>");
      temp.setVerticalAlignment(SwingConstants.CENTER);
      temp.setFont(font);

      centerPanel.add(temp);
    }
    
    // This is essentially the same thing except that we use celsius values instead of fahrenheit values
    else
    {
      String feelsLike = weatherInfo.getFeelsLikeC();
      double currentTemp = weatherInfo.getCurrentTempC();
      
      JLabel temp = new JLabel("<html>Conditions:<br>-----------------------------------<br>Current Temperature: " + currentTemp + (char)176 + "C<br>High: " + weatherInfo.getHighLowC()[0] + (char)176 + "C<br>Low: " 
                                 + weatherInfo.getHighLowC()[1] + (char)176 + "C<br>Feels Like: " + feelsLike + (char)176 + "C<br><br>Wind: From the " + windDir + " at " + windSpeed + " MPH"
                                 + "<br>Humidity: " + humidity + "</html>", SwingConstants.CENTER);
      temp.setVerticalAlignment(SwingConstants.CENTER);
      temp.setFont(font);
      
      centerPanel.add(temp);
    }        
    
    try
    {
      // Get the radar image from the weatherInfo class and add it to centerPanel
      ImageIcon radar = new ImageIcon(new URL(weatherInfo.getRadar()));
      centerPanel.add(new JLabel(radar));
    }
    
    catch(Exception e)
    {
      e.printStackTrace();
    }
    
    // Validate the panel so that everything updates
    centerPanel.validate();
      
    // Setting up the bottom panel, or 5 day forecast. Each element of the fiveDayForecast array represents a panel that will be put into the bottom of the page
    for(int x = 0; x < 5; x++)
    { 
      // Creating all of the JLabels unrelated to temperature
      JLabel name = new JLabel(weatherInfo.getFiveDayName()[x], SwingConstants.CENTER);
      name.setVerticalAlignment(SwingConstants.CENTER);
      name.setFont(font);
      
      JLabel fconditions = new JLabel("<html><center>" + weatherInfo.getFiveDayConditions()[x] + "</html>", SwingConstants.CENTER);
      fconditions.setVerticalAlignment(SwingConstants.CENTER);
      fconditions.setFont(font);  
      
      JLabel chance = new JLabel(weatherInfo.getFiveDayChance()[x] + "%", new ImageIcon("raindrop.png"), SwingConstants.CENTER);
      chance.setVerticalAlignment(SwingConstants.CENTER);
      chance.setFont(font);

      // Adding the first few JLabels to the panel
      fiveDayForecast[x].removeAll();
      fiveDayForecast[x].add(name);
      fiveDayForecast[x].add(new JLabel(weatherInfo.getFiveDayIcons()[x]));
      fiveDayForecast[x].add(fconditions);
      
      // Again, the temperature JLabels are separated based upon the unit of temperature
      if(isF)
      {
        JLabel temp = new JLabel("<html>High: " + weatherInfo.getFiveDayTempF()[x][0] + (char)176 + "F<br>Low: " + weatherInfo.getFiveDayTempF()[x][1] + (char)176 + "F</html>", SwingConstants.CENTER);
        temp.setVerticalAlignment(SwingConstants.CENTER);
        temp.setFont(font); 

        fiveDayForecast[x].add(temp);
      }
      
      else
      {
        JLabel temp = new JLabel("<html>High: " + weatherInfo.getFiveDayTempC()[x][0] + (char)176 + "C<br>Low: " + weatherInfo.getFiveDayTempC()[x][1] + (char)176 + "C</html>", SwingConstants.CENTER);
        temp.setVerticalAlignment(SwingConstants.CENTER);
        temp.setFont(font); 
        
        fiveDayForecast[x].add(temp);
      }
      
      fiveDayForecast[x].add(chance);
      fiveDayForecast[x].setBorder(BorderFactory.createLineBorder(Color.BLACK));
      fiveDayForecast[x].setBackground(Color.WHITE);
      fiveDayForecast[x].validate();
    }
    
    // Add all of the JPanels in fiveDayForecast to the bottomPanel
    for(JPanel panel : fiveDayForecast)
    {
      bottomPanel.add(panel, BorderLayout.SOUTH);
    }
  }
  
  // ButtonListener class
  // Added as an ActionListener to all buttons
  private class ButtonListener implements ActionListener
  {
    public void actionPerformed(ActionEvent e)
    {
      // If the tempF button is pressed,
      if(e.getSource() == tempF)
      {
        // Disable it and enable tempC button so that the user can't press the F button. Also lets the user know that they are using fahrenheit
        tempF.setEnabled(false);
        tempC.setEnabled(true);
        
        // Change isF to true so that the panel will update with the correct unit of temperature
        isF = true;
        
        // Update the panel
        update();
      }
      
      // This is essentially the same thing except it's happening with celsius instead of fahrenheit
      else if(e.getSource() == tempC)
      {
        tempC.setEnabled(false);
        tempF.setEnabled(true);
        isF = false;
        update();
      }
      
      // If the search button is pressed,
      else if(e.getSource() == search)
      {
        try
        {
          // If the search bar contains nothing, then ignore it. Blank queries to the API does weird stuff
          if(searchBar.getText().equals(""))
          {
            // Also set the top label and notify the user of the invalid location
            topLabel.setText("Invalid location");            
          }
          
          else
          {
            // Use search method to reset the weatherInfo class.
            weatherInfo.search(searchBar.getText());
            
            // Then update the panel
            update();
            
            // Set the top label to nothing so that the error messages (if any) are erased
            topLabel.setText("");
            
            // This is only for when the user does their first search. I had to disable the refresh button when the user first starts the program because there is
            // nothing to refresh. The first time the user does their search, the refresh button will be enabled.
            refresh.setEnabled(true);
          }
        }
        
        // If anything goes wrong, we know that the user inputted an invalid location. 
        catch(Exception ex)
        {
          // Let the user know that their location entered was invalid
          topLabel.setText("Invalid location");
        }
      }
      
      // If the user clicks the refresh button,
      else if(e.getSource() == refresh)
      {
        try
        {
          // Refresh the weatherInfo class
          weatherInfo.setInputName(weatherInfo.getInputName());
          
          // Update panel 
          update();
        }
        
        catch(Exception ex)
        {
          ex.printStackTrace();
        }
      }
    }
  }
}
