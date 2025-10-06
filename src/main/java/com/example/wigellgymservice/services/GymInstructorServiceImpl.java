package com.example.wigellgymservice.services;

import com.example.wigellgymservice.exceptions.ContentNotFoundException;
import com.example.wigellgymservice.models.DTO.DTOGymInstructor;
import com.example.wigellgymservice.models.entities.GymInstructor;
import com.example.wigellgymservice.services.util.validateTrainingType;
import com.example.wigellgymservice.repositories.GymInstructorRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    public List<GymInstructor> getAllGymInstructors(Authentication authentication) {
        List<GymInstructor> gymInstructors;

        if(authentication.getAuthorities().toString().equals("[ROLE_ADMIN]")) {
            gymInstructors = gymInstructorRepository.findAll();
        } else {
            gymInstructors = gymInstructorRepository.findAllByIsActiveTrue();
        }

        if (gymInstructors.isEmpty()) {
            throw new ContentNotFoundException("GymInstructor");
        }
        return gymInstructors;
    }

    @Override
    public GymInstructor addGymInstructor(DTOGymInstructor dtoGymInstructor, Authentication authentication) {
        checkGymInstructor(dtoGymInstructor);
        GymInstructor gymInstructor = dtoToGymInstructor(dtoGymInstructor);

        gymInstructorRepository.save(gymInstructor);

        CHANGES_IN_DB_LOGGER.info("{} {} added instructor {} with speciality in {}",
                authentication.getAuthorities(),
                authentication.getName(),
                gymInstructor.getGymInstructorName(),
                gymInstructor.getTrainingType());

        return gymInstructor;
    }

    private GymInstructor dtoToGymInstructor(DTOGymInstructor dtoGymInstructor) {
        GymInstructor gymInstructor = new GymInstructor();
        gymInstructor.setGymInstructorName(dtoGymInstructor.getGymInstructorName());
        gymInstructor.setTrainingType(validateTrainingType.getTrainingType(dtoGymInstructor.getTrainingType()));
        gymInstructor.setActive(true);
        return gymInstructor;
    }


    private void checkGymInstructor(DTOGymInstructor dtoGymInstructor) {
        if(dtoGymInstructor.getGymInstructorName() == null || dtoGymInstructor.getGymInstructorName().isBlank() || dtoGymInstructor.getGymInstructorName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Gym Instructor name is empty");
        }

        if(dtoGymInstructor.getTrainingType() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Training Type is empty");
        }

        if(!validateTrainingType.validTrainingType(dtoGymInstructor.getTrainingType().toString())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Training Type is not valid");
        }

    }


}
