import time
import serial.tools.list_ports

print("Sensors and Actiators")

### PRINT COMPORT NAME (STEP 3)
def getPort():
    ports = serial.tools.list_ports.comports()
    N = len(ports)
    commPort = "None"
    for i in range (0, N):
        port = ports[i]
        strPort = str(port)
        if "COM4" in strPort:
            splitPort = strPort.split(" ")
            commPort = (splitPort[0])
    return commPort

### OPEN THE COM PORT (STEP 4)
#portName = getPort()
#print(f"Portname: {portName}")
#if portName != "None":
#    ser1 = serial.Serial(port=portName,
#                        baudrate=9600)

### SEND COMMAND TO ACTUATORS (STEP 5)
relay1_ON = [0, 6, 0, 0, 0, 255, 200, 91]
relay1_OFF = [0, 6, 0, 0, 0, 0, 136, 27]
### SETUP FOR 1ST ACTUATORS (STEP 5)
def setDevice1(state, relay1_ON=relay1_ON, relay1_OFF=relay1_OFF):
    if state:
        ser1.write(relay1_ON)
    else:
        ser1.write(relay1_OFF)

### EXTEND FOR 2ND ACTUATORS (STEP 6)
relay2_ON = [15, 6, 0, 0, 0, 255, 200, 164]
relay2_OFF = [15, 6, 0, 0, 0, 0, 136, 228]
### SETUP FOR 2ND ACTUATORS (STEP 6)
def setDevice2(state, relay2_ON=relay2_ON, relay2_OFF=relay2_OFF):
    if state:
        ser1.write(relay2_ON)
    else:
        ser1.write(relay2_OFF)

### RECEIVE RESPONSE (STEP 7)
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

### READ SOIL TEMPERATURE (STEP 8)
soil_temperature = [1, 3, 6, 0, 1, 100, 11]
def readTemperature(ser, soil_temperature=soil_temperature):
    serial_read_data(ser)
    ser.write(soil_temperature)
    time.sleep(1)
    return serial_read_data(ser)

### READ SOIL MOISTURE (STEP 9)
soil_moisture = [1, 3, 0, 7, 0, 1, 53, 203]
def readMoisture(ser, soil_moisture=soil_moisture):
    serial_read_data(ser)
    ser.write(soil_moisture)
    time.sleep(1)
    return serial_read_data(ser)

if __name__ == "__main__":
    ### TESTING USING REAL SENSORS
    portName = getPort()
    print(f"Portname: {portName}")
    if portName != "None":
        ser1 = serial.Serial(port=portName,
                            baudrate=9600)

    while True:
        print("TEST SENSOR")
        print(f"TEMPERATURE: {readTemperature(ser1)}")
        print(f"MOISTURE: {readMoisture(ser1) / 100:.2f}")
        time.sleep(2)
