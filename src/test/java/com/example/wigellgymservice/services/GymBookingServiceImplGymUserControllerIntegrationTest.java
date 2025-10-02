package com.example.wigellgymservice.services;

import com.example.wigellgymservice.controllers.GymUserController;
import com.example.wigellgymservice.enums.TrainingType;
import com.example.wigellgymservice.models.DTO.DTOGymWorkout;
import com.example.wigellgymservice.models.entities.GymBooking;
import com.example.wigellgymservice.models.entities.GymCustomer;
import com.example.wigellgymservice.models.entities.GymInstructor;
import com.example.wigellgymservice.models.entities.GymWorkout;
import com.example.wigellgymservice.repositories.GymBookingRepository;
import com.example.wigellgymservice.repositories.GymCustomerRepository;
import com.example.wigellgymservice.repositories.GymInstructorRepository;
import com.example.wigellgymservice.repositories.GymWorkoutRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;


@ExtendWith(MockitoExtension.class)
class GymBookingServiceImplGymUserControllerIntegrationTest {
    @Mock
    private GymWorkoutRepository gymWorkoutRepository;
    @Mock
    private GymBookingRepository gymBookingRepository;
    @Mock
    private GymCustomerRepository gymCustomerRepository;
    @Mock
    private GymInstructorRepository gymInstructorRepository;

    @InjectMocks
    private GymBookingServiceImpl gymBookingService;

    @InjectMocks
    private GymInstructorServiceImpl gymInstructorService;

    @InjectMocks
    private GymWorkoutServiceImpl gymWorkoutService;


    private GymUserController userController;


    private MockMvc mockMvcUser;
    private ObjectMapper objectMapper;

    private List<GymWorkout> gymWorkouts;
    private List<GymBooking> gymBookings;
    private List<GymInstructor> gymInstructors;
    private List<GymCustomer> gymCustomers;

    private LocalDateTime now = LocalDateTime.now();
    private LocalDateTime oneDayAfterNow = LocalDateTime.now().plusDays(1);
    private LocalDateTime beforeNow = now.minusDays(3);
    private LocalDateTime aftereNow = now.plusDays(3);
    private LocalDateTime aftereNow2 = now.plusDays(4);

    private GymCustomer gymCustomer1;
    private GymCustomer gymCustomer2;
    private GymCustomer gymCustomer3;

    private GymInstructor gymInstructor1;
    private GymInstructor gymInstructor2;
    private GymInstructor gymInstructor3;

    private GymWorkout gymWorkout1;
    private GymWorkout gymWorkout2;
    private GymWorkout gymWorkout3;

    private GymBooking gymBooking1;
    private GymBooking gymBooking2;
    private GymBooking gymBooking3;

    private DTOGymWorkout dtoGymWorkout;
    private DTOGymWorkout update;

    private Principal mockPrincipal = mock(Principal.class);
    private Authentication mockAuthentication = mock(Authentication.class);


    @BeforeEach
    void setUp(){
        userController = new GymUserController(gymBookingService, gymWorkoutService);

        mockMvcUser = MockMvcBuilders
                .standaloneSetup(userController)
                .build();
        objectMapper = new ObjectMapper();

        gymWorkouts = new ArrayList<>();
        gymBookings = new ArrayList<>();
        gymInstructors = new ArrayList<>();
        gymCustomers = new ArrayList<>();

        gymCustomer1 = new GymCustomer(1L, "mia", true);
        gymCustomer2 = new GymCustomer(2L, "helen", true);
        gymCustomer3 = new GymCustomer(3L, "will", false);

        gymInstructor1 = new GymInstructor(1L, "Clara Klarkson", TrainingType.DANCE, true);
        gymInstructor2 = new GymInstructor(2L, "Klark Ohlssons", TrainingType.YOGA, false);
        gymInstructor3 = new GymInstructor(3L, "George Skog", TrainingType.STRENGTH, true);

        gymWorkout1 = new GymWorkout(1L, "Bugg", TrainingType.DANCE, 20, 175.99, gymInstructor1, beforeNow, true);
        gymWorkout2 = new GymWorkout(2L, "Bugg", TrainingType.YOGA, 12, 125.89, gymInstructor2, oneDayAfterNow, false);
        gymWorkout3 = new GymWorkout(3L, "Bugg", TrainingType.STRENGTH, 8, 229.99,gymInstructor3, aftereNow, true);

        gymBooking1 = new GymBooking(1L,gymCustomer1, gymWorkout1,LocalDateTime.now(), gymWorkout1.getPrice()+40, true);
        gymBooking2 = new GymBooking(2L,gymCustomer2, gymWorkout2,LocalDateTime.now(), gymWorkout2.getPrice()+40, true);
        gymBooking3 = new GymBooking(3L,gymCustomer3, gymWorkout3,LocalDateTime.now(), gymWorkout3.getPrice()+40, true);

        gymCustomer1.getGymBookings().add(gymBooking1);
        gymCustomer2.getGymBookings().add(gymBooking2);
        gymCustomer3.getGymBookings().add(gymBooking3);

        gymInstructor1.getGymWorkouts().add(gymWorkout1);
        gymInstructor2.getGymWorkouts().add(gymWorkout2);
        gymInstructor3.getGymWorkouts().add(gymWorkout3);

        gymWorkout1.getGymBookings().add(gymBooking1);
        gymWorkout2.getGymBookings().add(gymBooking2);
        gymWorkout3.getGymBookings().add(gymBooking3);

        dtoGymWorkout = new DTOGymWorkout("Tango", "Dance", 18, 185.99, aftereNow,true);
        update = new DTOGymWorkout("Tango", "Dance", 10, 240.00,  aftereNow2, true);

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_USER");
        List<GrantedAuthority> authorities = List.of(authority);
        lenient().when(mockAuthentication.getAuthorities()).thenReturn((Collection) authorities);


    }
    @Test
    void bookWorkout_ShouldReturnDTOBooking() throws Exception{
        //customer 1 wants to book workout 3
        when(gymCustomerRepository.findByUsername("mia")).thenReturn(gymCustomer1);
        when(gymWorkoutRepository.findById(3L)).thenReturn(Optional.of(gymWorkout3));
        when(gymBookingRepository.findAllByIsActiveTrueAndGymCustomer(gymCustomer1)).thenReturn(List.of(gymBooking1));
        when(gymBookingRepository.findAllByIsActiveTrueAndGymWorkout(gymWorkout3)).thenReturn(List.of(gymBooking3));


        mockMvcUser.perform(post("/wigellgym/bookworkout/3")
                            .with(user("mia").roles("USER"))
                            .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.gymCustomer.gymCustomerId").value(1L))
                        .andExpect(jsonPath("$.gymWorkout.gymWorkoutId").value(3L));


    }

    @Test
    void bookWorkout() {
    }

    @Test
    void cancelBookingOnWorkout() {
    }




    ///getUserGymBookings
    @Test
    void getUserGymBookings_ReturnBookingAsDTOs()throws Exception {
        when(gymCustomerRepository.findByUsername(gymCustomer1.getUsername())).thenReturn(gymCustomer1);
        when(gymBookingRepository.findAllByGymCustomer(gymCustomer1)).thenReturn(List.of(gymBooking1));

        mockPrincipal = () -> gymCustomer1.getUsername();

        mockMvcUser.perform(get("/wigellgym/mybookings")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$[0].gymBookingId").value(1L))
                    .andExpect(jsonPath("$[0].gymCustomer.gymCustomerId").value(1L))
                    .andExpect(jsonPath("$[0].gymCustomer.username").value("mia"))
                    .andExpect(jsonPath("$[0].gymCustomer.isActive").value(true))

                    .andExpect(jsonPath("$[0].gymWorkout.gymWorkoutId").value(1L))
                    .andExpect(jsonPath("$[0].gymWorkout.name").value("Bugg"))
                    .andExpect(jsonPath("$[0].gymWorkout.trainingType").value("DANCE"))
                    .andExpect(jsonPath("$[0].gymWorkout.maxParticipants").value(20))
                    .andExpect(jsonPath("$[0].gymWorkout.price").value(175.99))
                        .andExpect(jsonPath("$[0].gymWorkout.gymInstructor.gymInstructorId").value(1L))
                        .andExpect(jsonPath("$[0].gymWorkout.gymInstructor.gymInstructorName").value("Clara Klarkson"))
                        .andExpect(jsonPath("$[0].gymWorkout.gymInstructor.trainingType").value("DANCE"))
                        .andExpect(jsonPath("$[0].gymWorkout.gymInstructor.isActive").value(true))
                    .andExpect(jsonPath("$[0].gymWorkout.dateTime").value(beforeNow.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
                    .andExpect(jsonPath("$[0].gymWorkout.isActive").value(true))

                .andExpect(jsonPath("$[0].bookingDate").exists())
                .andExpect(jsonPath("$[0].priceSek").value(215.99))
                .andExpect(jsonPath("$[0].priceEuro").exists())
                .andExpect(jsonPath("$[0].isActive").value(true));


    }

    @Test
    void getUserGymBookings_ShouldThrowIfCustomerNotFound() throws Exception {

        when(gymCustomerRepository.findByUsername("max")).thenReturn(null);
        mockPrincipal = () -> "max";
        mockMvcUser.perform(get("/wigellgym/mybookings")
                    .principal(mockPrincipal)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

    }

    @Test
    void getUserGymBookings_ShouldThrowIfCustomerBookingsEmpty() throws Exception{
        when(gymCustomerRepository.findByUsername(gymCustomer1.getUsername())).thenReturn(gymCustomer1);
        when(gymBookingRepository.findAllByGymCustomer(gymCustomer1)).thenReturn(List.of());

        mockPrincipal = () -> gymCustomer1.getUsername();
        mockMvcUser.perform(get("/wigellgym/mybookings")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    //TODO: ask how to check
    @Test
    void getUserGymBookings_ShouldThrowIfApiSekToEuroFails() throws Exception{
        gymBooking1.setPrice(39.99);
        when(gymCustomerRepository.findByUsername(gymCustomer1.getUsername())).thenReturn(gymCustomer1);
        when(gymBookingRepository.findAllByGymCustomer(gymCustomer1)).thenReturn(List.of(gymBooking1));

        mockPrincipal = () -> gymCustomer1.getUsername();
        mockMvcUser.perform(get("/wigellgym/mybookings")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Price sek cannot be less then 40 (the booking fee)"));
    }

}