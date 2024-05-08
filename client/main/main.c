/*
 * Alarm
 */
#include <unistd.h>

#include "nvs_flash.h"
#include "esp_netif.h"
#include "esp_event.h"
#include "mdns.h"

#include "mdns_local.h"
#include "alarm_client.h"
#include "wifi.h"

#include "config.h"

#define THIRTHY_SECONDS 30

void app_main(void)
{
    ESP_ERROR_CHECK(nvs_flash_init());
    ESP_ERROR_CHECK(esp_netif_init());
    ESP_ERROR_CHECK(esp_event_loop_create_default());

    if (wifi_connect() != ESP_OK) {
        printf("Cannot start WiFi");
        return;
    }
    ESP_ERROR_CHECK(esp_register_shutdown_handler(&wifi_shutdown));

    if (mdns_init() != ESP_OK) {
        printf("Unable to initialize mDNS protocol");
        return;
    }

    char ip[16];
    int err;
    if ((err = resolve_mdns_host(SERVER_HOSTNAME, ip)) != 0) {
        printf("Cannot find server host");
        return;
    }

    while (1) {
        bool status = poll_status(ip);
        sleep(THIRTHY_SECONDS);
    }
}
