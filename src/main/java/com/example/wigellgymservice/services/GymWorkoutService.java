package com.example.wigellgymservice.services;

import com.example.wigellgymservice.models.DTO.DTOGymWorkout;
import com.example.wigellgymservice.models.entities.GymWorkout;
import org.springframework.security.core.Authentication;

import java.security.Principal;
import java.util.List;

public interface GymWorkoutService {

    //TODO: might change so it returns Objects instead of DTOs
    List<GymWorkout> getAllGymWorkouts();
    GymWorkout addGymWorkout(DTOGymWorkout dtoGymWorkout, Long instructorId, Authentication authentication);
    GymWorkout updateGymWorkout(DTOGymWorkout dtoGymWorkout, Long workoutId, Long instructorId, Authentication authentication);
    String removeGymWorkout(Long gymWorkoutId, Authentication authentication);
}
