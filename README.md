# VGU_AIoT
Group AN CHAY CS2019 2022 - 2023 Project

# Installing requirements

Clone the project, then go to terminal to install all the required packages

```
pip install -r requirements.txt
```

# User Manual

Follow the steps below to successfully use our application:

1. Create an [Adafruit IO Account](https://io.adafruit.com) and create 4 feeds named `Humidity`, `Temperature`, `Intrusion Detector` and `Water Pump`. Make sure their keys are `humidity`, `intrusion-detector`, `temperature` and `water-pump` respectively.

![Adafruit IO Setup](/Documentation/adafruit-setup.png)

2. Retrieve Adafruit API key as instructed in the above image. Make sure that all of your feeds are public so that the mobile app can retrieve the IoT Dashboard data using [Adafruit API](https://io.adafruit.com/api/docs/#adafruit-io-http-api).

3. Place your Adafruit API key on the [config.yml](/config.yml) file (note that you will have to create this file by your self and place it on the [root folder](/) of the project). For example, in the `/config.yml` file:
```yml
aio_key: PLACE_YOUR_ADAFRUIT_IO_API_KEY_HERE
```

4. Create an IoT Dashboard to track your data. You can customize it by your own. Take ours as an example:

![IoT Dashboard](/Documentation/dashboard.png)

5. Create [Visual Crossing](https://www.visualcrossing.com/weather-api) account to begin using Visual Crossing Weather API.

6. Go to [Weather Query Builder](https://www.visualcrossing.com/weather/weather-data-services/Thu%20Dau%20Mot?v=api) to get the URL for querying weather forcast. You can choose any options that you like, from day range to weather elements. Then copy the URL as instructed on the below image.

![Visual Crossing API](/Documentation/vsapi.png)

7. Go to [MainActivity.java](/IoT_Dashboard/app/src/main/java/vgu/aiot/group_anchay/iotdashboard/MainActivity.java) and replace my Visual Crossing API URL with the one that you copied in the previous step:

```java
static Calendar calendar = Calendar.getInstance();
    static Map<String, String> dictionary = new HashMap<>();
    static String url = "YOUR_VISUAL_CROSSING_URL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
```

8. Use the `Adafruit IO Key` in the step 3 and place it where the `Adafruit API Key` appears: [MainActivity.java](/IoT_Dashboard/app/src/main/java/vgu/aiot/group_anchay/iotdashboard/MainActivity.java), [MQTTHelper.java](/IoT_Dashboard/app/src/main/java/vgu/aiot/group_anchay/iotdashboard/MQTTHelper.java). For example, on `MainActivity.java`, at function `sendPostRequest`:

```java
private void sendPostRequest(boolean isChecked, String feedKey) {
        OkHttpClient client = new OkHttpClient();
        // Create the request body
        String data = isChecked ? "OFF" : "ON";
        RequestBody requestBody = new FormBody.Builder()
                .add("X-AIO-Key", "PLACE_YOUR_ADAFRUIT_API_KEY_HERE")  // Add your Adafruit API key
                .add("value", data)  // Add any other data you want to send to the Adafruit API
                .build();
        // Create the request
        Request request = new Request.Builder()
                .url(url + feedKey + "/data")  // Set the URL of the Adafruit API endpoint
                .post(requestBody)  // Set the request method to POST and the request body
                .build();

```
9. Start the [remote monitoring system](/Model/main.py). You should be able to see a screen with title `Intrusion Warning` appears on your screen:

![Intruder Detection](/Documentation/monitoring-system.png)

10. Start the [mobile application](/IoT_Dashboard/app/src/main/java/vgu/aiot/group_anchay/iotdashboard/MainActivity.java). You should be able to see the app like this:

![Mobile Application](/Documentation/mobile-app.gif)

11. At your `Intrusion Warning` window, use your mouse to draw a polygon represents your interested area, for example, your crop field. Then, when you finish, press `d` to trigger the object detector. When you want to stop the object detector, press `q`.

![Object Detection](/Documentation/intrusion-detector.gif)

12. Check your `IoT Dashboard` and `Mobile Application`.

13. In case you want to use the sensors, setup your sensors and connect the sensors through cables. Open `Device Manager` on your PC, find the port that connects to the sensors. Then, use the name of that port to place in the [main.py](/Model/main.py), at function `getPort()`. For example, if your connected port is `COM5`, then your `getPort()` function should look like this:
```python
def getPort():
    ports = serial.tools.list_ports.comports()
    N = len(ports)
    commPort = "None"
    for i in range (0, N):
        port = ports[i]
        strPort = str(port)
        if "COM5" in strPort: # CHANGE COM PORT NUMBER HERE IN DEVICE MANAGER
            splitPort = strPort.split(" ")
            commPort = (splitPort[0])
    return commPort
```

14. At [main.py](/Model/main.py), change those 2 lines so that the `monitoring system` can interpret the sensor's data. In this project, we use RS485 MODBUS protocol:
```python
### SEND COMMAND TO ACTUATORS
    points = []
    animals_and_persons = ["person", "dog", "cat", "bird", "horse", "sheep", "cow", "elephant", "bear", "zebra", "giraffe"]
    data_air2_temp = [3, 3, 0, 0, 0, 1, 133, 232] # CHANGE THIS LINE FOR TEMPERATURE SENSOR
    data_air2_humi = [3, 3, 0, 1, 0, 1, 212, 40] # CHANGE THIS LINE FOR HUMIDITY SENSOR
```
