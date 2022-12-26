import csv
import codecs
import re
import urllib.request
import sys

# BaseURL = 'http://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/weatherdata/'

def kelvin_to_celsius_fahrenheit (kelvin):
    celsius = kelvin - 273.15
    fahrenheit = celsius * (9/5) + 32
    return celsius, fahrenheit

def F_to_C (fahrenheit):
    celsius = (fahrenheit - 32) * (5/9)
    return round(celsius, 1)
# Build the entire query
URL = 'https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/binh%20duong%20province?unitGroup=us&include=days&key=WVX8938KKDNP8X28MTDR7FUAH&contentType=json'

# print(' - Running query URL: ', URL)
# print()

def retrieve_data_visualcrossing():
    # Parse the results as CSV
    CSVBytes = urllib.request.urlopen(URL)
    CSVText = csv.reader(codecs.iterdecode(CSVBytes, 'utf-8'))
    RowIndex = 0
    arr = []

    for Row in CSVText:
        FirstRow = Row
        # print(FirstRow)

    for col in FirstRow:
        pos = col.find(":")+1
        if re.search("2022", col):
            arr.append(col[-11:-1])
        elif re.search("tempmax", col):
            arr.append(col[pos:])
        elif re.search("tempmin", col):
            arr.append(col[pos:])
        elif re.search("humidity", col):
            arr.append(col[pos:])
        elif re.search("windspeed", col):
            arr.append(col[pos:])
        elif re.search("solarradiation", col):
            arr.append(col[pos:])
        elif re.search("conditions", col):
            arr.append(col[pos:])

    print(arr)
    return arr
    # ['2022-12-26', '87.7', '67.9', '71.0', '6.7', '212.8', '"Partially cloudy"',...]
#       date, temp_max, temp_min, humidity, wind_speed, solar_radiation, condition

# day 0 is today
def get_tempmax(day):
    arr = retrieve_data_visualcrossing()
    res = float(arr[1 + 7*day])
    return F_to_C(res)

def get_tempmin(day):
    arr = retrieve_data_visualcrossing()
    res = float(arr[2 + 7 * day])
    return F_to_C(res)

def get_humi(day):
    arr = retrieve_data_visualcrossing()
    res = float(arr[3 + 7 * day])
    return res

def get_windspeed(day):
    arr = retrieve_data_visualcrossing()
    res = float(arr[4 + 7 * day])
    return res

def get_solar(day):
    arr = retrieve_data_visualcrossing()
    res = float(arr[5 + 7 * day])
    return res

def get_condition(day):
    arr = retrieve_data_visualcrossing()
    res = arr[6 + 7 * day]
    return res
print(get_tempmax(1))

