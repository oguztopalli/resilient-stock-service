package com.oguz.resilientstockservice.controller;

import com.oguz.resilientstockservice.service.ExternalStockService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final ExternalStockService externalStockService;

    @GetMapping("/{symbol}")
    public String getPrice(@PathVariable String symbol) {
        return externalStockService.getStockPrice(symbol);
    }

    @GetMapping("/api/stocks/{symbol}")
    public String getStock(@PathVariable String symbol) {
        return externalStockService.getStockPrice(symbol);
    }
}
