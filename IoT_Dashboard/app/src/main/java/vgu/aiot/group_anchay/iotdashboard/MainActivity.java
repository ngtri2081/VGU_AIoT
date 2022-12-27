package vgu.aiot.group_anchay.iotdashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import android.graphics.Color;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{
    MQTTHelper mqttHelper;
    ToggleButton waterPumpButton;
    TextView txtTemperature, txtHumidity;
    Button weatherButton, detectorButton;
    static Calendar calendar = Calendar.getInstance();
    static Map<String, String> dictionary = new HashMap<>();
    static String url = "https://io.adafruit.com/api/v2/VGU_RTOS_Group11/feeds/";
    static String weatherUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Thu%20Dau%20Mot/next7days?unitGroup=metric&elements=datetime%2Ctempmax%2Ctempmin%2Cdescription%2Cicon&include=days&key=HJTDNTULRSEBHCC3BF2U54LV9&contentType=json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            startMQTT();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        txtTemperature = findViewById(R.id.temperatureText);
        txtHumidity = findViewById(R.id.humidityText);

        weatherButton = findViewById(R.id.weatherText);
        waterPumpButton = findViewById(R.id.waterPumpStatus);
        detectorButton = findViewById(R.id.intrusionDetector);

        dictionary.put("humidity", "%");
        dictionary.put("temperature", "°C");
        dictionary.put("water-pump", "");
        dictionary.put("intrusion-detector", "");

        getInitialData("temperature", txtTemperature);
        getInitialData("humidity", txtHumidity);
        getInitialData("water-pump", waterPumpButton);
        getWarningData("intrusion-detector", detectorButton);

        ArrayList<Weather> weatherArrayList = requestWeatherForecast();

        weatherButton.setOnClickListener(v -> {
            Intent intentLoadNewActivity = new Intent(MainActivity.this, NewActivity.class);
            intentLoadNewActivity.putExtra("weatherArray", weatherArrayList);
            startActivity(intentLoadNewActivity);
        });

        detectorButton.setOnClickListener(v -> {
            String status = "NORMAL";
            OkHttpClient client = new OkHttpClient();
            // Create the request body
            RequestBody requestBody = new FormBody.Builder()
                    .add("X-AIO-Key", "aio_idZr18DbedLdzNNQsjk0zAw45HrO")  // Add your Adafruit API key
                    .add("value", status)  // Add any other data you want to send to the Adafruit API
                    .build();
            // Create the request
            Request request = new Request.Builder()
                    .url(url + "intrusion-detector/data")  // Set the URL of the Adafruit API endpoint
                    .post(requestBody)  // Set the request method to POST and the request body
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    System.out.println("----ERROR ON SENDING DATA TO intrusion-detector----");
                    e.printStackTrace();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) {
                    if (response.isSuccessful()) {
                        System.out.println("----DATA SENT SUCCESSFULLY----");
                    }
                }
            });
            detectorButton.setText(status);
            detectorButton.setEnabled(false);
            detectorButton.setBackgroundColor(Color.parseColor("#4a764b"));
        });

        waterPumpButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sendPostRequest(isChecked, "water-pump");
        });
    }

    public void startMQTT(){
        mqttHelper = new MQTTHelper(this);
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                Log.d("TEST", topic + " *** " + message.toString());
                if (topic.contains("humidity")){
                    txtHumidity.setText(message + "%");
                } else if (topic.contains("temperature")){
                    txtTemperature.setText(message + "°C");
                } else if (topic.contains("water-pump")){
                    waterPumpButton.setText(message.toString());
                } else if (topic.contains("intrusion-detector")){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        detectorButton.setText(message.toString());
                        if (message.toString().equals("WARNING!")) {
                            detectorButton.setEnabled(true);
                            detectorButton.setBackgroundColor(Color.RED);
                        }
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private static ArrayList<Weather> requestWeatherForecast() {
        OkHttpClient client = new OkHttpClient();
        ArrayList<Weather> arr = new ArrayList<>();
        Request request = new Request.Builder()
                .url(weatherUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println("----ERROR ON REQUESTING WEATHER FORECAST IN 7 DAYS----");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String myResponse = response.body().string();
                    try {
                        JSONObject dataJSON = new JSONObject(myResponse);
                        JSONArray dataJSONArray = dataJSON.getJSONArray("days");
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        for (int i = 0; i < dataJSONArray.length(); i++) {
                            // GET THE DATA IN FORMS OF JSON OBJECT
                            JSONObject day = dataJSONArray.getJSONObject(i);
                            // THE DATE
                            String dateString = (String) day.get("datetime");
                            Date date = dateFormat.parse(dateString);
                            calendar.setTime(date);
                            // THE DESCRIPTION
                            String description = (String) day.get("description");
                            // HIGH TEMP
                            double highTemp = (double) day.get("tempmax");
                            // LOW TEMP
                            double lowTemp = (double) day.get("tempmin");
                            // WEATHER ICON
                            String weatherIcon = (String) day.get("icon");
                            // INIT WEATHER OBJECT
                            Weather weather = new Weather();
                            // SET THE DATA TO THE WEATHER OBJECT
                            weather.setDate(calendar.get(Calendar.DAY_OF_WEEK));
                            weather.setDescription(description);
                            weather.setHighTemp(highTemp);
                            weather.setLowTemp(lowTemp);
                            weather.setWeatherIcon(weatherIcon);
                            arr.add(weather);
                        }
                        System.out.println("----UPDATE WEATHER SUCCESSFULLY----");
                    } catch (JSONException e) {
                        System.out.println("----ERROR WHEN TRANSFORMING DATA STRING TO JSON----");
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return arr;
    }

    private void getInitialData(String feedKey, TextView textView) {
        OkHttpClient client = new OkHttpClient();
        String post = dictionary.get(feedKey);

        Request request = new Request.Builder()
                .url(url + feedKey + "/data/retain")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println("----ERROR ON REQUESTING" + feedKey + "----");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String myResponse = response.body().string();
                    String finalRes = myResponse.substring(0, myResponse.indexOf(',')) + post;
                    System.out.println("Request successfully!");
                    System.out.println("On screen: " + finalRes);

                    MainActivity.this.runOnUiThread(() -> textView.setText(finalRes));
                }
            }
        });
    }

    private void getWarningData(String feedKey, Button button) {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url + feedKey + "/data/retain")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println("----ERROR ON REQUESTING" + feedKey + "----");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String myResponse = response.body().string();
                    String finalRes = myResponse.substring(0, myResponse.indexOf(','));
                    System.out.println("Request successfully!");
                    System.out.println("On screen: " + finalRes);

                    MainActivity.this.runOnUiThread(() -> {
                        button.setText(finalRes);
                        if (finalRes.equals("WARNING!")){
                            button.setBackgroundColor(Color.RED);
                        } else {
                            button.setBackgroundColor(Color.parseColor("#4a764b"));
                        }
                    });
                }
            }
        });
    }

    private void sendPostRequest(boolean isChecked, String feedKey) {
        OkHttpClient client = new OkHttpClient();
        // Create the request body
        String data = isChecked ? "OFF" : "ON";
        RequestBody requestBody = new FormBody.Builder()
                .add("X-AIO-Key", "aio_idZr18DbedLdzNNQsjk0zAw45HrO")  // Add your Adafruit API key
                .add("value", data)  // Add any other data you want to send to the Adafruit API
                .build();
        // Create the request
        Request request = new Request.Builder()
                .url(url + feedKey + "/data")  // Set the URL of the Adafruit API endpoint
                .post(requestBody)  // Set the request method to POST and the request body
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println("----ERROR ON SENDING DATA TO " + feedKey + "----");
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                if (response.isSuccessful()) {
                    System.out.println("----DATA SENT SUCCESSFULLY----");
                }
            }
        });
    }

}
