package com.example.wigellgymservice.services.externalAPI;

import com.example.wigellgymservice.models.CurrencyConversionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CurrencyConverterImpl implements CurrencyConverter {

    @Override
    public double sekToEuroConverter(double sek) {
        try {

            String uri = "https://v1.apiplugin.io/v1/currency/mki37XKl/convert?amount=" + sek + "&from=SEK&to=EUR";
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
