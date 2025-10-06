package com.example.wigellgymservice.services;

import com.example.wigellgymservice.exceptions.ContentNotFoundException;
import com.example.wigellgymservice.exceptions.ResourceNotFoundException;
import com.example.wigellgymservice.services.externalAPI.CurrencyConverter;
import com.example.wigellgymservice.services.externalAPI.CurrencyConverterImpl;
import com.example.wigellgymservice.models.DTO.DTOGymBooking;
import com.example.wigellgymservice.models.entities.GymBooking;
import com.example.wigellgymservice.models.entities.GymCustomer;
import com.example.wigellgymservice.models.entities.GymWorkout;
import com.example.wigellgymservice.repositories.GymBookingRepository;
import com.example.wigellgymservice.repositories.GymCustomerRepository;
import com.example.wigellgymservice.repositories.GymWorkoutRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GymBookingServiceImpl implements GymBookingService {

    private final GymBookingRepository gymBookingRepository;
    private final GymWorkoutRepository gymWorkoutRepository;
    private final GymCustomerRepository gymCustomerRepository;
    private final CurrencyConverter currencyConverter;

    private double bookingFee = 40.0;

    private static final Logger CHANGES_IN_DB_LOGGER = LogManager.getLogger("changeindb");

    @Autowired
    public GymBookingServiceImpl(GymBookingRepository gymBookingRepository, GymWorkoutRepository gymWorkoutRepository, GymCustomerRepository gymCustomerRepository, CurrencyConverter currencyConverter) {
        this.gymBookingRepository = gymBookingRepository;
        this.gymWorkoutRepository = gymWorkoutRepository;
        this.gymCustomerRepository = gymCustomerRepository;
        this.currencyConverter = currencyConverter;
    }

    //Admin
    @Override
    public List<DTOGymBooking> getCancelledGymBookings() {
        List<GymBooking>cancelledBookings = gymBookingRepository.findAllByIsActive(false);
        if(cancelledBookings.isEmpty()){
            throw new ContentNotFoundException("cancelled gym bookings");
        }

        return getDtoGymBookings(cancelledBookings);
    }


    @Override
    public List<DTOGymBooking> upComingGymBookings() {
        List<GymBooking> activeBookings = gymBookingRepository.findAllByIsActive(true);
        if(activeBookings.isEmpty()){
            throw new ContentNotFoundException("active gym bookings");
        }
        List<GymBooking> bookingsUpComing = new ArrayList<>();
        for(GymBooking booking : activeBookings){
            if(booking.getGymWorkout().getDateTime().isAfter(LocalDateTime.now())){
                bookingsUpComing.add(booking);
            }
        }

        if(bookingsUpComing.isEmpty()){
            throw new ContentNotFoundException("upcoming active gym bookings");
        }

        return getDtoGymBookings(bookingsUpComing);
    }

    @Override
    public List<DTOGymBooking> pastGymBookings() {
        List<GymWorkout> pastWorkouts = gymWorkoutRepository.findAllByDateTimeBefore(LocalDateTime.now());
        if(pastWorkouts.isEmpty()){
            throw new ContentNotFoundException("past gym workouts");
        }

        List<GymBooking> pastBookings = new ArrayList<>();
        for(GymWorkout workout : pastWorkouts){
            List<GymBooking> pastActiveBookings = gymBookingRepository.findAllByIsActiveTrueAndGymWorkout(workout);
            pastBookings.addAll(pastActiveBookings);
        }
        if(pastBookings.isEmpty()){
            throw new ContentNotFoundException("past gym bookings");
        }

        return getDtoGymBookings(pastBookings);
    }




    //User
    @Override
    public List<DTOGymBooking> getUserGymBookings(String username) {

        GymCustomer customer = findCustomer(username);

        List<GymBooking> bookings = gymBookingRepository.findAllByGymCustomer(customer);
        if(bookings.isEmpty()){
            throw new ContentNotFoundException("gym bookings");
        }

        return getDtoGymBookings(bookings);
    }

    @Override
    public DTOGymBooking bookWorkout(Authentication authentication, Long workoutId) {

        GymCustomer customer = findCustomer(authentication.getName());
        GymWorkout workout = findWorkout(workoutId);

        if(workout.getDateTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Workout has already happened/started");
        }

        List<GymBooking> customerBookings = gymBookingRepository.findAllByIsActiveTrueAndGymCustomer(customer);
        for(GymBooking booking : customerBookings) {
            if(booking.getGymWorkout().getGymWorkoutId().equals(workout.getGymWorkoutId())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already booked this workout");
            }
        }

        List<GymBooking> workoutBookings = gymBookingRepository.findAllByIsActiveTrueAndGymWorkout(workout);

        if(workout.getMaxParticipants() <= workoutBookings.size()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Maximum number of participants exceeded");
        }

        GymBooking newGymBooking = new GymBooking(customer,workout,LocalDateTime.now(),workout.getPrice() + bookingFee, true);

        customer.getGymBookings().add(newGymBooking);
        gymBookingRepository.save(newGymBooking);
        gymCustomerRepository.save(customer);

        DTOGymBooking dtoGymBooking = dtoConverterBooking(newGymBooking);

        CHANGES_IN_DB_LOGGER.info("{} {} booked a {} workout on {}. \nPrice sek: {}\nPrice euro:{}",
                authentication.getAuthorities(),
                customer.getUsername(),
                workout.getTrainingType(),
                newGymBooking.getBookingDate(),
                newGymBooking.getPrice(),
                dtoGymBooking.getPriceEuro());

        return dtoGymBooking;
    }

    @Override
    public String cancelBookingOnWorkout(Authentication authentication, Long bookingId) {
        GymCustomer customer = findCustomer(authentication.getName());
        GymBooking booking = findBooking(bookingId);
        GymWorkout workout = findWorkout(booking.getGymWorkout().getGymWorkoutId());

        if(!booking.getGymCustomer().getUsername().equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"You are not allowed to cancel this booking. Customer username mismatch");
        }

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime workoutDateTime = booking.getGymWorkout().getDateTime();

        if(!today.isBefore(workoutDateTime.minusDays(1))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Too late to cancel. Cancellation must be at least 24 hours before the workout will take place");
        }

        booking.setActive(false);

        gymBookingRepository.save(booking);
        gymCustomerRepository.save(customer);

        CHANGES_IN_DB_LOGGER.info("{} {} canceled booking '{}', a {} workout with {}",
                authentication.getAuthorities(),
                customer.getUsername(),
                bookingId,
                workout.getTrainingType(),
                workout.getGymInstructor().getGymInstructorName());

        return "Booking successfully cancelled";
    }








    private List<DTOGymBooking> getDtoGymBookings(List<GymBooking> bookings) {
        List<DTOGymBooking> dtoGymBookings = new ArrayList<>();
        for(GymBooking booking : bookings){
            dtoGymBookings.add(dtoConverterBooking(booking));
        }

        return dtoGymBookings;
    }

    private DTOGymBooking dtoConverterBooking (GymBooking booking){
        if(booking.getPrice() < 40){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Price sek cannot be less than 40 (the booking fee)");
        }

        double priceEuro = currencyConverter.sekToEuroConverter(booking.getPrice());

        if(priceEuro == 0.0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Currency conversion failed");
        }
        DTOGymBooking dtoGymBooking = new DTOGymBooking(
                booking.getGymBookingId(),
                booking.getGymCustomer(),
                booking.getGymWorkout(),
                booking.getBookingDate(),
                booking.getPrice(),
                priceEuro,
                booking.isActive()
        );
        return dtoGymBooking;
    }

    private GymCustomer findCustomer(String name){
        Optional<GymCustomer> findCustomer = Optional.ofNullable(gymCustomerRepository.findByUsername(name));
        if(findCustomer.isEmpty()){
            throw new ResourceNotFoundException("GymCustomer","username",name);
        }
        GymCustomer customer = findCustomer.get();
        if(!customer.isActive()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer with username ["+customer.getUsername()+"] is not active");
        }
        return customer;
    }

    private GymWorkout findWorkout(Long id){
        Optional<GymWorkout> findWorkout = gymWorkoutRepository.findById(id);
        if(findWorkout.isEmpty()){
            throw new ResourceNotFoundException("GymWorkout","id",id);
        }
        return findWorkout.get();
    }

    private GymBooking findBooking(Long id){
        Optional<GymBooking> findBooking = gymBookingRepository.findById(id);
        if(findBooking.isEmpty()){
            throw new ResourceNotFoundException("GymBooking","id",id);
        }
        return findBooking.get();
    }
}
