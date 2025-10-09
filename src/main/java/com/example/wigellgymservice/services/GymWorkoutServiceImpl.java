package com.example.wigellgymservice.services;

import com.example.wigellgymservice.exceptions.ContentNotFoundException;
import com.example.wigellgymservice.exceptions.ResourceNotFoundException;
import com.example.wigellgymservice.models.DTO.DTOGymWorkout;
import com.example.wigellgymservice.models.entities.GymBooking;
import com.example.wigellgymservice.models.entities.GymInstructor;
import com.example.wigellgymservice.models.entities.GymWorkout;
import com.example.wigellgymservice.services.util.validateTrainingType;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class GymWorkoutServiceImpl implements GymWorkoutService {

    private final GymWorkoutRepository gymWorkoutRepository;
    private final GymInstructorRepository gymInstructorRepository;
    private final GymBookingRepository gymBookingRepository;

    private String newName = "";
    private String newTrainingType = "";
    private String newMaxParticipants = "";
    private String newPrice = "";
    private String newGymInstructorName = "";
    private String newDateTime = "";
    private String arrow = " -> ";

    private static final Logger CHANGES_IN_DB_LOGGER = LogManager.getLogger("changeindb");

    @Autowired
    public GymWorkoutServiceImpl(GymWorkoutRepository gymWorkoutRepository, GymInstructorRepository gymInstructorRepository, GymBookingRepository gymBookingRepository) {
        this.gymWorkoutRepository = gymWorkoutRepository;
        this.gymInstructorRepository = gymInstructorRepository;
        this.gymBookingRepository = gymBookingRepository;
    }


    @Override
    public List<GymWorkout> getAllGymWorkouts() {
        List<GymWorkout> allUpComingWorkouts = gymWorkoutRepository.findAllByDateTimeAfterAndIsActiveTrue(LocalDateTime.now());
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

        if (availableWorkouts.isEmpty()) {
            throw new ContentNotFoundException("upcoming available gym workouts");
        }

        return availableWorkouts;
    }

    @Override
    public GymWorkout addGymWorkout(DTOGymWorkout dtoGymWorkout, Long instructorId, Authentication authentication) {

        notNull(dtoGymWorkout, instructorId);
        GymWorkout gymWorkout = dtoToGymWorkout(dtoGymWorkout, instructorId);
        validateGymWorkout(gymWorkout);
        checkIfInstructorBooked(gymWorkout, gymWorkout.getGymInstructor());

        gymWorkoutRepository.save(gymWorkout);

        CHANGES_IN_DB_LOGGER.info("{} {} added a {} workout, id '{}', on {} with instructor {}",
                authentication.getAuthorities(),
                authentication.getName(),
                gymWorkout.getTrainingType(),
                gymWorkout.getGymWorkoutId(),
                gymWorkout.getDateTime(),
                gymWorkout.getGymInstructor().getGymInstructorName());

        return gymWorkout;
    }

    @Override
    public GymWorkout updateGymWorkout(DTOGymWorkout dtoGymWorkout, Long workoutId, Long instructorId, Authentication authentication) {
        Optional<GymWorkout> findGymWorkout = gymWorkoutRepository.findById(workoutId);
        if(findGymWorkout.isEmpty()){
            throw new ResourceNotFoundException("GymWorkout","id",workoutId);
        }

        GymWorkout gymWorkout = findGymWorkout.get();

        GymWorkout oldGymWorkout = new GymWorkout(gymWorkout);

        Optional<GymInstructor> instructor = gymInstructorRepository.findById(instructorId);
        if(instructor.isEmpty()){
            throw new ResourceNotFoundException("GymInstructor","id",instructorId);
        }

        GymInstructor gymInstructor = instructor.get();

        GymWorkout updatedWorkout = nullValues(dtoGymWorkout, gymWorkout);

        validateGymWorkout(updatedWorkout);
        checkIfInstructorBooked(updatedWorkout, gymInstructor);
        updatedWorkout.setGymInstructor(gymInstructor);

        ifEquals(updatedWorkout, oldGymWorkout);

        String changes = "Changes: original -> change\n" +
                "Name: " + oldGymWorkout.getName() +newName +"\n" +
                "Training Type: "+oldGymWorkout.getTrainingType().toString() + newTrainingType+ "\n" +
                "Max Participants: "+oldGymWorkout.getMaxParticipants() +newMaxParticipants + "\n" +
                "Price sek: "+oldGymWorkout.getPrice() + newPrice + "\n" +
                "Gym instructor name: "+oldGymWorkout.getGymInstructor().getGymInstructorName() + newGymInstructorName +"\n" +
                "Date time: "+oldGymWorkout.getDateTime() + newDateTime+ "\n" +
                "Is active: "+updatedWorkout.isActive();


        CHANGES_IN_DB_LOGGER.info("{} {} updated workout with id '{}'. \n{}",
                authentication.getAuthorities(),
                authentication.getName(),
                workoutId,
                changes);

        gymWorkoutRepository.save(updatedWorkout);

        return updatedWorkout;
    }



    @Override
    public String removeGymWorkout(Long id, Authentication authentication) {
        Optional<GymWorkout>findGymWorkout = gymWorkoutRepository.findById(id);
        if(findGymWorkout.isEmpty()){
            throw new ResourceNotFoundException("GymWorkout","id",id);
        }

        GymWorkout gymWorkout = findGymWorkout.get();
        List<GymBooking> bookingsSetToInactive = new ArrayList<>();

        String pl = "";
        if(gymWorkout.getGymBookings().size()!=1){
            pl = "s";
        }
        String bookingsAffected = "Workout had '"+gymWorkout.getGymBookings().size()+"' booking"+pl;



        if(gymWorkout.getDateTime().isAfter(LocalDateTime.now())){
            if(!gymWorkout.getGymBookings().isEmpty()){
                for(GymBooking gymBooking : gymWorkout.getGymBookings()){

                    if(gymBooking.isActive()){
                        bookingsSetToInactive.add(gymBooking);
                        gymBooking.setActive(false);
                    }
                }
                if(bookingsSetToInactive.size() <2){
                    pl = "";
                }
                bookingsAffected = bookingsAffected +".\n '"+bookingsSetToInactive.size()+ "' booking"+pl+" set to inactive";
            }

        }

        gymWorkout.setActive(false);

        gymWorkoutRepository.save(gymWorkout);
        gymBookingRepository.saveAll(gymWorkout.getGymBookings());

        CHANGES_IN_DB_LOGGER.info("{} {} removed (set to inactive) workout with id '{}'. {}",
                authentication.getAuthorities(),
                authentication.getName(),
                gymWorkout.getGymWorkoutId(),
                bookingsAffected);

        return "Gym workout has been set to inactive. "+bookingsAffected;
    }

    private void notNull(DTOGymWorkout dtoGymWorkout, Long instructorId){
        if(instructorId == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"InstructorId cannot be null");
        }
        if(dtoGymWorkout.getName() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"GymWorkout name cannot be null");
        }
        if(dtoGymWorkout.getTrainingType() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"GymWorkout trainingType cannot be null");
        }
        if(dtoGymWorkout.getMaxParticipants() == 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"GymWorkout maxParticipants cannot be zero");
        }
        if(dtoGymWorkout.getPrice() == 0.0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"GymWorkout price cannot be zero");
        }
        if(dtoGymWorkout.getDateTime() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"GymWorkout datetime cannot be null");
        }
    }

    private GymWorkout nullValues(DTOGymWorkout dtoGymWorkout, GymWorkout gymWorkout){

        if(dtoGymWorkout.getName() != null){
            gymWorkout.setName(dtoGymWorkout.getName());
            newName = arrow +dtoGymWorkout.getName();
        }
        if(dtoGymWorkout.getTrainingType() != null){
            gymWorkout.setTrainingType(validateTrainingType.getTrainingType(dtoGymWorkout.getTrainingType()));
            newTrainingType = arrow +dtoGymWorkout.getTrainingType();
        }
        if(dtoGymWorkout.getMaxParticipants() != 0){
            gymWorkout.setMaxParticipants(dtoGymWorkout.getMaxParticipants());
            newMaxParticipants = arrow +dtoGymWorkout.getMaxParticipants();
        }
        if(dtoGymWorkout.getPrice() != 0.0){
            gymWorkout.setPrice(dtoGymWorkout.getPrice());
            newPrice = arrow +dtoGymWorkout.getPrice();
        }
        if(dtoGymWorkout.getDateTime() != null){
            gymWorkout.setDateTime(dtoGymWorkout.getDateTime());
            newDateTime = arrow +dtoGymWorkout.getDateTime().toString();
        }

        return gymWorkout;
    }

    private void validateGymWorkout(GymWorkout gymWorkout) {
        if(gymWorkout.getName().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Empty gym workout name");
        }

        if(gymWorkout.getName().length() < 3){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"To short workout name, has to be at least 3 characters");
        }

        if(gymWorkout.getMaxParticipants() < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"To low gym workout max participants, it has to be 1 or more");
        }

        if(gymWorkout.getPrice() < 70.0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"To low gym workout price, it has to be more than 70.0");
        }

        LocalDateTime now = LocalDateTime.now();

        if(gymWorkout.getDateTime().isBefore(now)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid gym workout date. Date/time already happened");
        }

        if(!now.isBefore(gymWorkout.getDateTime().minusMinutes(30))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid gym workout date. Have to be at least 30 minutes after now");
        }
    }

    private void ifEquals(GymWorkout update, GymWorkout old){
        if(!Objects.equals(update.getName(), old.getName())){
            newName = arrow +update.getName();
        }
        if(!Objects.equals(update.getTrainingType(), old.getTrainingType())){
            newTrainingType = arrow +update.getTrainingType();
        }
        if(!Objects.equals(update.getMaxParticipants(), old.getMaxParticipants())){
            newMaxParticipants = arrow +update.getMaxParticipants();
        }
        if(!Objects.equals(update.getPrice(), old.getPrice())){
            newPrice = arrow +update.getPrice();
        }
        if(!Objects.equals(update.getDateTime(), old.getDateTime())){
            newDateTime = arrow +update.getDateTime();
        }
        if(!Objects.equals(update.getGymInstructor().getGymInstructorName(), old.getGymInstructor().getGymInstructorName())){
            newGymInstructorName = arrow +update.getGymInstructor().getGymInstructorName();
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
        gymWorkout.setTrainingType(validateTrainingType.getTrainingType(dtoGymWorkout.getTrainingType()));
        gymWorkout.setDateTime(dtoGymWorkout.getDateTime());
        gymWorkout.setGymInstructor(gymInstructor);
        gymWorkout.setActive(true);

        return gymWorkout;
    }



    private void checkIfInstructorBooked(GymWorkout newGymWorkout, GymInstructor gymInstructor) {
        if(!gymInstructor.isActive()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"GymInstructor with id '"+gymInstructor.getGymInstructorId()+"' is not active");
        }

        List<GymWorkout> workoutsWithGivenInstructor = gymWorkoutRepository.findAllByGymInstructorAndIsActiveTrue(gymInstructor);

        LocalDateTime newStart = newGymWorkout.getDateTime();

        for(GymWorkout existingWorkout : workoutsWithGivenInstructor){
            if(existingWorkout == newGymWorkout){
                continue;
            }
            LocalDateTime existingStart = existingWorkout.getDateTime();

            //Gets the amount of minutes between the times, ex 30 if newStart is 30min after or -30 if newStart is 30min before existingStart
            long minutesBetween = Duration.between(existingStart, newStart).toMinutes();
            long absMinutesBetween = Math.abs(minutesBetween);//Saves the minutes as hole numbers, ex 30=30 or -30=30

            if(absMinutesBetween < 75){
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Instructor is already booked around "+existingStart +". Bookings must be at least 1 hour and 15 minutes (75 minutes) apart"
                );
            }
        }
    }
}
