package com.example.wigellgymservice.services;

import com.example.wigellgymservice.enums.TrainingType;
import com.example.wigellgymservice.exceptions.ResourceNotFoundException;
import com.example.wigellgymservice.services.externalAPI.CurrencyConverter;
import com.example.wigellgymservice.models.entities.GymBooking;
import com.example.wigellgymservice.models.entities.GymCustomer;
import com.example.wigellgymservice.models.entities.GymInstructor;
import com.example.wigellgymservice.models.entities.GymWorkout;
import com.example.wigellgymservice.repositories.GymBookingRepository;
import com.example.wigellgymservice.repositories.GymCustomerRepository;
import com.example.wigellgymservice.repositories.GymInstructorRepository;
import com.example.wigellgymservice.repositories.GymWorkoutRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
@Import(GymBookingServiceImplGymUserAndAdminControllerIntegrationTest.MockCurrencyConverterConfig.class)
class GymBookingServiceImplGymUserAndAdminControllerIntegrationTest {

    @Autowired
    private CurrencyConverter currencyConverter;
    @Autowired
    private GymWorkoutRepository gymWorkoutRepository;
    @Autowired
    private GymBookingRepository gymBookingRepository;
    @Autowired
    private GymCustomerRepository gymCustomerRepository;
    @Autowired
    private GymInstructorRepository gymInstructorRepository;

    @Autowired
    private MockMvc mockMvc;


    @TestConfiguration
    static class MockCurrencyConverterConfig {
        public static CurrencyConverter currencyConverterMock;

        @Bean
        @Primary
        public CurrencyConverter currencyConverter(){
            currencyConverterMock = Mockito.mock(CurrencyConverter.class);
            Mockito.when(currencyConverterMock.sekToEuroConverter(Mockito.anyDouble())).thenReturn(10.0);
            return currencyConverterMock;
        }
    }

    private LocalDateTime now = LocalDateTime.now();
    private LocalDateTime oneDayAfterNow = LocalDateTime.now().plusDays(1);
    private LocalDateTime beforeNow = now.minusDays(3);
    private LocalDateTime aftereNow = now.plusDays(3);

    private GymCustomer gymCustomer1;
    private GymCustomer gymCustomer2;
    private GymCustomer gymCustomer3;
    private GymCustomer gymCustomer4;
    private GymCustomer gymCustomerAllTrue;

    private GymInstructor gymInstructor1;
    private GymInstructor gymInstructor2;
    private GymInstructor gymInstructor3;

    private GymWorkout gymWorkout1;
    private GymWorkout gymWorkout2;
    private GymWorkout gymWorkout3;

    private GymBooking gymBooking1;
    private GymBooking gymBooking2;
    private GymBooking gymBooking3;
    private GymBooking gymBookingAllTrue;



    @BeforeEach
    void setUp(){

        gymBookingRepository.deleteAll();
        gymWorkoutRepository.deleteAll();
        gymCustomerRepository.deleteAll();
        gymInstructorRepository.deleteAll();

        Mockito.reset(MockCurrencyConverterConfig.currencyConverterMock);
        Mockito.when(MockCurrencyConverterConfig.currencyConverterMock.sekToEuroConverter(Mockito.anyDouble()))
                .thenReturn(10.0);


        gymCustomer1 = new GymCustomer("mia", true);
        gymCustomer2 = new GymCustomer("helen", true);
        gymCustomer3 = new GymCustomer("will", false);
        gymCustomer4 = new GymCustomer("pelle", true);
        gymCustomerAllTrue = new GymCustomer("quill", true);

        gymInstructor1 = new GymInstructor("Clara Klarkson", TrainingType.DANCE, true);
        gymInstructor2 = new GymInstructor("Klark Ohlssons", TrainingType.YOGA, false);
        gymInstructor3 = new GymInstructor("George Skog", TrainingType.STRENGTH, true);

        gymWorkout1 = new GymWorkout("Bugg", TrainingType.DANCE, 20, 175.99, gymInstructor1, beforeNow, true);
        gymWorkout2 = new GymWorkout( "Flexibility", TrainingType.YOGA, 12, 125.89, gymInstructor2, oneDayAfterNow, false);
        gymWorkout3 = new GymWorkout( "Heavy lifting", TrainingType.STRENGTH, 8, 229.99,gymInstructor3, aftereNow, true);

        gymBooking1 = new GymBooking(gymCustomer1, gymWorkout1,LocalDateTime.now(), gymWorkout1.getPrice()+40, true);
        gymBooking2 = new GymBooking(gymCustomer2, gymWorkout2,LocalDateTime.now(), gymWorkout2.getPrice()+40, false);
        gymBooking3 = new GymBooking(gymCustomer3, gymWorkout3,LocalDateTime.now(), gymWorkout3.getPrice()+40, true);
        gymBookingAllTrue = new GymBooking(gymCustomerAllTrue, gymWorkout3,LocalDateTime.now(), gymWorkout3.getPrice()+40, true);

        gymCustomer1.getGymBookings().add(gymBooking1);
        gymCustomer2.getGymBookings().add(gymBooking2);
        gymCustomer3.getGymBookings().add(gymBooking3);
        gymCustomerAllTrue.getGymBookings().add(gymBookingAllTrue);

        gymInstructor1.getGymWorkouts().add(gymWorkout1);
        gymInstructor2.getGymWorkouts().add(gymWorkout2);
        gymInstructor3.getGymWorkouts().add(gymWorkout3);

        gymCustomerRepository.save(gymCustomer1);
        gymCustomerRepository.save(gymCustomer2);
        gymCustomerRepository.save(gymCustomer3);
        gymCustomerRepository.save(gymCustomer4);
        gymCustomerRepository.save(gymCustomerAllTrue);

        gymInstructorRepository.save(gymInstructor1);
        gymInstructorRepository.save(gymInstructor2);
        gymInstructorRepository.save(gymInstructor3);

        gymWorkout1.getGymBookings().add(gymBooking1);
        gymWorkout2.getGymBookings().add(gymBooking2);
        gymWorkout3.getGymBookings().add(gymBooking3);
        gymWorkout3.getGymBookings().add(gymBookingAllTrue);

        gymWorkoutRepository.save(gymWorkout1);
        gymWorkoutRepository.save(gymWorkout2);
        gymWorkoutRepository.save(gymWorkout3);

        gymBookingRepository.save(gymBooking1);
        gymBookingRepository.save(gymBooking2);
        gymBookingRepository.save(gymBooking3);
        gymBookingRepository.save(gymBookingAllTrue);


    }



    ///----Admin----
    /// getCancelledGymBookings
    @Test
    void getCancelledGymBookings_ShouldReturnCancelledGymBookings() throws Exception {
        mockMvc.perform(get("/wigellgym/listcanceled")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].gymCustomer.username").value("helen"))
                .andExpect(jsonPath("$[0].gymCustomer.isActive").value(true))

                .andExpect(jsonPath("$[0].gymWorkout.name").value("Flexibility"))
                .andExpect(jsonPath("$[0].gymWorkout.trainingType").value("YOGA"))
                .andExpect(jsonPath("$[0].gymWorkout.maxParticipants").value(12))
                .andExpect(jsonPath("$[0].gymWorkout.price").value(125.89))
                .andExpect(jsonPath("$[0].gymWorkout.gymInstructor.gymInstructorName").value("Klark Ohlssons"))
                .andExpect(jsonPath("$[0].gymWorkout.gymInstructor.trainingType").value("YOGA"))
                .andExpect(jsonPath("$[0].gymWorkout.gymInstructor.isActive").value(false))
                .andExpect(jsonPath("$[0].gymWorkout.dateTime").value(oneDayAfterNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$[0].gymWorkout.isActive").value(false))

                .andExpect(jsonPath("$[0].bookingDate").exists())
                .andExpect(jsonPath("$[0].priceSek").value(165.89))
                .andExpect(jsonPath("$[0].priceEuro").exists())
                .andExpect(jsonPath("$[0].priceEuro").value(10.0))
                .andExpect(jsonPath("$[0].isActive").value(false));
    }
    @Test
    void getCancelledGymBookings_ShouldThrowIfThereAreNoCancelledBookings() throws Exception {
        gymBooking2.setActive(true);
        gymBookingRepository.save(gymBooking2);

        mockMvc.perform(get("/wigellgym/listcanceled")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }



    ///upComingGymBookings
    @Test
    void upComingGymBookings_ShouldReturnUpcomingGymBookings() throws Exception {
        gymCustomer3.setActive(true);
        gymCustomerRepository.save(gymCustomer3);

        mockMvc.perform(get("/wigellgym/listupcoming")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$[0].gymCustomer.username").value("will"))
                .andExpect(jsonPath("$[0].gymCustomer.isActive").value(true))

                .andExpect(jsonPath("$[0].gymWorkout.name").value("Heavy lifting"))
                .andExpect(jsonPath("$[0].gymWorkout.trainingType").value("STRENGTH"))
                .andExpect(jsonPath("$[0].gymWorkout.maxParticipants").value(8))
                .andExpect(jsonPath("$[0].gymWorkout.price").value(229.99))
                .andExpect(jsonPath("$[0].gymWorkout.gymInstructor.gymInstructorName").value("George Skog"))
                .andExpect(jsonPath("$[0].gymWorkout.gymInstructor.trainingType").value("STRENGTH"))
                .andExpect(jsonPath("$[0].gymWorkout.gymInstructor.isActive").value(true))
                .andExpect(jsonPath("$[0].gymWorkout.dateTime").value(aftereNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$[0].gymWorkout.isActive").value(true))

                .andExpect(jsonPath("$[0].bookingDate").exists())
                .andExpect(jsonPath("$[0].priceSek").value(269.99))
                .andExpect(jsonPath("$[0].priceEuro").exists())
                .andExpect(jsonPath("$[0].priceEuro").value(10.0))
                .andExpect(jsonPath("$[0].isActive").value(true));

    }

    @Test
    void upComingGymBookings_ShouldThrowIfThereAreNoActiveBookings() throws Exception {
        gymBooking1.setActive(false);
        gymBooking2.setActive(false);
        gymBooking3.setActive(false);
        gymBookingAllTrue.setActive(false);
        gymBookingRepository.saveAll(List.of(gymBooking1, gymBooking2, gymBooking3, gymBookingAllTrue));

        mockMvc.perform(get("/wigellgym/listupcoming")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void upComingGymBookings_ShouldThrowIfThereAreNoActiveBookingsSetAfterNow() throws Exception {
        gymWorkout3.setDateTime(beforeNow);
        gymWorkout2.setDateTime(beforeNow);
        gymWorkout1.setDateTime(beforeNow);
        gymWorkoutRepository.saveAll(List.of(gymWorkout1, gymWorkout2, gymWorkout3));
        mockMvc.perform(get("/wigellgym/listupcoming")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }




    ///pastGymBookings
    @Test
    void pastGymBookings_ShouldReturnPastGymBookings() throws Exception {
        mockMvc.perform(get("/wigellgym/listpast")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$[0].gymCustomer.username").value("mia"))
                .andExpect(jsonPath("$[0].gymCustomer.isActive").value(true))

                .andExpect(jsonPath("$[0].gymWorkout.name").value("Bugg"))
                .andExpect(jsonPath("$[0].gymWorkout.trainingType").value("DANCE"))
                .andExpect(jsonPath("$[0].gymWorkout.maxParticipants").value(20))
                .andExpect(jsonPath("$[0].gymWorkout.price").value(175.99))
                .andExpect(jsonPath("$[0].gymWorkout.gymInstructor.gymInstructorName").value("Clara Klarkson"))
                .andExpect(jsonPath("$[0].gymWorkout.gymInstructor.trainingType").value("DANCE"))
                .andExpect(jsonPath("$[0].gymWorkout.gymInstructor.isActive").value(true))
                .andExpect(jsonPath("$[0].gymWorkout.dateTime").value(beforeNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$[0].gymWorkout.isActive").value(true))

                .andExpect(jsonPath("$[0].bookingDate").exists())
                .andExpect(jsonPath("$[0].priceSek").value(215.99))
                .andExpect(jsonPath("$[0].priceEuro").exists())
                .andExpect(jsonPath("$[0].priceEuro").value(10.0))
                .andExpect(jsonPath("$[0].isActive").value(true));
    }

    @Test
    void pastGymBookings_ShouldThrowIfNoWorkoutBeforeNow() throws Exception {
        gymWorkout1.setDateTime(aftereNow);
        gymWorkoutRepository.save(gymWorkout1);

        mockMvc.perform(get("/wigellgym/listpast")
                .with(user("admin").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void pastGymBookings_ShouldThrowIfActiveBookingsOnPastWorkouts() throws Exception {
        gymBooking1.setActive(false);
        gymBookingRepository.save(gymBooking1);

        mockMvc.perform(get("/wigellgym/listpast")
                        .with(user("admin").roles("ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }







    ///----User----
    ///getUserGymBookings
    @Test
    void getUserGymBookings_ReturnBookingAsDTOs()throws Exception {

        mockMvc.perform(get("/wigellgym/mybookings")
                        .with(user("mia").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$[0].gymCustomer.username").value("mia"))
                .andExpect(jsonPath("$[0].gymCustomer.isActive").value(true))

                .andExpect(jsonPath("$[0].gymWorkout.name").value("Bugg"))
                .andExpect(jsonPath("$[0].gymWorkout.trainingType").value("DANCE"))
                .andExpect(jsonPath("$[0].gymWorkout.maxParticipants").value(20))
                .andExpect(jsonPath("$[0].gymWorkout.price").value(175.99))
                .andExpect(jsonPath("$[0].gymWorkout.gymInstructor.gymInstructorName").value("Clara Klarkson"))
                .andExpect(jsonPath("$[0].gymWorkout.gymInstructor.trainingType").value("DANCE"))
                .andExpect(jsonPath("$[0].gymWorkout.gymInstructor.isActive").value(true))
                .andExpect(jsonPath("$[0].gymWorkout.dateTime").value(beforeNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                .andExpect(jsonPath("$[0].gymWorkout.isActive").value(true))

                .andExpect(jsonPath("$[0].bookingDate").exists())
                .andExpect(jsonPath("$[0].priceSek").value(215.99))
                .andExpect(jsonPath("$[0].priceEuro").exists())
                .andExpect(jsonPath("$[0].priceEuro").value(10.0))
                .andExpect(jsonPath("$[0].isActive").value(true));


    }

    @Test
    void getUserGymBookings_ShouldThrowIfCustomerIsInactive() throws Exception {
        mockMvc.perform(get("/wigellgym/mybookings")
                        .with(user("will").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ResponseStatusException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"Customer with username [will] is not active\"", Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void getUserGymBookings_ShouldThrowIfCustomerNotFound() throws Exception {
        mockMvc.perform(get("/wigellgym/mybookings")
                        .with(user("max").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ResourceNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("No GymCustomer with username [max] found", Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void getUserGymBookings_ShouldThrowIfCustomerBookingsEmpty() throws Exception{
        mockMvc.perform(get("/wigellgym/mybookings")
                        .with(user("pelle").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void getUserGymBookings_ShouldThrowIfBookingPriceIsLessThan40() throws Exception{
        gymBooking1.setPrice(39.99);
        gymBookingRepository.save(gymBooking1);

        mockMvc.perform(get("/wigellgym/mybookings")
                        .with(user("mia").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ResponseStatusException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"Price sek cannot be less than 40 (the booking fee)\"", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getUserGymBookings_ShouldThrowIfApiFails() throws Exception {
        Mockito.when(currencyConverter.sekToEuroConverter(Mockito.anyDouble())).thenReturn(0.0);

        mockMvc.perform(get("/wigellgym/mybookings")
                        .with(user("mia").roles("USER")))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ResponseStatusException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("404 NOT_FOUND \"Currency conversion failed. \nSek: "+gymBooking1.getPrice()+"\"", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }







    ///bookWorkout
    @Test
    void bookWorkout_ShouldReturnDTOBooking() throws Exception{
        gymCustomer1.getGymBookings().remove(gymBooking1);
        gymCustomer1.getGymBookings().remove(gymBooking2);
        gymCustomer1.getGymBookings().remove(gymBooking3);
        gymCustomerRepository.save(gymCustomer1);

        mockMvc.perform(post("/wigellgym/bookworkout/"+gymWorkout3.getGymWorkoutId())
                        .with(user("mia").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.gymCustomer.gymCustomerId").value(gymCustomer1.getGymCustomerId()))
                        .andExpect(jsonPath("$.gymCustomer.username").value("mia"))
                        .andExpect(jsonPath("$.gymCustomer.isActive").value(true))

                        .andExpect(jsonPath("$.gymWorkout.gymWorkoutId").value(gymWorkout3.getGymWorkoutId()))
                        .andExpect(jsonPath("$.gymWorkout.name").value("Heavy lifting"))
                        .andExpect(jsonPath("$.gymWorkout.trainingType").value("STRENGTH"))
                        .andExpect(jsonPath("$.gymWorkout.maxParticipants").value(8))
                        .andExpect(jsonPath("$.gymWorkout.price").value(229.99))
                            .andExpect(jsonPath("$.gymWorkout.gymInstructor.gymInstructorId").value(gymInstructor3.getGymInstructorId()))
                            .andExpect(jsonPath("$.gymWorkout.gymInstructor.gymInstructorName").value("George Skog"))
                            .andExpect(jsonPath("$.gymWorkout.gymInstructor.trainingType").value("STRENGTH"))
                            .andExpect(jsonPath("$.gymWorkout.gymInstructor.isActive").value(true))
                        .andExpect(jsonPath("$.gymWorkout.dateTime").value(aftereNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                        .andExpect(jsonPath("$.gymWorkout.isActive").value(true))
                .andExpect(jsonPath("$.bookingDate").exists())
                .andExpect(jsonPath("$.priceSek").value(269.99))
                .andExpect(jsonPath("$.priceEuro").exists())
                .andExpect(jsonPath("$.priceEuro").value(10.0))
                .andExpect(jsonPath("$.isActive").value(true));


    }

    @Test
    void bookWorkout_ShouldThrowIfCustomerNotFound() throws Exception {
        mockMvc.perform(post("/wigellgym/bookworkout/"+gymWorkout3.getGymWorkoutId())
                        .with(user("mille").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ResourceNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("No GymCustomer with username [mille] found", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void bookWorkout_ShouldThrowIfCustomerInInactive() throws Exception {
        mockMvc.perform(post("/wigellgym/bookworkout/"+gymWorkout3.getGymWorkoutId())
                        .with(user("will").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ResponseStatusException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"Customer with username [will] is not active\"", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }


    @Test
    void bookWorkout_ShouldThrowIfWorkoutNotFound() throws Exception {
        long falseId = gymWorkout3.getGymWorkoutId()+10;
        mockMvc.perform(post("/wigellgym/bookworkout/"+falseId)
                        .with(user("mia").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ResourceNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("No GymWorkout with id ["+falseId+"] found", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void bookWorkout_ShouldThrowIfWorkoutIsInactive() throws Exception{
        gymWorkout3.setActive(false);
        gymWorkoutRepository.save(gymWorkout3);

        mockMvc.perform(post("/wigellgym/bookworkout/"+gymWorkout3.getGymWorkoutId())
                        .with(user("mia").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ResponseStatusException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"Workout is not active\"", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }


    @Test
    void bookWorkout_ShouldThrowIfWorkoutAlreadyHappened() throws Exception {
        mockMvc.perform(post("/wigellgym/bookworkout/"+gymWorkout1.getGymWorkoutId())
                .with(user("helen").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ResponseStatusException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"Workout has already happened/started\"", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }


    @Test
    void bookWorkout_ShouldThrowIfCustomerAlreadyBookedWorkout() throws Exception {
        gymWorkout1.setDateTime(aftereNow);
        gymWorkoutRepository.save(gymWorkout1);

        mockMvc.perform(post("/wigellgym/bookworkout/"+gymWorkout1.getGymWorkoutId())
                        .with(user("mia").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ResponseStatusException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"You have already booked this workout\"", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void bookWorkout_ShouldThrowIfCustomerHasAnotherWorkoutBookedWithinPlus60min() throws Exception {
        gymWorkout1.setDateTime(aftereNow.plusMinutes(59));
        gymWorkoutRepository.save(gymWorkout1);

        mockMvc.perform(post("/wigellgym/bookworkout/"+gymWorkout3.getGymWorkoutId())
                        .with(user("mia").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ResponseStatusException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"You have already booked a workout at this time. Each workout is 60min\"", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void bookWorkout_ShouldThrowIfCustomerHasAnotherWorkoutBookedWithinMinus60min() throws Exception {
        gymWorkout1.setDateTime(aftereNow.minusMinutes(59));
        gymWorkoutRepository.save(gymWorkout1);

        mockMvc.perform(post("/wigellgym/bookworkout/"+gymWorkout3.getGymWorkoutId())
                        .with(user("mia").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ResponseStatusException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"You have already booked a workout at this time. Each workout is 60min\"", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void bookWorkout_ShouldThrowIfWorkoutIsFullyBooked() throws Exception {
        gymCustomer1.getGymBookings().remove(gymBooking1);
        gymCustomer1.getGymBookings().remove(gymBooking2);
        gymCustomer1.getGymBookings().remove(gymBooking3);
        gymCustomerRepository.save(gymCustomer1);

        gymWorkout3.setMaxParticipants(1);
        gymWorkoutRepository.save(gymWorkout3);

        mockMvc.perform(post("/wigellgym/bookworkout/"+gymWorkout3.getGymWorkoutId())
                        .with(user("mia").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ResponseStatusException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"Maximum number of participants exceeded\"", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }







    ///cancelBookingOnWorkout
    @Test
    void cancelBookingOnWorkout_ShouldSetBookingToInactive() throws Exception {

        mockMvc.perform(put("/wigellgym/cancelworkout/"+gymBookingAllTrue.getGymBookingId())
                .with(user("quill").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Booking successfully cancelled"));
    }

    @Test
    void cancelBookingOnWorkout_ShouldThrowIfCustomerNotFound() throws Exception {
        mockMvc.perform(put("/wigellgym/cancelworkout/"+gymBooking1.getGymBookingId())
                .with(user("mille").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ResourceNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("No GymCustomer with username [mille] found", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void cancelBookingOnWorkout_ShouldThrowIfCustomerIsInactive() throws Exception {
        mockMvc.perform(put("/wigellgym/cancelworkout/"+gymBooking3.getGymBookingId())
                        .with(user("will").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ResponseStatusException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"Customer with username [will] is not active\"", Objects.requireNonNull(result.getResolvedException()).getMessage()));

    }

    @Test
    void cancelBookingOnWorkout_ShouldThrowIfBookingNotFound() throws Exception {
        long falseId = gymWorkout3.getGymWorkoutId()+10;
        mockMvc.perform(put("/wigellgym/cancelworkout/"+falseId)
                        .with(user("mia").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertInstanceOf(ResourceNotFoundException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("No GymBooking with id ["+falseId+"] found", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void cancelBookingOnWorkout_ShouldThrowIfBookingIsNotToUser() throws Exception {
        mockMvc.perform(put("/wigellgym/cancelworkout/"+gymBooking3.getGymBookingId())
                .with(user("mia").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ResponseStatusException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"You are not allowed to cancel this booking. Customer username mismatch\"", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }


    @Test
    void cancelBookingOnWorkout_ShouldThrowIfWorkoutHappenedLessThanOneDayFromAttempt() throws Exception {
        gymWorkout1.setDateTime(now.plusHours(23));
        gymWorkoutRepository.save(gymWorkout1);

        mockMvc.perform(put("/wigellgym/cancelworkout/"+gymBooking1.getGymBookingId())
                        .with(user("mia").roles("USER"))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertInstanceOf(ResponseStatusException.class, result.getResolvedException()))
                .andExpect(result -> assertEquals("400 BAD_REQUEST \"Too late to cancel. Cancellation must be at least 24 hours before the workout will take place\"", Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }


}