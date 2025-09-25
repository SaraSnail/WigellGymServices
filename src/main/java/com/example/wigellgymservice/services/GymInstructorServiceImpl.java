package com.example.wigellgymservice.services;

import com.example.wigellgymservice.exceptions.ContentNotFoundException;
import com.example.wigellgymservice.models.DTO.DTOGymInstructor;
import com.example.wigellgymservice.models.entities.GymInstructor;
import com.example.wigellgymservice.services.util.util.Util;
import com.example.wigellgymservice.repositories.GymInstructorRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Service
public class GymInstructorServiceImpl implements GymInstructorService {


    private final GymInstructorRepository gymInstructorRepository;

    private static final Logger CHANGES_IN_DB_LOGGER = LogManager.getLogger("changeindb");

    @Autowired
    public GymInstructorServiceImpl(GymInstructorRepository gymInstructorRepository) {
        this.gymInstructorRepository = gymInstructorRepository;
    }



    @Override
    public List<GymInstructor> getAllGymInstructors() {
        List<GymInstructor> gymInstructors = gymInstructorRepository.findAll();
        if (gymInstructors.isEmpty()) {
            throw new ContentNotFoundException("GymInstructor");
        }
        return gymInstructors;
    }


    //TODO: should it return DTO here, it will not show the ID if DTO
    @Override
    public GymInstructor addGymInstructor(DTOGymInstructor dtoGymInstructor, Principal principal, Authentication authentication) {
        GymInstructor gymInstructor = dtoToGymInstructor(dtoGymInstructor);
        checkGymInstructor(gymInstructor);

        gymInstructorRepository.save(gymInstructor);

        CHANGES_IN_DB_LOGGER.info("{} {} added a {} instructor with speciality in {}", authentication.getAuthorities(), principal.getName(), gymInstructor.getGymInstructorName(), gymInstructor.getTrainingType());
        return gymInstructor;
    }

    private GymInstructor dtoToGymInstructor(DTOGymInstructor dtoGymInstructor) {
        GymInstructor gymInstructor = new GymInstructor();
        gymInstructor.setGymInstructorName(dtoGymInstructor.getGymInstructorName());
        gymInstructor.setTrainingType(dtoGymInstructor.getTrainingType());
        gymInstructor.setActive(dtoGymInstructor.isActive());
        return gymInstructor;
    }


    private void checkGymInstructor(GymInstructor gymInstructor) {
        if(gymInstructor.getGymInstructorName() == null || gymInstructor.getGymInstructorName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Gym Instructor name is empty");
        }

        if(gymInstructor.getTrainingType() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Training Type is empty");
        }

        if(!Util.validTrainingType(gymInstructor.getTrainingType().toString())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Training Type is not valid");
        }

    }


}
