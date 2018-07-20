// +--------------------------------------------------------------+
// | Driver class for WeatherAppPanel                             |
// | Note: Be sure to include gson-2.7.jar as an extra classpath. |
// | By Bradley He                                                |
// +--------------------------------------------------------------+

import javax.swing.*;
import java.net.*;
import java.io.*;

public class WeatherApp
{
  public static void main(String[] args)
  {
    try 
    {
      UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
    } 
    
    catch (Exception e) 
    {
      e.printStackTrace();
    }
    
    // Create a JFrame and put a WeatherAppPanel inside it.
    JFrame frame = new JFrame("Weather");
    WeatherAppPanel panel;
    WeatherInfo info = new WeatherInfo();
    panel = new WeatherAppPanel(info);
    
    // Setting up the JFrame and making it visible
    frame.add(panel);
    frame.pack();
    frame.setLocationRelativeTo(null);
    frame.setResizable(false);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setVisible(true);
  }
}