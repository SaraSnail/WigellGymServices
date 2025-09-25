package com.example.wigellgymservice.services.util.util;

import com.example.wigellgymservice.enums.TrainingType;

public class Util {

    public static boolean validTrainingType(String type){
        try {
            TrainingType.valueOf(type);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }
}
