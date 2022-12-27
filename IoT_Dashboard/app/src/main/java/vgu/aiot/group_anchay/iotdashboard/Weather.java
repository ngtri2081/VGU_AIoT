package vgu.aiot.group_anchay.iotdashboard;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;

public class Weather implements Serializable {
    public int date;
    public double highTemp;
    public double lowTemp;
    public String description;
    public String weatherIcon;

    public Weather(){
        this.date = 0;
        this.highTemp = 0;
        this.lowTemp = 0;
        this.description = "";
        this.weatherIcon = "";
    }

    public Weather(int date, double highTemp, double lowTemp, String description, String weatherIcon) {
        this.date = date;
        this.highTemp = highTemp;
        this.lowTemp = lowTemp;
        this.description = description;
        this.weatherIcon = weatherIcon;
    }

    public int getDate() {
        return this.date;
    }

    public double getHighTemp() {
        return this.highTemp;
    }

    public double getLowTemp() {
        return this.lowTemp;
    }

    public String getDescription() {
        return this.description;
    }

    public String getWeatherIcon() {
        return this.weatherIcon;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public void setHighTemp(double highTemp) {
        this.highTemp = highTemp;
    }

    public void setLowTemp(double lowTemp) {
        this.lowTemp = lowTemp;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWeatherIcon(String weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public String getDayOfWeek() {
        switch (this.date) {
            case Calendar.SUNDAY:
                return "Sunday";

            case Calendar.MONDAY:
                return "Monday";

            case Calendar.TUESDAY:
                return "Tuesday";

            case Calendar.WEDNESDAY:
                return "Wednesday";

            case Calendar.THURSDAY:
                return "Thursday";

            case Calendar.FRIDAY:
                return "Friday";

            case Calendar.SATURDAY:
                return "Saturday";

            default:
                return "Invalid day of week";
        }
    }

    @NonNull
    @Override
    public String toString(){
        return "Day: " + getDayOfWeek() + ", day in week: " + date + ", description: " + description + ", high temp: " + highTemp + ", low temp: " + lowTemp + ", icon: " + getWeatherIcon();
    }
}
