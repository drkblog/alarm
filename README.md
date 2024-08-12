# Google Calendar alarm

Personal project to build a physical alarm for Google Calendar.

## Background

Sometime it's handy to be alerted of incoming meetings.
Specially if for any reason you cannot hear the notification sound from your computer.
In this case an external device that you can sit on your desktop and produces some sort of alarm can be a solution.
For this reason I built a small device based on a `XH-C2X` module powered by a `ESP32C2`/`ESP8684H4` 2.4GHz WiFi/BLE module.

## Server

It's a Spring boot application acting as a RESTful server exposing an endpoint.
The client can query the endpoint in order to know if the alarm should be enabled.
Once the server returns a `true` for a calendar event it won't take the event into account in the following requests.
The events for which a `true` is returned depend on the server logic.

## Client

It's a C application built using ESP-IDF framework.
It's configured during the build with a server hostname and a WiFi network and password.
On start the client connects to the network, resolves the hostname using **mDNS** and starts polling the server.
If the server returns 'true' the client activates the alarm for some (configurable) time.

## Hardware

The schematic [can be viewed here](hardware/schematic.png). It consists of the following high-level modules:
- XH-C2X module containing the ESP32C2 chip, antenna, crystal oscillator and a few other passives
- TP4056 battery charger (with protection)
- HT7333 low-dropout 3.3v regulator
- A transistor, LED and a couple of resistors to activate the vibration motor

No PCB has been designed, instead components were mounted into a perfboard and connected with enameled wire. 
