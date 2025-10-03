package com.example.wigellgymservice.services.util;

import com.example.wigellgymservice.enums.TrainingType;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class validateTrainingType {

    public static boolean validTrainingType(String type){
        try {
            TrainingType.valueOf(type);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    public static TrainingType getTrainingType(String type){
        String input = type.toUpperCase();

        if(!validTrainingType(input)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "'"+type + "' is an invalid training type");
        }
        return TrainingType.valueOf(input);
    }
}
