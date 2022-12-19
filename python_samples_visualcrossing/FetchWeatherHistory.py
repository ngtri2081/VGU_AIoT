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
# Build the entire query
URL = 'https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/binh%20duong%20province?unitGroup=us&include=days&key=WVX8938KKDNP8X28MTDR7FUAH&contentType=json'

print(' - Running query URL: ', URL)
print()

# Parse the results as CSV
CSVBytes = urllib.request.urlopen(URL)
CSVText = csv.reader(codecs.iterdecode(CSVBytes, 'utf-8'))
RowIndex = 0
arr = []

for Row in CSVText:
    FirstRow = Row
    print(FirstRow)

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
print()

