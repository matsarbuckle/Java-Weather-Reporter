import com.google.gson.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WeatherForecast {

    public static void main(String[] args) throws IOException {

            // From Terminal Argument :
            float lgCd = Float.parseFloat(args[3]); // Longitude
            float ltCd = Float.parseFloat(args[1]); // Latitude

            // Extra Credit : Reverse Geocoding API URL
            URL geoURL = new URL(" https://geocode.maps.co/reverse?lat="+ ltCd +"&lon=" + lgCd);

            // Weather API URL
            URL myurl = new URL("https://api.open-meteo.com/v1/forecast?latitude=" + ltCd + "&longitude=" +
                    lgCd + "&hourly=temperature_2m,rain,uv_index,cloud_cover&temperature_unit=fahrenheit&timezone=EST");


            //HTTPS Connection
            HttpsURLConnection connection = (HttpsURLConnection) myurl.openConnection();
            HttpsURLConnection geoConnection = (HttpsURLConnection) geoURL.openConnection();

            //Connection Set-up
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            geoConnection.setRequestMethod("GET");
            geoConnection.setConnectTimeout(5000);
            geoConnection.setReadTimeout(5000);
            // => Connection Set-up Complete


            //Test Response Code of the HTTP Request.
            // Goal : OUTPUT ==> 200
            try {
                if (connection.getResponseCode() == 200 && geoConnection.getResponseCode() == 200) {
                    BufferedReader readConnection = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    BufferedReader readgeoConnection = new BufferedReader(new InputStreamReader(geoConnection.getInputStream()));
                    StringBuilder container = new StringBuilder();
                    StringBuilder geoContainer = new StringBuilder();
                    String line;
                    String geoLine;
                    while ((line = readConnection.readLine()) != null) {
                        container.append(line);
                    }
                    while ((geoLine = readgeoConnection.readLine()) != null) {
                        geoContainer.append(geoLine);
                    }

                    //Grabbing Components[city, country, state] from the Reverse GeoCode API
                    JsonElement myG = JsonParser.parseString(geoContainer.toString());
                    JsonObject gsonObject = myG.getAsJsonObject();
                    gsonObject = gsonObject.getAsJsonObject("address");
                    JsonPrimitive city = gsonObject.getAsJsonPrimitive("city");
                    JsonPrimitive country = gsonObject.getAsJsonPrimitive("country");
                    JsonPrimitive state = gsonObject.getAsJsonPrimitive("state");

                     //Grabbing Components[time,temp,uv_index,rain,cloud_cover] from the Weather API
                    JsonElement myE = JsonParser.parseString(container.toString());
                    JsonObject jsonObject = myE.getAsJsonObject();
                    jsonObject = jsonObject.getAsJsonObject("hourly");
                    JsonArray time = jsonObject.getAsJsonArray("time");
                    JsonArray temp = jsonObject.getAsJsonArray("temperature_2m");
                    JsonArray rain = jsonObject.getAsJsonArray("rain");
                    JsonArray uvIndex = jsonObject.getAsJsonArray("uv_index");
                    JsonArray cloud = jsonObject.getAsJsonArray("cloud_cover");


                    //Formatting data from JsonArrays and JsonPrimitives
                    // Title :
                    System.out.printf("-----------------------------------------------%n");
                    System.out.printf("|  " + city.getAsString() +  " 7-Day Forecast in Fahrenheit    |%n");
                    System.out.printf("|  State: " + state.getAsString() + "                      |%n");
                    System.out.printf("|  Country: " + country.getAsString() + "                     |%n");
                    System.out.printf("-----------------------------------------------%n");

                    //Data formatted into a table
                    int setter = 0;
                    for (int i = 0; i < time.size() - 1; i++) {
                        //Setting time[seven day week]
                        if (i == setter) {
                            System.out.println();
                            System.out.printf("-----------------------------------------------%n");
                            System.out.println("|           Forecast for " + time.get(i).getAsString().substring(0, 10) + "           |");
                            System.out.printf("-----------------------------------------------%n");
                            System.out.printf("| %-3s  |  %-3s  | %-1s | %-1s | %-1s |%n", "Time", " °F ", " Rain ", "UV Idx ", "Cloud %");
                            System.out.printf("-----------------------------------------------%n");
                            setter = setter + 24;
                        }

                        //Data Points [per 3 hours during a given day]
                        String timet = time.get(i)
                                .getAsString().substring(11); // Military time
                        String tempt = temp.get(i).getAsString() + "°F"; // Temperature
                        String raint = rain.get(i).getAsString() + "mm"; // Rainfall in (mm)
                        String uvt = uvIndex.get(i).getAsString() + " UV"; // UV index
                        String cloudt = cloud.get(i).getAsString() + "%"; // Cloudiness (%)
                        System.out.printf("| %-2s | %-2s | %-2s | %-2s |   %-4s" + "  |%n", timet, tempt, raint, uvt, cloudt);
                        System.out.printf("-----------------------------------------------%n");
                        i++;
                        i++;
                    }
                }
            }catch(IOException E){
                E.printStackTrace();
            }
    }


}

