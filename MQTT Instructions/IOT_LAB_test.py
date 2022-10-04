import serial.tools.list_ports
import time
import sys

def getPort():
    ports = serial.tools.list_ports.comports()
    N = len(ports)
    commPort = "None"
    for i in range(0, N):
        port = ports[i]
        strPort = str(port)
        if "USB Serial Device" in strPort: #change to COM3, find it in Device management/Port
            splitPort = strPort.split(" ")
            commPort = (splitPort[0])
    return commPort

def setDevice(state):
    if state == True:
        ser.write(relay1_ON)
    else:
        ser.write(relay1_OFF)


#====================**==================#
portName = getPort()
ser = None
if portName != "None":
    #print Serial name
    ser = serial.Serial(port=portName, baudrate=9600)

relay1_ON = [0, 6, 0, 0, 0, 255, 200, 91]
relay1_OFF = [0, 6, 0, 0, 0, 136, 27]
#test on-off tunnel
# while True:
#     ser.write(relay1_ON)
#     time.sleep(2)
#     ser.write(relay1_OFF)
#     time.sleep(10)
