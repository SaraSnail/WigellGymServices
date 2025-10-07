package com.example.wigellgymservice.controllers;

import com.example.wigellgymservice.models.DTO.DTOGymBooking;
import com.example.wigellgymservice.models.DTO.DTOGymInstructor;
import com.example.wigellgymservice.models.DTO.DTOGymWorkout;
import com.example.wigellgymservice.models.entities.GymInstructor;
import com.example.wigellgymservice.models.entities.GymWorkout;
import com.example.wigellgymservice.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wigellgym")
@PreAuthorize("hasRole('ADMIN')")
public class GymAdminController {

    private final GymBookingService gymBookingService;
    private final GymInstructorService gymInstructorService;
    private final GymWorkoutService gymWorkoutService;

    @Autowired
    public GymAdminController(GymBookingService gymBookingService, GymInstructorService gymInstructorService, GymWorkoutService gymWorkoutService) {
        this.gymBookingService = gymBookingService;
        this.gymInstructorService = gymInstructorService;
        this.gymWorkoutService = gymWorkoutService;
    }


    @GetMapping("/listcanceled")
    public ResponseEntity<List<DTOGymBooking>> listCanceled() {
        return ResponseEntity.ok(gymBookingService.getCancelledGymBookings());
    }

    @GetMapping("/listupcoming")
    public ResponseEntity<List<DTOGymBooking>> listUpcoming() {
        return ResponseEntity.ok(gymBookingService.upComingGymBookings());
    }

    @GetMapping("/listpast")
    public ResponseEntity<List<DTOGymBooking>> listPast() {
        return ResponseEntity.ok(gymBookingService.pastGymBookings());
    }


    @PostMapping("/addworkout/{instructorId}")
    public ResponseEntity<GymWorkout> addWorkout(@RequestBody DTOGymWorkout dtoGymWorkout, @PathVariable Long instructorId, Authentication authentication) {
        return new ResponseEntity<>(gymWorkoutService.addGymWorkout(dtoGymWorkout,instructorId, authentication),HttpStatus.CREATED);
    }

    @PutMapping("/updateworkout/{workoutId}/{instructorId}")
    public ResponseEntity<GymWorkout> updateWorkout(@RequestBody DTOGymWorkout dtoGymWorkout, @PathVariable Long workoutId, @PathVariable Long instructorId, Authentication authentication) {
        return ResponseEntity.ok(gymWorkoutService.updateGymWorkout(dtoGymWorkout, workoutId, instructorId, authentication));
    }

    @PutMapping("/remworkout/{id}")
    public ResponseEntity<String> removeWorkout(@PathVariable Long id,Authentication authentication) {
        return ResponseEntity.ok(gymWorkoutService.removeGymWorkout(id,authentication));
    }

    @PostMapping("/addinstructor")
    public ResponseEntity<GymInstructor> addInstructor(@RequestBody DTOGymInstructor dtoGymInstructor, Authentication authentication) {
        return new ResponseEntity<>(gymInstructorService.addGymInstructor(dtoGymInstructor, authentication), HttpStatus.CREATED);
    }


}
