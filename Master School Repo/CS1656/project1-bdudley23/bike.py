import argparse
import collections
import csv
import json
import glob
import math
import os
import pandas
import re
import requests
import string
import sys
import time
import xml

class Bike():
    def __init__(self, baseURL, station_info, station_status):
        # initialize the instance
        station_infoURL = baseURL+station_info
        station_statusURL = baseURL+station_status
        self.response1 = requests.get(station_infoURL, verify=False)
        self.information = json.loads(self.response1.content)
        self.response2 = requests.get(station_statusURL, verify=False)
        self.status = json.loads(self.response2.content)
        pass

    def total_bikes(self):
        # return the total number of bikes available
        numBikes = 0
        station_bikes = self.status.get('data')
        for i in range(0, len(station_bikes['stations'])):
            numBikes = (numBikes + station_bikes['stations'][i]['num_bikes_available'])
        return numBikes

    def total_docks(self):
        # return the total number of docks available
        station_docks = self.status.get('data')
        numDocks = 0
        for i in range(0, len(station_docks['stations'])):
            numDocks = (numDocks + station_docks['stations'][i]['num_docks_available'])
        return numDocks

    def percent_avail(self, station_id):
        # return the percentage of available docks
        station_docks = self.status.get('data')
        for i in range(0, len(station_docks['stations'])):
            if int(station_docks['stations'][i]['station_id']) == station_id:
                numBikes = station_docks['stations'][i]['num_bikes_available']
                numDocks = station_docks['stations'][i]['num_docks_available']
                break
        percent = str(int((numDocks / (numBikes + numDocks)) * 100)) + '%'
        return percent

    def closest_stations(self, latitude, longitude):
        # return the stations closest to the given coordinates
        station_locations = self.information.get('data')
        numStations = len(station_locations['stations'])

        list_distance = []
        for i in range(0, numStations):
            list_distance.append(self.distance(latitude, longitude, station_locations['stations'][i]['lat'], station_locations['stations'][i]['lon']))

        list_distance_sorted = list_distance.copy()
        list_distance_sorted.sort()
        closest_distance1 = min(list_distance_sorted)
        list_distance_sorted.pop(0)
        closest_distance2 = min(list_distance_sorted)
        list_distance_sorted.pop(0)
        closest_distance3 = min(list_distance_sorted)
        list_distance_sorted.pop(0)
        closest_index1 = list_distance.index(closest_distance1)
        closest_index2 = list_distance.index(closest_distance2)
        closest_index3 = list_distance.index(closest_distance3)

        loc_dict = {station_locations['stations'][closest_index1]['station_id']: station_locations['stations'][closest_index1]['name'],
                    station_locations['stations'][closest_index2]['station_id']: station_locations['stations'][closest_index2]['name'],
                    station_locations['stations'][closest_index3]['station_id']: station_locations['stations'][closest_index3]['name']}

        return loc_dict


    def closest_bike(self, latitude, longitude):
        # return the station with available bikes closest to the given coordinates
        station_locations = self.information.get('data')
        station_status = self.status.get('data')
        numStations = len(station_locations['stations'])

        list_distance = []
        for i in range(0, numStations):
            list_distance.append(self.distance(latitude, longitude, station_locations['stations'][i]['lat'],
                                               station_locations['stations'][i]['lon']))

        list_distance_sorted = list_distance.copy()

        closest_distance1 = min(list_distance_sorted)
        closest_index1 = list_distance.index(closest_distance1)
        while station_status['stations'][closest_index1]['num_bikes_available'] == 0:
            list_distance_sorted.pop(0)
            closest_distance1 = min(list_distance_sorted)
            closest_index1 = list_distance.index(closest_distance1)
        list_distance_sorted.pop(0)

        closest_distance2 = min(list_distance_sorted)
        closest_index2 = list_distance.index(closest_distance2)
        while station_status['stations'][closest_index2]['num_bikes_available'] == 0:
            list_distance_sorted.pop(0)
            closest_distance2 = min(list_distance_sorted)
            closest_index2 = list_distance.index(closest_distance2)
        list_distance_sorted.pop(0)

        closest_distance3 = min(list_distance_sorted)
        closest_index3 = list_distance.index(closest_distance3)
        while station_status['stations'][closest_index3]['num_bikes_available'] == 0:
            list_distance_sorted.pop(0)
            closest_distance3 = min(list_distance_sorted)
            closest_index3 = list_distance.index(closest_distance3)

        loc_dict = {
            station_locations['stations'][closest_index1]['station_id']: station_locations['stations'][closest_index1][
                'name'],
            station_locations['stations'][closest_index2]['station_id']: station_locations['stations'][closest_index2][
                'name'],
            station_locations['stations'][closest_index3]['station_id']: station_locations['stations'][closest_index3][
                'name']}

        return loc_dict
        
    def station_bike_avail(self, latitude, longitude):
        # return the station id and available bikes that correspond to the station with the given coordinates
        station_locations = self.information.get('data')
        station_status = self.status.get('data')
        numStations = len(station_locations['stations'])

        result = {}
        for i in range(0, numStations):
            if station_locations['stations'][i]['lat'] == latitude and station_locations['stations'][i]['lon'] == longitude:
                result[station_status['stations'][i]['station_id']] = station_status['stations'][i]['num_bikes_available']
        return result
        

    def distance(self, lat1, lon1, lat2, lon2):
        p = 0.017453292519943295
        a = 0.5 - math.cos((lat2-lat1)*p)/2 + math.cos(lat1*p)*math.cos(lat2*p) * (1-math.cos((lon2-lon1)*p)) / 2
        return 12742 * math.asin(math.sqrt(a))


# testing and debugging the Bike class

if __name__ == '__main__':
    instance = Bike('https://api.nextbike.net/maps/gbfs/v1/nextbike_pp/en', '/station_information.json', '/station_status.json')
    print('------------------total_bikes()-------------------')
    t_bikes = instance.total_bikes()
    print(type(t_bikes))
    print(t_bikes)
    print()

    print('------------------total_docks()-------------------')
    t_docks = instance.total_docks()
    print(type(t_docks))
    print(t_docks)
    print()

    print('-----------------percent_avail()------------------')
    p_avail = instance.percent_avail(342885) # replace with station ID
    print(type(p_avail))
    print(p_avail)
    print()

    print('----------------closest_stations()----------------')
    c_stations = instance.closest_stations(40.447525, -80.008528) # replace with latitude and longitude
    print(type(c_stations))
    print(c_stations)
    print()

    print('-----------------closest_bike()-------------------')
    c_bike = instance.closest_bike(40.444618, -79.954707) # replace with latitude and longitude
    print(type(c_bike))
    print(c_bike)
    print()

    print('---------------station_bike_avail()---------------')
    s_bike_avail = instance.station_bike_avail(40.444777, -80.000831) # replace with exact latitude and longitude of station
    print(type(s_bike_avail))
    print(s_bike_avail)
