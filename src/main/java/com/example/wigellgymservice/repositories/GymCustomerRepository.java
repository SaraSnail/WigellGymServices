package com.example.wigellgymservice.repositories;

import com.example.wigellgymservice.models.entities.GymCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GymCustomerRepository extends JpaRepository<GymCustomer,Long> {
    GymCustomer findByUsername(String name);
}
