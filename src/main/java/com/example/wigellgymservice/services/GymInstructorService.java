package com.example.wigellgymservice.services;

import com.example.wigellgymservice.models.DTO.DTOGymInstructor;
import com.example.wigellgymservice.models.entities.GymInstructor;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface GymInstructorService {

    List<GymInstructor> getAllGymInstructors(Authentication authentication);
    GymInstructor addGymInstructor(DTOGymInstructor dtoGymInstructor, Authentication authentication);
}
