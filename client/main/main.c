/*
 * Alarm
 */
#include <unistd.h>

#include "driver/ledc.h"

#include "nvs_flash.h"
#include "esp_netif.h"
#include "esp_event.h"
#include "mdns.h"

#include "mdns_local.h"
#include "alarm_client.h"
#include "wifi.h"
#include "esp_log.h"

#include "config.h"

#define PULSE_OFF 0
static const char *TAG = "ESP32C2-ALARM";

static uint8_t s_alarm_state = 0;

static void alarm_pulse(bool alarm_state)
{
    if (alarm_state) {
        gpio_set_level(ALARM_OUTPUT_IO, s_alarm_state);
    } else {
        gpio_set_level(ALARM_OUTPUT_IO, PULSE_OFF);
    }
}

static void configure_alarm(void)
{
    gpio_reset_pin(ALARM_OUTPUT_IO);
    gpio_set_direction(ALARM_OUTPUT_IO, GPIO_MODE_OUTPUT);
}


void app_main(void)
{
    esp_log_level_set("*", ESP_LOG_INFO);
    esp_log_level_set(TAG, ESP_LOG_VERBOSE);
    esp_log_level_set("ESP32C2-TCP-CLIENT", ESP_LOG_VERBOSE);

    ESP_ERROR_CHECK(nvs_flash_init());
    ESP_ERROR_CHECK(esp_netif_init());
    ESP_ERROR_CHECK(esp_event_loop_create_default());

    configure_alarm();

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

    uint8_t loop_count = 0;
    bool status = false;
    while (1) {
        if (loop_count % (POLL_PERIOD_MS / LOOP_PERIOD_MS) == 0) {
            status = poll_status(ip, SERVER_PORT);
            ESP_LOGI(TAG, "Status: %s", status ? "true" : "false");
        }
        if (loop_count % (BLINK_PERIOD_MS / LOOP_PERIOD_MS) == 0) {
            s_alarm_state = !s_alarm_state;
            ESP_LOGI(TAG, "Blink: %s", s_alarm_state ? "true" : "false");
        }
        
        alarm_pulse(status);

        // Sleep 100ms (In theory, because the clock may be wrong)
        vTaskDelay(LOOP_PERIOD_MS / portTICK_PERIOD_MS);

        loop_count++;
    }
}
