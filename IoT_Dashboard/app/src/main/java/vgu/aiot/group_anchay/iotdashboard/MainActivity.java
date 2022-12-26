package vgu.aiot.group_anchay.iotdashboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
    ImageButton myImageButton;
    TextView txtTemperature, txtHumidity;
    static Calendar calendar = Calendar.getInstance();
    static Map<String, String> dictionary = new HashMap<>();
    static String url = "https://io.adafruit.com/api/v2/VGU_RTOS_Group11/feeds/";
    static String weatherUrl = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/Thu%20Dau%20Mot/next7days?unitGroup=metric&elements=description&include=days&key=HJTDNTULRSEBHCC3BF2U54LV9&contentType=json";

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
        myImageButton = findViewById(R.id.weatherImage);
        waterPumpButton = findViewById(R.id.waterPumpStatus);

        dictionary.put("humidity", "%");
        dictionary.put("temperature", " degree C");
        dictionary.put("water-pump", "");

        getInitialData("temperature", txtTemperature);
        getInitialData("humidity", txtHumidity);
        getInitialData("water-pump", waterPumpButton);

        ArrayList<Weather> weatherArrayList = requestWeatherForecast();

        myImageButton.setOnClickListener(v -> {
            Intent intentLoadNewActivity = new Intent(MainActivity.this, NewActivity.class);
            intentLoadNewActivity.putExtra("weatherArray", weatherArrayList);
            startActivity(intentLoadNewActivity);
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
                    txtTemperature.setText(message + " degree C");
                } else if (topic.contains("water-pump")){
                    waterPumpButton.setText(message.toString());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private static ArrayList<Weather> requestWeatherForecast() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(weatherUrl)
                .build();

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        ArrayList<Weather> arr = new ArrayList<>();

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
                        for (int i = 0; i < dataJSONArray.length(); i++) {
                            JSONObject day = dataJSONArray.getJSONObject(i);
                            // Do something with the element
                            Object description = day.get("description");
                            int currentDate = dayOfWeek + i;
                            System.out.println("----DAY " + currentDate + ": " + description);
                            arr.add(new Weather(currentDate, description.toString()));
                        }
                        System.out.println("----UPDATE WEATHER SUCCESSFULLY----");
                    } catch (JSONException e) {
                        System.out.println("----ERROR WHEN TRANSFORMING DATA STRING TO JSON----");
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
