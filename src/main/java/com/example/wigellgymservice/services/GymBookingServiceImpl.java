package com.example.wigellgymservice.services;

import com.example.wigellgymservice.exceptions.ContentNotFoundException;
import com.example.wigellgymservice.exceptions.ResourceNotFoundException;
import com.example.wigellgymservice.models.DTO.DTOGymBooking;
import com.example.wigellgymservice.models.entities.GymBooking;
import com.example.wigellgymservice.models.entities.GymCustomer;
import com.example.wigellgymservice.models.entities.GymWorkout;
import com.example.wigellgymservice.services.util.util.CurrencyConverter;
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

    private double bookingFee = 40.0;

    private static final Logger CHANGES_IN_DB_LOGGER = LogManager.getLogger("changeindb");

    @Autowired
    public GymBookingServiceImpl(GymBookingRepository gymBookingRepository, GymWorkoutRepository gymWorkoutRepository, GymCustomerRepository gymCustomerRepository) {
        this.gymBookingRepository = gymBookingRepository;
        this.gymWorkoutRepository = gymWorkoutRepository;
        this.gymCustomerRepository = gymCustomerRepository;
    }


    //User
    @Override
    public DTOGymBooking bookWorkout(String username, Authentication authentication, Long workoutId) {

        GymCustomer customer = findCustomer(username);
        GymWorkout workout = findWorkout(workoutId);

        if(workout.getDateTime().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Workout has already happened");
        }

        List<GymBooking> customerBookings = gymBookingRepository.findAllByIsActiveTrueAndGymCustomer(customer);
        for(GymBooking booking : customerBookings) {
            if(booking.getGymWorkout().equals(workout)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "You have already booked this workout");
            }
        }

        List<GymBooking> workoutBookings = gymBookingRepository.findAllByIsActiveTrueAndGymWorkout(workout);
        System.out.println("Number of active gym bookings '"+workoutBookings.size()+"' on workout with name: "+workout.getName());

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
    public String cancelBookingOnWorkout(String username,Authentication authentication, Long bookingId) {
        GymCustomer customer = findCustomer(username);
        GymBooking booking = findBooking(bookingId);

        if(!booking.getGymCustomer().getUsername().equals(username)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"You are not allowed to cancel this booking. Customer id mismatch");
        }

        LocalDateTime today = LocalDateTime.now();
        LocalDateTime workoutDateTime = booking.getGymWorkout().getDateTime();

        if(!today.isBefore(workoutDateTime.minusDays(1))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Too late to cancel. Cancellation must be at least 24 hours before the workout will take place");
        }

        GymWorkout workout = findWorkout(booking.getGymWorkout().getGymWorkoutId());

        booking.setActive(false);

        gymBookingRepository.save(booking);
        gymCustomerRepository.save(customer);
        gymWorkoutRepository.save(workout);

        CHANGES_IN_DB_LOGGER.info("{} {} canceled booking '{}', a {} workout with {}",
                authentication.getAuthorities(),
                customer.getUsername(),
                bookingId,
                workout.getTrainingType(),
                workout.getGymInstructor().getGymInstructorName());

        return "Booking successfully cancelled";
    }

    @Override
    public List<DTOGymBooking> getGymBookings(String username) {

        GymCustomer customer = findCustomer(username);

        List<GymBooking> bookings = gymBookingRepository.findAllByIsActiveTrueAndGymCustomer(customer);
        if(bookings.isEmpty()){
            throw new ContentNotFoundException("gym bookings");
        }

        return getDtoGymBookings(bookings);
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
            } else if (booking.getGymWorkout().getDateTime().isEqual(LocalDateTime.now())) {
                bookingsUpComing.add(booking);
            }
        }

        if(bookingsUpComing.isEmpty()){
            throw new ContentNotFoundException("upcoming active gym bookings");
        }

        return getDtoGymBookings(bookingsUpComing);
    }

    @Override
    public List<DTOGymBooking> historicalGymBookings() {
        List<GymBooking> pastBookings = new ArrayList<>();

        List<GymBooking> allBookings = gymBookingRepository.findAll();
        if(allBookings.isEmpty()){
            throw new ContentNotFoundException("gym bookings");
        }
        for(GymBooking booking : allBookings){
            if(booking.getGymWorkout().getDateTime().isBefore(LocalDateTime.now())){
                pastBookings.add(booking);
            }
        }
        if(pastBookings.isEmpty()){
            throw new ContentNotFoundException("past gym bookings");
        }

        return getDtoGymBookings(pastBookings);
    }





    private List<DTOGymBooking> getDtoGymBookings(List<GymBooking> bookings) {
        List<DTOGymBooking> dtoGymBookings = new ArrayList<>();
        for(GymBooking booking : bookings){
            dtoGymBookings.add(dtoConverterBooking(booking));
        }

        if(dtoGymBookings.isEmpty()){
            throw new ContentNotFoundException("dto gym bookings");
        }

        return dtoGymBookings;
    }

    private DTOGymBooking dtoConverterBooking (GymBooking booking){
        double priceEuro = CurrencyConverter.sekToEuroConverter(booking.getPrice());

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

    //TODO: remove?
    private GymCustomer findCustomer(Long id){
        Optional<GymCustomer> findCustomer = gymCustomerRepository.findById(id);
        if(findCustomer.isEmpty()){
            throw new ResourceNotFoundException("GymCustomer","id",id);
        }
        return findCustomer.get();
    }

    private GymCustomer findCustomer(String name){
        Optional<GymCustomer> findCustomer = Optional.ofNullable(gymCustomerRepository.findByUsername(name));
        if(findCustomer.isEmpty()){
            throw new ResourceNotFoundException("GymCustomer","username",name);
        }
        return findCustomer.get();
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
