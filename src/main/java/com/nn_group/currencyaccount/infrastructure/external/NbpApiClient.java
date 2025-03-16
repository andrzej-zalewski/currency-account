package com.nn_group.currencyaccount.infrastructure.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "nbp-api", url = "${nbp.api.base-url}")
public interface NbpApiClient {
    @GetMapping("/rates/A/{currency}?format=json")
    NbpExchangeRateResponse getExchangeRate(@PathVariable("currency") String currency);
}