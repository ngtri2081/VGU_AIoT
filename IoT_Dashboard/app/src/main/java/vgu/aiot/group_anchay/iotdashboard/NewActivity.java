package vgu.aiot.group_anchay.iotdashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;


public class NewActivity extends AppCompatActivity {
    static ArrayList<Weather> arr;
    ListView lsvWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        lsvWeather = findViewById(R.id.lsvWeather);

        arr = (ArrayList<Weather>) getIntent().getExtras().get("weatherArray");
        for (Weather weather : arr) {
            System.out.println(weather);
        }
        System.out.println("----SUCCESSFULLY RETRIEVE WEATHER FORECAST----");
        WeatherAdapter adapter = new WeatherAdapter((Context) this,0, arr);
        lsvWeather.setAdapter(adapter);
    }
}