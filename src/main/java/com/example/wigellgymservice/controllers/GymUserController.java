package com.example.wigellgymservice.controllers;

import com.example.wigellgymservice.models.DTO.DTOGymBooking;
import com.example.wigellgymservice.models.entities.GymWorkout;
import com.example.wigellgymservice.services.GymBookingServiceImpl;
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

    private GymBookingServiceImpl gymBookingService;
    private GymWorkoutServiceImpl gymWorkoutService;

    @Autowired
    public GymUserController(GymBookingServiceImpl gymBookingService,GymWorkoutServiceImpl gymWorkoutServiceImpl) {
        this.gymBookingService = gymBookingService;
        this.gymWorkoutService = gymWorkoutServiceImpl;
    }


    //• Lista träningspass GET /api/wigellgym/workouts
    @GetMapping("/workouts")
    public ResponseEntity<List<GymWorkout>> getAllWorkouts(){
        return ResponseEntity.ok(gymWorkoutService.getAllGymWorkouts());
    }

    //• Boka träningspass POST /api/wigellgym/bookworkout
    //TODO: Should I have dateTime here or set it on something else?
    @PostMapping("/bookworkout/{workoutId}")
    public ResponseEntity<DTOGymBooking> bookWorkOut(Authentication authentication, @PathVariable Long workoutId){
        return new ResponseEntity<>(gymBookingService.bookWorkout(authentication,workoutId), HttpStatus.CREATED);
    }

    //• Avboka träningspass PUT /api/wigellgym/cancelworkout (fram tills en dag innan avsatt datum)
    @PutMapping("/cancelworkout/{bookingId}")
    public ResponseEntity<String> cancelBooking(Authentication authentication, @PathVariable Long bookingId) {
        return ResponseEntity.ok(gymBookingService.cancelBookingOnWorkout(authentication, bookingId));
    }

    //• Se tidigare och aktiva bokningar GET /api/wigellgym/mybookings
    @GetMapping("/mybookings")
    public ResponseEntity<List<DTOGymBooking>> userBookings (Principal principal){
        return ResponseEntity.ok(gymBookingService.getUserGymBookings(principal.getName()));
    }


}
