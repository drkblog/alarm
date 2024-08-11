/*
 * SPDX-FileCopyrightText: 2022 Espressif Systems (Shanghai) CO LTD
 *
 * SPDX-License-Identifier: Unlicense OR CC0-1.0
 */
#include "sdkconfig.h"
#include <string.h>
#include <unistd.h>
#include <sys/socket.h>
#include <errno.h>
#include <netdb.h>            // struct addrinfo
#include <arpa/inet.h>
#include "esp_netif.h"
#include "esp_log.h"
#if defined(CONFIG_EXAMPLE_SOCKET_IP_INPUT_STDIN)
#include "addr_from_stdin.h"
#endif

static const char *TAG = "ESP32C2-TCP-CLIENT";
static const char *payload_template = "GET /calendar/status HTTP/1.1\r\nHost: %s:%u\r\nConnection: close\r\n\r\n";

#define RX_BUFFER_SIZE 256
#define PAYLOAD_BUFFER_SIZE 256

bool get_boolean_from(const char * str) {
    char * message_start = strstr(str, "\r\n\r\n");
    message_start = strstr(message_start + 4, "\r\n");
    message_start += 2;
    ESP_LOGV(TAG, "Boolean: %c", message_start[0]);
    return message_start[0] == 't';
}

bool poll_status(const char * host_ip, uint16_t port)
{
    char rx_buffer[RX_BUFFER_SIZE];
    int addr_family = 0;
    int ip_protocol = 0;

    struct sockaddr_in dest_addr;
    inet_pton(AF_INET, host_ip, &dest_addr.sin_addr);
    dest_addr.sin_family = AF_INET;
    dest_addr.sin_port = htons(port);
    addr_family = AF_INET;
    ip_protocol = IPPROTO_IP;

    int sock =  socket(addr_family, SOCK_STREAM, ip_protocol);
    if (sock < 0) {
        ESP_LOGE(TAG, "Unable to create socket: errno %d", errno);
        return false;
    }
    ESP_LOGD(TAG, "Socket created, connecting to %s:%d", host_ip, port);

    int err = connect(sock, (struct sockaddr *)&dest_addr, sizeof(dest_addr));
    if (err != 0) {
        ESP_LOGE(TAG, "Socket unable to connect: errno %d", errno);
        close(sock);
        return false;
    }
    ESP_LOGI(TAG, "Successfully connected");

    char payload[PAYLOAD_BUFFER_SIZE];
    snprintf(payload, PAYLOAD_BUFFER_SIZE - 1, payload_template, host_ip, port);
    err = send(sock, payload, strlen(payload), 0);
    if (err < 0) {
        ESP_LOGE(TAG, "Error occurred during sending: errno %d", errno);
        shutdown(sock, 0);
        close(sock);
        return false;
    }

    int len = recv(sock, rx_buffer, RX_BUFFER_SIZE - 1, 0);
    // Error occurred during receiving
    if (len < 0) {
        ESP_LOGE(TAG, "recv failed: errno %d", errno);
        shutdown(sock, 0);
        close(sock);
        return false;
    }
    else {
        rx_buffer[len] = 0; // Null-terminate whatever we received and treat like a string
        ESP_LOGD(TAG, "Received %d bytes from %s:", len, host_ip);
        ESP_LOGV(TAG, "%s", rx_buffer);
    }

    ESP_LOGI(TAG, "Shutting down socket...");
    shutdown(sock, 0);
    close(sock);

    return get_boolean_from(rx_buffer);
}

