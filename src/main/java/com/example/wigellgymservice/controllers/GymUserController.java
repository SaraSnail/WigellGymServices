package com.example.wigellgymservice.controllers;

import com.example.wigellgymservice.models.DTO.DTOGymBooking;
import com.example.wigellgymservice.models.entities.GymWorkout;
import com.example.wigellgymservice.services.GymBookingService;
import com.example.wigellgymservice.services.GymBookingServiceImpl;
import com.example.wigellgymservice.services.GymWorkoutService;
import com.example.wigellgymservice.services.GymWorkoutServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/wigellgym")
@PreAuthorize("hasRole('USER')")
public class GymUserController {

    private GymBookingService gymBookingService;
    private GymWorkoutService gymWorkoutService;

    @Autowired
    public GymUserController(GymBookingService gymBookingService, GymWorkoutService gymWorkoutServiceImpl) {
        this.gymBookingService = gymBookingService;
        this.gymWorkoutService = gymWorkoutServiceImpl;
    }


    @GetMapping("/workouts")
    public ResponseEntity<List<GymWorkout>> getAllWorkouts(){
        return ResponseEntity.ok(gymWorkoutService.getAllGymWorkouts());
    }

    @GetMapping("/mybookings")
    public ResponseEntity<List<DTOGymBooking>> userBookings (Principal principal){
        return ResponseEntity.ok(gymBookingService.getUserGymBookings(principal.getName()));
    }

    @PostMapping("/bookworkout/{workoutId}")
    public ResponseEntity<DTOGymBooking> bookWorkOut(Authentication authentication, @PathVariable Long workoutId){
        return new ResponseEntity<>(gymBookingService.bookWorkout(authentication,workoutId), HttpStatus.CREATED);
    }

    @PutMapping("/cancelworkout/{bookingId}")
    public ResponseEntity<String> cancelBooking(Authentication authentication, @PathVariable Long bookingId) {
        return ResponseEntity.ok(gymBookingService.cancelBookingOnWorkout(authentication, bookingId));
    }


}
