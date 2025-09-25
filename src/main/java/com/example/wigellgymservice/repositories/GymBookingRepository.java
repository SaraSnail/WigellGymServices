package com.example.wigellgymservice.repositories;

import com.example.wigellgymservice.models.entities.GymBooking;
import com.example.wigellgymservice.models.entities.GymCustomer;
import com.example.wigellgymservice.models.entities.GymWorkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GymBookingRepository extends JpaRepository<GymBooking, Long> {

    List<GymBooking> findGymBookingsByGymWorkout(GymWorkout gymWorkoutId);
    List<GymBooking> findAllByIsActiveTrueAndGymWorkout(GymWorkout gymWorkout);

    List<GymBooking> findAllByIsActiveTrueAndGymCustomer(GymCustomer gymCustomer);
    List<GymBooking> findAllByIsActive(boolean isActive);

}
