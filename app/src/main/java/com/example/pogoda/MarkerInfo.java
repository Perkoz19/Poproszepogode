package com.example.pogoda;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.pogoda.utils.Weather;

import org.json.JSONException;

import java.io.IOException;

public class MarkerInfo extends MapsActivity{
    private String latit = "";
    private String longit = "";
    Bitmap img = null;

    private TextView title;
    private TextView condDescr;
    private TextView condDet;
    private TextView temp;
    private TextView press;
    private TextView windSpeed;
    private TextView hum;

    private ImageView imgView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            latit = extras.getString("ltt");
            longit = extras.getString("lgt");
            WeatherInfo wi = new WeatherInfo();
            wi.execute(new String[]{latit, longit});
        }

        setContentView(R.layout.markerinfo_layout);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int)(width*0.8), (int)(height*0.8));

        title = findViewById(R.id.poptitle);
        temp = findViewById(R.id.poptemperature);
        condDescr = findViewById(R.id.popcond);
        condDet = findViewById(R.id.popconddet);
        press = findViewById(R.id.poppress);
        windSpeed = findViewById(R.id.popwind);
        hum = findViewById(R.id.pophumid);
        imgView = findViewById(R.id.popicon);
    }

    private class WeatherInfo extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            Client cl = new Client();
            String data = null;
            try {
                data = cl.getWeatherData(params[0], params[1]);
            } catch(IOException e){
                throw new RuntimeException(e);
            }

            try {
                weather = Parser.getWeather(data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            title.setText(weather.location.getCity() + ", " + weather.location.getCountry());
            condDescr.setText(weather.currentCondition.getCondition());
            condDet.setText(weather.currentCondition.getDescr());
            temp.setText(String.format("" + Math.round((weather.temperature.getTemp() - 273.15))) + "°C");
            hum.setText("Humidity: " + weather.currentCondition.getHumidity() + "%");
            press.setText("Air pressure: " + weather.currentCondition.getPressure() + " hPa");
            windSpeed.setText("Wind speed: " + weather.wind.getSpeed() + " m/s");

            String xx = condDescr.getText().toString();
            switch(xx){
                case "Rain":
                    imgView.setImageResource(R.drawable.rain);
                    break;
                case "Drizzle":
                    imgView.setImageResource(R.drawable.drizzle);
                    break;
                case "Thunderstorm":
                    imgView.setImageResource(R.drawable.thunderstorm);
                    break;
                case "Snow":
                    imgView.setImageResource(R.drawable.snow);
                    break;
                case "Mist":
                    imgView.setImageResource(R.drawable.mist);
                    break;
                case "Clouds":
                    imgView.setImageResource(R.drawable.cloud);
                    break;
                case "Clear":
                    imgView.setImageResource(R.drawable.clear);
                    break;
                default:
                    imgView.setImageResource(R.drawable.nodata);
            }
        }
    }
}
