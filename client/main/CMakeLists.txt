idf_build_get_property(target IDF_TARGET)

if(${target} STREQUAL "linux")
    set(requires esp_event esp_stubs protocol_examples_common nvs_flash)
endif()

idf_component_register(SRCS "tcp_client_v4.c" "main.c" "mdns_local.c" "wifi.c"
                                INCLUDE_DIRS "."
                                REQUIRES ${requires})
