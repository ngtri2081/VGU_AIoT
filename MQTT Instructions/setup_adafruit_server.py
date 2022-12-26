import sys
import time
from datetime import date,datetime
# from simple_ai import *
from weather_and_forecast import *
from Adafruit_IO import MQTTClient

AIO_FEED_ID = ["Actuator1", "Actuator2", "TEMP_MAX"]
AIO_USERNAME = "VGU_RTOS_Group11"
AIO_KEY = "aio_idZr18DbedLdzNNQsjk0zAw45HrO"

def connected(client):
    print("Ket noi thanh cong ...")
    for topic in AIO_FEED_ID:
      client.subscribe(topic)

def subscribe(client , userdata , mid , granted_qos):
    print("Subscribe thanh cong ...")

def disconnected(client):
    print("Ngat ket noi ...")
    sys.exit (1)

def message(client , feed_id , payload):
    print("Nhan du lieu: " + payload)

Init_has_run = False
t = time.localtime()
cur_t = time.strftime("%H:%M", t)
def Init():
    global Init_has_run
    global cur_t
    t1 = time.localtime()
    current_t = time.strftime("%H:%M", t1)
    if cur_t != current_t:
        cur_t = current_t
        Init_has_run = False
    if Init_has_run:
        return
    Init_has_run = True
    client.publish("temp-max", get_tempmax(1))

client = MQTTClient(AIO_USERNAME , AIO_KEY)
client.on_connect = connected
client.on_disconnect = disconnected
client.on_message = message
client.on_subscribe = subscribe
client.connect()
client.loop_background()

while True:
    time.sleep(5)
    # image_capture()
    # ai_result = image_detector()
    # client.publish("vision-detector", ai_result)
    Init()
    print(cur_t)