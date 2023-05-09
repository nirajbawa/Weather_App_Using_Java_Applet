import java.applet.*;
import java.awt.*;
import java.awt.event.*;

import java.math.BigDecimal;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.*;
import java.net.URL;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/*
    <APPLET code="WeatherApp", width="490", height="530"> </APPLET>
 */


//compilation commands  

//  javac8 -cp json.jar WeatherApp.java

// appletviewer -J-Djava.security.policy=applet.policy WeatherApp.java

// appletviewer -J-Djava.class.path=json.jar -J-Djava.security.policy=applet.policy WeatherApp.java

// Applet App Main Class

public class WeatherApp extends Applet implements ActionListener {
    // variables for storing data
    Color boxColor;
    TextField textField;
    Font f;
    Button enterBtn;
    String tempr = "-";
    String city = "city: -";
    String country = "country : -";
    String humidityt = "humidity : -";
    String wind = "wind : -";
    FetchLocation a;
    String cityNamet;
    BigDecimal lat;
    String countryt;
    BigDecimal lon;
    String ApiKey = "130ace1fc10ddbbf672b273155bb2337";
    Float  temprature;
    Float  humidity;
    Float windt;
    String search = "";

  
    // fetch Weather data using latitude and longitude
    class FetchWeatherData extends Thread{
        public void run(){
            try {
                // creating the network request to api
                URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&appid="+ApiKey);
    
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    
                conn.setRequestMethod("GET");
    
                int responseCode = conn.getResponseCode();
                System.out.println("Response code : " + responseCode);
    
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String temp = " ";
    
                StringBuffer response = new StringBuffer();
    
                while ((temp = reader.readLine()) != null) {
                    response.append(temp);
                }
                reader.close();
                System.out.println("weather details : " + response.toString());
                conn.disconnect();

                
                // parsing json  
    
                JSONObject jsonObject = new JSONObject(response.toString());

                // Get the inner object "data"
                JSONObject innerObject = jsonObject.getJSONObject("main");

                // Access the values within the inner object
                temprature = innerObject.getFloat("temp");
                  // Celsius = (Kelvin – 273.15) 
                temprature = (temprature-273.15f);
                humidity = innerObject.getFloat("humidity");

                JSONObject innerObject2 = jsonObject.getJSONObject("wind");

                windt = innerObject2.getFloat("speed");


                System.out.println("temp: " + temprature);
                System.out.println("humidity: " + humidity);

                
                city = "city : "+cityNamet;
                country = "country : "+countryt;
                humidityt = "humidity : "+ String.valueOf(humidity);
                tempr = String.valueOf(String.format("%.2f",temprature))+"\u2103";
                wind = "Wind : "+String.valueOf(windt);

                repaint();

    
            } catch (Exception e) {
                tempr = "Try Again";
                repaint();
                System.out.println(e);
            }
        }
    }

    // fetch Location data using city name
    class FetchLocation extends Thread{
        public void run(){
            try {
                 // creating the network request to api
                URL url = new URL("https://api.openweathermap.org/geo/1.0/direct?q="+search+"&limit=1&appid="+ApiKey);
    
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    
                conn.setRequestMethod("GET");
    
                int responseCode = conn.getResponseCode();
                System.out.println("Response code : " + responseCode);
    
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String temp = " ";
    
                StringBuffer response = new StringBuffer();
    
                while ((temp = reader.readLine()) != null) {
                    response.append(temp);
                }
                reader.close();
                System.out.println("response : " + response.toString());
                conn.disconnect();
    
                // parsing json             
                JSONArray array = new JSONArray(response.toString());
    
                JSONObject object = array.getJSONObject(0);
                cityNamet = object.getString("name");
                lat = object.getBigDecimal("lat");
                lon = object.getBigDecimal("lon");  
                countryt = object.getString("country");  
                System.out.println(cityNamet);
                System.out.println(lat);
                System.out.println(lon);
                System.out.println(countryt);

                // invoking Fetch Weather Data thread
                FetchWeatherData fdata = new FetchWeatherData();
                fdata.start();
    
            } catch (Exception e) {
                tempr = "Try Again";
                repaint();
                System.out.println(e);
            }
        }
    
    }

   
    //Applet initilizaion method
    public void init() {

        // set font
        f = new Font("Monotype Corsiva", Font.BOLD, 20);
        setFont(f);

        // add ui controls
        textField = new TextField("Enter City Name");
        boxColor = new Color(80, 219, 180);
        enterBtn = new Button("Search");
        add(textField);
        add(enterBtn);

        // set actiond listener
        enterBtn.addActionListener(this);
      

    }

    //applet paint method
    public void paint(Graphics g) {

        // set applet ui components
        g.setColor(boxColor);
        g.fillRoundRect(50, 130, 400, 250, 30, 30);
        g.setColor(Color.BLACK);
        g.drawString(tempr, 205, 230);
        g.drawString(city, 90, 300);
        g.drawString(country, 90, 340);
        g.drawString(humidityt, 260, 300);
        g.drawString(wind, 260, 340);

    }

    // event handler
    public void actionPerformed(ActionEvent ae) {
        tempr = "wait....";
        search = textField.getText();
        repaint();
        try {
            // invoking Fetch location thread
            a = new FetchLocation();
            a.start();
        } catch (Exception e) {
            System.out.println(e);
        }

    }
}