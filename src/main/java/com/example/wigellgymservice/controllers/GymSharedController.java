package com.example.wigellgymservice.controllers;

import com.example.wigellgymservice.models.entities.GymInstructor;
import com.example.wigellgymservice.services.GymInstructorService;
import com.example.wigellgymservice.services.GymInstructorServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/wigellgym")
@PreAuthorize("hasAnyRole('USER','ADMIN')")
public class GymSharedController {

    private final GymInstructorService gymInstructorService;

    @Autowired
    public GymSharedController(GymInstructorService gymInstructorService) {
        this.gymInstructorService = gymInstructorService;
    }


    @GetMapping("/instructors")
    public ResponseEntity<List<GymInstructor>> getAllInstructors(Authentication authentication) {
        return ResponseEntity.ok(gymInstructorService.getAllGymInstructors(authentication));
    }


}
