package vgu.aiot.group_anchay.iotdashboard;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;


public class MainActivity extends AppCompatActivity {
    MQTTHelper mqttHelper;
    TextView txtTemperature, txtHumidity;
    ImageButton myImageButton;

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
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.d("TEST", topic + " *** " + message.toString());
                if (topic.contains("actuator1")){
                    txtHumidity.setText(message.toString() + "%");
                } else if (topic.contains("actuator2")){
                    txtTemperature.setText(message.toString());
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }


}
