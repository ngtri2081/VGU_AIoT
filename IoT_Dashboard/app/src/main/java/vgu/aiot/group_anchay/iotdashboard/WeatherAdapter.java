package vgu.aiot.group_anchay.iotdashboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class WeatherAdapter extends ArrayAdapter<Weather> {
    private Context ct;
    private ArrayList<Weather> arr;
    public WeatherAdapter(@NonNull Context context, int resource, @NonNull List<Weather> objects) {
        super(context, resource, objects);
        this.ct= context;
        this.arr= new ArrayList<>(objects);

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        if(convertView == null){
            LayoutInflater i = (LayoutInflater)ct.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = i.inflate(R.layout.weather_layout,null);
        }

        if(arr.size() > 10){

            Weather w = arr.get(position);
            TextView txtDay = convertView.findViewById(R.id.dayText);
            TextView txtDescription = convertView.findViewById(R.id.descriptionText);

            txtDay.setText(w.date);
            txtDescription.setText(w.description);

        }
        return convertView;
    }
}
