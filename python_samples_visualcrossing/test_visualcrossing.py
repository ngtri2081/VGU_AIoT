import codecs
import csv
import json
import requests
import sys


url = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/binh%20duong%20province?unitGroup=us&include=days&key=WVX8938KKDNP8X28MTDR7FUAH&contentType=csv"
response = requests.get(url)
response.raise_for_status()  # raises exception when not a 2xx response
if response.status_code != 204:
    print(response.json())
