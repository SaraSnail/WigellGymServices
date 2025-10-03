package com.example.wigellgymservice.controllers;

import com.example.wigellgymservice.models.DTO.DTOGymBooking;
import com.example.wigellgymservice.models.DTO.DTOGymInstructor;
import com.example.wigellgymservice.models.DTO.DTOGymWorkout;
import com.example.wigellgymservice.models.entities.GymInstructor;
import com.example.wigellgymservice.models.entities.GymWorkout;
import com.example.wigellgymservice.services.GymBookingServiceImpl;
import com.example.wigellgymservice.services.GymInstructorServiceImpl;
import com.example.wigellgymservice.services.GymWorkoutServiceImpl;
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

    private final GymBookingServiceImpl gymBookingService;
    private final GymInstructorServiceImpl gymInstructorService;
    private final GymWorkoutServiceImpl gymWorkoutService;

    @Autowired
    public GymAdminController(GymBookingServiceImpl gymBookingService, GymInstructorServiceImpl gymInstructorService, GymWorkoutServiceImpl gymWorkoutService) {
        this.gymBookingService = gymBookingService;
        this.gymInstructorService = gymInstructorService;
        this.gymWorkoutService = gymWorkoutService;
    }


    //• Lista avbokningar: GET /api/wigellgym/listcanceled
    @GetMapping("/listcanceled")
    public ResponseEntity<List<DTOGymBooking>> listCanceled() {
        return ResponseEntity.ok(gymBookingService.getCancelledGymBookings());
    }

    //• Lista kommande bokningar: GET /api/wigellgym/listupcoming
    @GetMapping("/listupcoming")
    public ResponseEntity<List<DTOGymBooking>> listUpcoming() {
        return ResponseEntity.ok(gymBookingService.upComingGymBookings());
    }

    //• Lista historiska bokningar: GET /api/wigellgym/listpast
    @GetMapping("/listpast")
    public ResponseEntity<List<DTOGymBooking>> listPast() {
        return ResponseEntity.ok(gymBookingService.historicalGymBookings());
    }


    //• Lägg till träningspass: POST /api/wigellgym/addworkout
    @PostMapping("/addworkout/{instructorId}")
    public ResponseEntity<GymWorkout> addWorkout(@RequestBody DTOGymWorkout dtoGymWorkout, @PathVariable Long instructorId, Authentication authentication) {
        return new ResponseEntity<>(gymWorkoutService.addGymWorkout(dtoGymWorkout,instructorId, authentication),HttpStatus.CREATED);
    }

    //• Uppdatera träningspass: PUT /api/wigellgym/updateworkout
    @PutMapping("/updateworkout/{workoutId}/{instructorId}")
    public ResponseEntity<GymWorkout> updateWorkout(@RequestBody DTOGymWorkout dtoGymWorkout, @PathVariable Long workoutId, @PathVariable Long instructorId, Authentication authentication) {
        return ResponseEntity.ok(gymWorkoutService.updateGymWorkout(dtoGymWorkout, workoutId, instructorId, authentication));
    }

    //TODO: not tested. Change to a Put and don't remove the workout
    //• Radera träningspass: DELETE /api/wigellgym/remworkout/{id}
    @PutMapping("/remworkout/{id}")
    public ResponseEntity<String> removeWorkout(@PathVariable Long id,Authentication authentication) {
        return ResponseEntity.ok(gymWorkoutService.removeGymWorkout(id,authentication));
    }

    //• Lägg till instruktör: POST /api/wigellgym/addinstructor
    @PostMapping("/addinstructor")
    public ResponseEntity<GymInstructor> addInstructor(@RequestBody DTOGymInstructor dtoGymInstructor, Authentication authentication) {
        return new ResponseEntity<>(gymInstructorService.addGymInstructor(dtoGymInstructor, authentication), HttpStatus.CREATED);
    }


}
