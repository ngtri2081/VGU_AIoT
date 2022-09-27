import sys
from simple_ai import *
from Adafruit_IO import MQTTClient

AIO_FEED_ID = ["Actuator1", "Actuator2"]
AIO_USERNAME = "VGU_RTOS_Group11"
AIO_KEY = "aio_wOBS48lAtMKBAfYpu710i3ea0Xux"

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

client = MQTTClient(AIO_USERNAME , AIO_KEY)
client.on_connect = connected
client.on_disconnect = disconnected
client.on_message = message
client.on_subscribe = subscribe
client.connect()
client.loop_background()

while True:
    os.time.sleep(5)
    image_capture()
    ai_result = image_detector()
    client.publish("Vision Detector", ai_result)
    pass