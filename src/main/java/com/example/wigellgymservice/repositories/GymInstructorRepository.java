package com.example.wigellgymservice.repositories;

import com.example.wigellgymservice.models.entities.GymInstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GymInstructorRepository extends JpaRepository<GymInstructor, Long> {

}
