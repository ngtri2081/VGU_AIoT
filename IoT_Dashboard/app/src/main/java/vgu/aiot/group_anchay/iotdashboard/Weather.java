package vgu.aiot.group_anchay.iotdashboard;

import androidx.annotation.NonNull;

import java.io.Serializable;

public class Weather implements Serializable {
    public int date;
    public String description;

    public Weather(int date, String description) {
        this.date = date;
        this.description = description;
    }

    @NonNull
    @Override
    public String toString(){
        return "Day: " + date + ", description: " + description;
    }
}
