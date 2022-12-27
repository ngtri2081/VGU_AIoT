package vgu.aiot.group_anchay.iotdashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WeatherAdapter extends ArrayAdapter<Weather> {
    private final Context context;
    private final ArrayList<Weather> weatherArrayList;
    public WeatherAdapter(@NonNull Context context, @NonNull List<Weather> weatherArrayList) {
        super(context, R.layout.custom_weather_item, weatherArrayList);
        this.context = context;
        this.weatherArrayList = new ArrayList<>(weatherArrayList);

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        Weather weather = weatherArrayList.get(position);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.custom_weather_item, parent, false);

        TextView dayView = (TextView) rowView.findViewById(R.id.day_of_week);
        dayView.setText(weather.getDayOfWeek());

        TextView highTempView = (TextView) rowView.findViewById(R.id.high_temp);
        highTempView.setText(String.valueOf(weather.getHighTemp()));

        TextView lowTempView = (TextView) rowView.findViewById(R.id.low_temp);
        lowTempView.setText(String.valueOf(weather.getLowTemp()));

        TextView descriptionView = (TextView) rowView.findViewById(R.id.descriptionText);
        descriptionView.setText(weather.getDescription());

        ImageView weatherIconView = (ImageView) rowView.findViewById(R.id.weatherIcon);
        switch (weather.getWeatherIcon()) {
            case "partly-cloudy-day":
                weatherIconView.setImageResource(R.drawable.partlycloudy);
                break;
            case "cloudy":
                weatherIconView.setImageResource(R.drawable.cloudy);
                break;
            case "rain":
                weatherIconView.setImageResource(R.drawable.rainy);
                break;
            case "sunny":
                weatherIconView.setImageResource(R.drawable.sunny);
                break;
            // Add cases for other weather types as needed
        }

        return rowView;
    }
}
