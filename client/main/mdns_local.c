#include <netinet/in.h>
#include "mdns.h"


int resolve_mdns_host(const char * host_name, char ip[16]) {
    struct ip4_addr addr;
    addr.addr = 0;

    esp_err_t err = mdns_query_a(host_name, 2000,  &addr);
    if(err){
        if(err == ESP_ERR_NOT_FOUND){
            printf("Host was not found!");
            return err;
        }
        printf("Query Failed");
        return err;
    }
    sprintf(ip, IPSTR, IP2STR(&addr));
    return 0;
}
