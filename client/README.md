| Supported Targets | ESP32 | ESP32-C2 | ESP32-C3 | ESP32-C6 | ESP32-H2 | ESP32-S2 | ESP32-S3 | Linux |
| ----------------- | ----- | -------- | -------- | -------- | -------- | -------- | -------- | ----- |


# Alarm ESP32 client

Client software for polling the Calendar server

## Configure the project
This example can be configured to run on ESP32 and Linux target to communicate over IPv4 and IPv6.

```
idf.py menuconfig
```

Set following parameters under ```Example Configuration``` Options:

* Set `IP version` of example to be IPV4 or IPV6.

* Set `IPV4 Address` in case your chose IP version IPV4 above.

* Set `IPV6 Address` in case your chose IP version IPV6 above.
    * For IPv6 there's an additional option for ```Interface selection```.
    * Enter the name of the interface to explicitely establish communication over a specific interface.
    * On selecting ```Auto``` the example will find the first interface with an IPv6 address and use it.

* Set `Port` number that represents remote port the example will connect to.
