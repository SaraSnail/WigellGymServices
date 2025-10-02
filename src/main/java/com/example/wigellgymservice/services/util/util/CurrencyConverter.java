package com.example.wigellgymservice.services.util.util;

import com.example.wigellgymservice.models.CurrencyConversionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

public class CurrencyConverter {

    public static double sekToEuroConverter(double sek) {
        if(sek < 40){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price sek cannot be less than 40 (the booking fee)");
        }

        try {

            String uri = "https://v1.apiplugin.io/v1/currency/MikSPFWu/convert?amount=" + sek + "&from=SEK&to=EUR";
            RestTemplate restTemplate = new RestTemplate();

            CurrencyConversionResponse response = restTemplate.getForObject(uri, CurrencyConversionResponse.class);

            if(response == null){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Could not convert");
            }

            return response.getResult();

        }catch (Exception e){
            e.printStackTrace();
            return 0.0;
        }
    }
}
