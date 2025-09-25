package com.example.wigellgymservice.services;

import com.example.wigellgymservice.models.DTO.DTOGymInstructor;
import com.example.wigellgymservice.models.entities.GymInstructor;
import org.springframework.security.core.Authentication;

import java.security.Principal;
import java.util.List;

public interface GymInstructorService {

    List<GymInstructor> getAllGymInstructors();
    GymInstructor addGymInstructor(DTOGymInstructor dtoGymInstructor, Principal principal, Authentication authentication);
}
