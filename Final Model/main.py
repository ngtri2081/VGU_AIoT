import sys
import time
from ai import *
from Adafruit_IO import MQTTClient
import serial.tools.list_ports

AIO_FEED_ID = ["temperature", "humidity", "vision-detector", "voice-command"]
AIO_USERNAME = "VGU_RTOS_Group11"
AIO_KEY = "aio_idZr18DbedLdzNNQsjk0zAw45HrO"

def connected(client):
    print("Ket noi thanh cong ...")
    for topic in AIO_FEED_ID:
      client.subscribe(topic)

def subscribe(client , userdata , mid , granted_qos):
    print("----Subscribed ...")

def disconnected(client):
    print("----Disconnecting ...")
    sys.exit (1)

def message(client , feed_id , payload):
    if feed_id == "voice-command":
        print(f"----Receive voice command: {payload}")

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

### SETUP FOR 1ST ACTUATORS
def setDevice1(state, relay1_ON, relay1_OFF):
    if state:
        ser1.write(relay1_ON)
    else:
        ser1.write(relay1_OFF)

### SETUP FOR 2ND ACTUATORS
def setDevice2(state, relay2_ON, relay2_OFF):
    if state:
        ser1.write(relay2_ON)
    else:
        ser1.write(relay2_OFF)

### RECEIVE RESPONSE
def serial_read_data(ser):
    bytesToRead = ser.inWaiting()
    if bytesToRead > 0:
        out = ser.read(bytesToRead)
        data_array = [b for b in out]
        print(data_array)
        if (len(data_array) >= 7):
            array_size = len(data_array)
            value = data_array[array_size - 4] * 256 + data_array[array_size - 3]
            return value
        else:
            return -1
    return 0

### READ SOIL TEMPERATURE
def readTemperature(ser, soil_temperature):
    serial_read_data(ser)
    ser.write(soil_temperature)
    time.sleep(1)
    return serial_read_data(ser)

### READ SOIL MOISTURE
def readMoisture(ser, soil_moisture):
    serial_read_data(ser)
    ser.write(soil_moisture)
    time.sleep(1)
    return serial_read_data(ser)


if __name__ == "__main__":
    client = MQTTClient(AIO_USERNAME , AIO_KEY)

    client.on_connect = connected
    client.on_disconnect = disconnected
    client.on_message = message
    client.on_subscribe = subscribe

    client.connect()
    client.loop_background()

    ### SEND COMMAND TO ACTUATORS
    data_air2_temp = [3, 3, 0, 0, 0, 1, 133, 232]
    data_air2_humi = [3, 3, 0, 1, 0, 1, 212, 40]
    data_pm_25 = [4, 3, 0, 12, 0, 1, 68, 92]
    data_pm_10 = [4, 3, 0, 13, 0, 1, 21, 156]
    data_co2 = [2, 3, 0, 4, 0, 1, 197, 248]
    ### TESTING USING REAL SENSORS
    #portName = getPort()
    #print(f"Portname: {portName}")
    #if portName != "None":
    #    ser1 = serial.Serial(port=portName,
    #                        baudrate=9600)

    while True:
        image_capture()
        ai_result = image_detector()
        time.sleep(1)
        #temperature_result = readTemperature(ser=ser1, soil_temperature=data_air2_temp)
        #moisture_result = readMoisture(ser=ser1, soil_moisture=data_air2_humi)
        print(f"AI RESULT: {ai_result}")
        #print(f"TEMPERATURE: {temperature_result} degree")
        #print(f"MOISTURE: {moisture_result}%")
        #client.publish("temperature", temperature_result)
        #client.publish("moisture", moisture_result)
        client.publish("vision-detector", ai_result)
        time.sleep(5)