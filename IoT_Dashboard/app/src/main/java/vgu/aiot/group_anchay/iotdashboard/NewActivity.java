package vgu.aiot.group_anchay.iotdashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewActivity extends AppCompatActivity {
ListView lsvWeather;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        lsvWeather = findViewById(R.id.lsvWeather);

        ArrayList<Weather> arr = new ArrayList<>();

        arr.add(new Weather(24,"So hot" ));
        arr.add(new Weather(24,"So hot" ));
        arr.add(new Weather(24,"So hot" ));
        arr.add(new Weather(24,"So hot" ));
        arr.add(new Weather(24,"So hot" ));
        arr.add(new Weather(24,"So hot" ));
        arr.add(new Weather(24,"So hot" ));

        WeatherAdapter adapter = new WeatherAdapter(this,0,arr);
        lsvWeather.setAdapter(adapter);
    }
}