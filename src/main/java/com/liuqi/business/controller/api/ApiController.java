package com.liuqi.business.controller.api;

import com.liuqi.business.service.OpenApiService;
import com.liuqi.response.APIResult;
import com.liuqi.response.ReturnResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/market")
public class ApiController {

    private OpenApiService openApiService;

    @Autowired
    public ApiController(OpenApiService openApiService) {
        this.openApiService = openApiService;
    }

    @GetMapping(value = "symbols")
    public APIResult symbols() {
        return openApiService.symbols();
    }

    @GetMapping(value = "trade")
    public APIResult trade(String symbol) {
        return openApiService.trade(symbol);
    }

    @GetMapping(value = "depth")
    public APIResult depth(String symbol,Integer gear) {
        return openApiService.depth(symbol,gear);
    }

    @GetMapping(value = "quotation")
    public APIResult quotation(String symbol) {
        return openApiService.quotation(symbol);
    }





}
