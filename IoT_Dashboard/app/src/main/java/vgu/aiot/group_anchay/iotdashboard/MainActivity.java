package vgu.aiot.group_anchay.iotdashboard;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{

    MQTTHelper mqttHelper;
    ImageButton myImageButton;
    TextView txtTemperature, txtHumidity, txtAudio;
    Map<String, String> dictionary = new HashMap<>();

    private void getInitialData(String feedKey, TextView textView) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://io.adafruit.com/api/v2/VGU_RTOS_Group11/feeds/";
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
                    System.out.println("On screen: " + finalRes);

                    MainActivity.this.runOnUiThread(() -> textView.setText(finalRes));
                }
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try{
            startMQTT();
        } catch (Exception exception){
            exception.printStackTrace();
        }
        txtTemperature = findViewById(R.id.temperatureText);
        txtHumidity = findViewById(R.id.humidityText);


        setContentView(R.layout.activity_main);
        myImageButton = findViewById(R.id.weatherImage);
        myImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intentLoadNewActivity = new Intent(MainActivity.this, NewActivity.class);
                    startActivity(intentLoadNewActivity);
               }
            });
        }

        txtAudio = findViewById(R.id.unavailableText);

        dictionary.put("humidity", "%");
        dictionary.put("temperature", " degree C");

        getInitialData("temperature", txtTemperature);
        getInitialData("humidity", txtHumidity);
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
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }


}
