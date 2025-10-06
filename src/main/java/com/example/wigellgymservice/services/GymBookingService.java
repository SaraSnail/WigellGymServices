package com.example.wigellgymservice.services;

import com.example.wigellgymservice.models.DTO.DTOGymBooking;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface GymBookingService {

    //User
    DTOGymBooking bookWorkout(Authentication authentication, Long workoutId);
    String cancelBookingOnWorkout(Authentication authentication, Long bookingId);
    List<DTOGymBooking> getUserGymBookings(String username);

    //Admin
    List<DTOGymBooking> getCancelledGymBookings();
    List<DTOGymBooking> upComingGymBookings();
    List<DTOGymBooking> pastGymBookings();
}
