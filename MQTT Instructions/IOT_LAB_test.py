import serial.tools.list_ports

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

#====================**==================#
portName = getPort()
if portName != "None":
    #print Serial name
    ser = serial.Serial(port=portName, baudrate=9688)

print(ser)