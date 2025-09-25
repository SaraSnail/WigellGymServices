package com.example.wigellgymservice.services;

import com.example.wigellgymservice.exceptions.ContentNotFoundException;
import com.example.wigellgymservice.exceptions.ResourceNotFoundException;
import com.example.wigellgymservice.models.DTO.DTOGymWorkout;
import com.example.wigellgymservice.models.entities.GymBooking;
import com.example.wigellgymservice.models.entities.GymInstructor;
import com.example.wigellgymservice.models.entities.GymWorkout;
import com.example.wigellgymservice.services.util.util.Util;
import com.example.wigellgymservice.repositories.GymBookingRepository;
import com.example.wigellgymservice.repositories.GymInstructorRepository;
import com.example.wigellgymservice.repositories.GymWorkoutRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GymWorkoutServiceImpl implements GymWorkoutService {

    private final GymWorkoutRepository gymWorkoutRepository;
    private final GymInstructorRepository gymInstructorRepository;
    private final GymBookingRepository gymBookingRepository;

    private static final Logger CHANGES_IN_DB_LOGGER = LogManager.getLogger("changeindb");

    @Autowired
    public GymWorkoutServiceImpl(GymWorkoutRepository gymWorkoutRepository, GymInstructorRepository gymInstructorRepository, GymBookingRepository gymBookingRepository) {
        this.gymWorkoutRepository = gymWorkoutRepository;
        this.gymInstructorRepository = gymInstructorRepository;
        this.gymBookingRepository = gymBookingRepository;
    }


    @Override
    public List<GymWorkout> getAllGymWorkouts() {
        List<GymWorkout> allUpComingWorkouts = gymWorkoutRepository.findAllByDateTimeAfter(LocalDateTime.now());
        if (allUpComingWorkouts.isEmpty()) {
            throw new ContentNotFoundException("upcoming gym workouts");
        }

        ///So it won't show workouts that are fully booked
        List<GymWorkout> availableWorkouts = new ArrayList<>();
        for(GymWorkout gymWorkout : allUpComingWorkouts) {
            List<GymBooking> bookings = gymBookingRepository.findAllByIsActiveTrueAndGymWorkout(gymWorkout);
            if(bookings.size() < gymWorkout.getMaxParticipants()){
                availableWorkouts.add(gymWorkout);
            }
        }


        if (allUpComingWorkouts.isEmpty()) {
            throw new ContentNotFoundException("upcoming available gym workouts");
        }

        return availableWorkouts;
    }

    @Override
    public GymWorkout addGymWorkout(DTOGymWorkout dtoGymWorkout, Long instructorId, Principal principal, Authentication authentication) {

        GymWorkout gymWorkout = dtoToGymWorkout(dtoGymWorkout, instructorId);
        validateGymWorkout(gymWorkout);

        checkIfInstructorBooked(gymWorkout, gymWorkout.getGymInstructor());

        gymWorkoutRepository.save(gymWorkout);

        CHANGES_IN_DB_LOGGER.info("{} {} added a {} workout on {} with instructor {}",
                authentication.getAuthorities(),
                principal.getName(),
                gymWorkout.getTrainingType(),
                gymWorkout.getDateTime(),
                gymWorkout.getGymInstructor().getGymInstructorName());
        return gymWorkout;
    }

    @Override
    public GymWorkout updateGymWorkout(GymWorkout gymWorkout, Long instructorId, Principal principal, Authentication authentication) {

        Optional<GymWorkout> findGymWorkout = gymWorkoutRepository.findById(gymWorkout.getGymWorkoutId());
        if(findGymWorkout.isEmpty()){
            throw new ResourceNotFoundException("GymWorkout","id",gymWorkout.getGymWorkoutId());
        }

        GymWorkout oldGymWorkout = findGymWorkout.get();

        Optional<GymInstructor> instructor = gymInstructorRepository.findById(instructorId);
        if(instructor.isEmpty()){
            throw new ResourceNotFoundException("GymInstructor","id",instructorId);
        }

        GymInstructor gymInstructor = instructor.get();

        validateGymWorkout(gymWorkout);
        GymWorkout newGymWorkout = updateGymWorkout(gymWorkout, gymInstructor, oldGymWorkout.getGymWorkoutId());
        checkIfInstructorBooked(gymWorkout, gymInstructor);

        gymWorkoutRepository.save(newGymWorkout);

        String changes = "Changes: old -> new\n" +
                "Name: " + oldGymWorkout.getName() +" -> "+ newGymWorkout.getName() +"\n" +
                "Training Type: "+oldGymWorkout.getTrainingType().toString() + " -> "+ newGymWorkout.getTrainingType().toString() + "\n" +
                "Max Participants: "+oldGymWorkout.getMaxParticipants() +" -> "+newGymWorkout.getMaxParticipants() + "\n" +
                "Price sek: "+oldGymWorkout.getPrice() + " -> "+newGymWorkout.getPrice() + "\n" +
                "Gym instructor name: "+oldGymWorkout.getGymInstructor().getGymInstructorName() + " -> " +newGymWorkout.getGymInstructor().getGymInstructorName() +"\n" +
                "Date time: "+oldGymWorkout.getDateTime() + " -> "+newGymWorkout.getDateTime() + "\n" +
                "Is active: "+oldGymWorkout.isActive() + " -> "+newGymWorkout.isActive();

        //TODO: might add so logger also saves what was updated
        CHANGES_IN_DB_LOGGER.info("{} {} updated workout with id '{}'. \n{}",
                authentication.getAuthorities(),
                principal.getName(),
                newGymWorkout.getGymWorkoutId(),
                changes);

        return newGymWorkout;
    }



    @Override
    public String removeGymWorkout(Long id, Principal principal, Authentication authentication) {
        Optional<GymWorkout>findGymWorkout = gymWorkoutRepository.findById(id);
        if(findGymWorkout.isEmpty()){
            throw new ResourceNotFoundException("GymWorkout","id",id);
        }

        GymWorkout gymWorkout = findGymWorkout.get();
        List<GymBooking> bookings = gymBookingRepository.findGymBookingsByGymWorkout(gymWorkout);

        if(!bookings.isEmpty()){
            for(GymBooking booking : bookings){
                booking.setActive(false);
            }
        }

        gymWorkout.setActive(false);

        gymWorkoutRepository.save(gymWorkout);
        gymBookingRepository.saveAll(bookings);

        CHANGES_IN_DB_LOGGER.info("{} {} removed (set to inactive) workout with id '{}'",
                authentication.getAuthorities(),
                principal.getName(),
                gymWorkout.getGymWorkoutId());
        return "Gym workout has been set to inactive.\n"+ gymWorkout;
    }

    private void validateGymWorkout(GymWorkout gymWorkout) {
        if(gymWorkout.getName() == null || gymWorkout.getName().isBlank() || gymWorkout.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid gym workout name");
        }

        if(gymWorkout.getMaxParticipants() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid gym workout max participants");
        }

        if(gymWorkout.getPrice() < 10.0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"To low gym workout price, it has to be more than 10.0");
        }

        if(gymWorkout.getTrainingType() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Empty gym workout training type");
        }
        if(!Util.validTrainingType(gymWorkout.getTrainingType().toString())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid gym workout training type");
        }

        if(gymWorkout.getDateTime() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Empty gym workout datetime");
        }

        LocalDateTime now = LocalDateTime.now();

        if(gymWorkout.getDateTime().isBefore(now)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid gym workout date. Date/time already happened");
        }
        if(gymWorkout.getDateTime().isEqual(now)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid gym workout date. Date/time is now and cannot be accepted");
        }

        if(!now.isBefore(gymWorkout.getDateTime().minusMinutes(30))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid gym workout date. Have to be at least 30 minutes after now");
        }
    }

    private GymWorkout dtoToGymWorkout(DTOGymWorkout dtoGymWorkout, Long instructorId){
        Optional<GymInstructor> findGymInstructor = gymInstructorRepository.findById(instructorId);
        if(findGymInstructor.isEmpty()){
            throw new ResourceNotFoundException("GymInstructor","id",instructorId);
        }
        GymInstructor gymInstructor = findGymInstructor.get();

        GymWorkout gymWorkout = new GymWorkout();
        gymWorkout.setName(dtoGymWorkout.getName());
        gymWorkout.setMaxParticipants(dtoGymWorkout.getMaxParticipants());
        gymWorkout.setPrice(dtoGymWorkout.getPrice());
        gymWorkout.setTrainingType(dtoGymWorkout.getTrainingType());
        gymWorkout.setDateTime(dtoGymWorkout.getDateTime());
        gymWorkout.setGymInstructor(gymInstructor);

        return gymWorkout;
    }

    private GymWorkout updateGymWorkout(GymWorkout gymWorkout, GymInstructor gymInstructor, Long id) {
        GymWorkout newGymWorkout = new GymWorkout();

        newGymWorkout.setGymWorkoutId(id);
        newGymWorkout.setName(gymWorkout.getName());
        newGymWorkout.setMaxParticipants(gymWorkout.getMaxParticipants());
        newGymWorkout.setPrice(gymWorkout.getPrice());
        newGymWorkout.setTrainingType(gymWorkout.getTrainingType());
        newGymWorkout.setGymInstructor(gymInstructor);
        newGymWorkout.setDateTime(gymWorkout.getDateTime());
        newGymWorkout.setActive(gymWorkout.isActive());
        return newGymWorkout;
    }

    private void checkIfInstructorBooked(GymWorkout newGymWorkout, GymInstructor gymInstructor) {
        List<GymWorkout> workoutsWithGivenInstructor = gymWorkoutRepository.findAllByGymInstructorAndIsActiveTrue(gymInstructor);
        System.out.println("active workouts with that instructor: \n" + workoutsWithGivenInstructor);

        LocalDateTime newStart = newGymWorkout.getDateTime();

        for(GymWorkout existingWorkout : workoutsWithGivenInstructor){
            LocalDateTime existingStart = existingWorkout.getDateTime();

            long minutesBetween = Duration.between(existingStart, newStart).toMinutes();
            long absMinutesBetween = Math.abs(minutesBetween);

            if(absMinutesBetween < 75){
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Instructor is already booked around "+existingStart + "." +
                                " Bookings must be at least 75 minutes apart"
                );
            }
        }
        System.out.println("Instructor is available for new workout");
    }
}
