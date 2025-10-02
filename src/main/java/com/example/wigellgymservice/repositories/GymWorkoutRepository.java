package com.example.wigellgymservice.repositories;

import com.example.wigellgymservice.models.entities.GymBooking;
import com.example.wigellgymservice.models.entities.GymInstructor;
import com.example.wigellgymservice.models.entities.GymWorkout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GymWorkoutRepository extends JpaRepository<GymWorkout, Long> {
    List<GymWorkout> findAllByDateTimeAfterAndIsActiveTrue(LocalDateTime dateTime);
    List<GymWorkout> findAllByDateTimeBefore(LocalDateTime dateTime);
    List<GymWorkout> findAllByGymInstructorAndIsActiveTrue(GymInstructor gymInstructor);
}
