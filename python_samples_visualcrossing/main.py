import datetime as dt
import requests
BASE_URL = "http://api.openweathermap.org/data/2.5/weather?"
API_KEY = "144435398fa6f540b507ab3f69536f00"
CITY = "Binh Duong"
def kelvin_to_celsius_fahrenheit (kelvin):
    celsius = kelvin - 273.15
    fahrenheit = celsius * (9/5) + 32
    return celsius, fahrenheit
url = BASE_URL + "appid=" + API_KEY + "&q=" + CITY
print(url)
response = requests.get(url).json()
print(response)

temp_kelvin = response['main']['temp']
temp_celsius, temp_fahrenheit=kelvin_to_celsius_fahrenheit(temp_kelvin)
feels_like_kelvin = response['main']['feels_like']
feels_like_celsius, feels_like_fahrenheit = kelvin_to_celsius_fahrenheit (feels_like_kelvin)
wind_speed = response['wind']['speed']
humidity = response['main']['humidity']
description = response['weather'][0]['description']
sunrise_time = dt.datetime.utcfromtimestamp (response['sys']['sunrise'] + response['timezone'])
sunset_time = dt.datetime.utcfromtimestamp (response['sys']['sunset'] + response['timezone'])
print(f"Temperature in {CITY}: {temp_celsius:.2f}°C or {temp_fahrenheit:.2f}°F")
print (f"Temperature in {CITY} feels like: {feels_like_celsius:.2f}°C or {feels_like_fahrenheit}°F")
print(f"Humidity in {CITY}: {humidity}%")
print (f"Wind Speed in {CITY}: {wind_speed}m/s")
print(f"General Weather in {CITY}: {description}")
print (f"Sun rises in {CITY} at {sunrise_time} local time.")
print (f"Sun sets in {CITY} at {sunset_time} local time.")

# test
exclude = "minute,hourly"
lat = 14.2972
lon = 109.0797
url2 = f'https://api.openweather.map.org/data/2.5/onecall?lat={lat}&lon={lon}&exclude={exclude}&appid{API_KEY}'
print(url2)
response2 = requests.get(url2).json()
print(response2)
# test 2
# city_name = "berlin" #you can ask for user input instead
#
# #Let's get the city's coordinates (lat and lon)
# url = f'https://api.openweathermap.org/data/2.5/weather?q={city_name}&appid={API_KEY}'
# print(url)
#
# #Let's parse the Json
# req = requests.get(url)
# data = req.json()
#
# #Let's get the name, the longitude and latitude
# name = data['name']
# lon = data['coord']['lon']
# lat = data['coord']['lat']
#
# print(name, lon, lat)
#
# exclude = "minute,hourly"
#
# url2 = f'https://api.openweather.map.org/data/2.5/onecall?lat={lat}&lon={lon}&exclude={exclude}&appid{API_KEY}'
# print(url2)
#
# # Let's now parse the JSON
# req2 = requests.get(url2)
# data2 = req2.json()
# print(data2)
#
# # Let's now get the temp for the day, the night and the weather conditions
# days = []
# nights = []
# descr = []
#
# # We need to access 'daily'
# for i in data2['daily']:
#     # We notice that the temperature is in Kelvin, so we need to do -273.15 for every datapoint
#
#     # Let's start by days
#     # Let's round the decimal numbers to 2
#     days.append(round(i['temp']['day'] - 273.15, 2))
#
#     # Nights
#     nights.append(round(i['temp']['night'] - 273.15, 2))
#
#     # Let's now get the weather condition and the description
#     # 'weather' [0] 'main' + 'weather' [0] 'description'
#     descr.append(i['weather'][0]['main'] + ": " + i['weather'][0]['description'])
#
# print(days)
# print(nights)
# print(descr)
#
# string = f'[ {name} - 8 days forecast]\n'
#
# # Let's now loop for as much days as there available (8 in this case):
# for i in range(len(days)):
#
#     # We want to print out the day (day1,2,3,4..)
#     # Also, day 1 = today and day 2 = tomorrow for reference
#
#     if i == 0:
#         string += f'\nDay {i + 1} (Today)\n'
#
#     elif i == 1:
#         string += f'\nDay {i + 1} (Tomorrow)\n'
#
#     else:
#         string += f'\nDay {i + 1}\n'
#
#     string += 'Morning:' + str(days[i]) + '°C' + "\n"
#     string += 'Night:' + str(nights[i]) + '°C' + "\n"
#     string += 'Conditions:' + descr[i] + '\n'
#
# print(string)
