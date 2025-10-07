package com.example.wigellgymservice.services;

import com.example.wigellgymservice.enums.TrainingType;
import com.example.wigellgymservice.exceptions.ContentNotFoundException;
import com.example.wigellgymservice.exceptions.ResourceNotFoundException;
import com.example.wigellgymservice.models.DTO.DTOGymWorkout;
import com.example.wigellgymservice.models.entities.GymBooking;
import com.example.wigellgymservice.models.entities.GymCustomer;
import com.example.wigellgymservice.models.entities.GymInstructor;
import com.example.wigellgymservice.models.entities.GymWorkout;
import com.example.wigellgymservice.repositories.GymBookingRepository;
import com.example.wigellgymservice.repositories.GymInstructorRepository;
import com.example.wigellgymservice.repositories.GymWorkoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GymWorkoutServiceImplUnitTest {
    @Mock
    private GymWorkoutRepository gymWorkoutRepository;
    @Mock
    private GymBookingRepository gymBookingRepository;
    @Mock
    private GymInstructorRepository gymInstructorRepository;

    @InjectMocks
    private GymWorkoutServiceImpl gymWorkoutService;

    private List<GymWorkout> gymWorkouts;
    private List<GymBooking> gymBookings;

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

    private Authentication mockAuthentication = mock(Authentication.class);

    @BeforeEach
    void setUp() {
        gymWorkouts = new ArrayList<>();
        gymBookings = new ArrayList<>();

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

        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_ADMIN");
        List<GrantedAuthority> authorities = List.of(authority);
        lenient().when(mockAuthentication.getAuthorities()).thenReturn((Collection) authorities);


    }


    ///getAllGymWorkouts ----------------------------------
    @Test
    void getAllGymWorkouts_ShouldReturnAllActiveAndAvailableGymWorkouts() {
        gymWorkouts.add(gymWorkout3);
        gymBookings.add(gymBooking3);
        when(gymWorkoutRepository.findAllByDateTimeAfterAndIsActiveTrue(any(LocalDateTime.class)))
                .thenReturn(gymWorkouts);

        when(gymBookingRepository.findAllByIsActiveTrueAndGymWorkout(gymWorkout3))
                .thenReturn(gymBookings);


        assertEquals(gymWorkouts, gymWorkoutService.getAllGymWorkouts());
    }

    @Test
    void getAllGymWorkouts_ShouldThrowIfAllUpComingWorkoutsIsEmpty() {
        when(gymWorkoutRepository.findAllByDateTimeAfterAndIsActiveTrue(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        ContentNotFoundException contentNotFoundException = assertThrows(
                ContentNotFoundException.class,
                () -> gymWorkoutService.getAllGymWorkouts());

        assertEquals("No upcoming gym workouts found", contentNotFoundException.getMessage());

    }

    @Test
    void getAllGymWorkouts_ShouldNotShowWorkoutIfMaxParticipantsIsReached() {
        gymWorkout2.setActive(true);
        gymWorkout2.setMaxParticipants(1);
        gymBooking2.setActive(true);
        gymWorkouts.add(gymWorkout2);
        gymWorkouts.add(gymWorkout3);

        when(gymWorkoutRepository.findAllByDateTimeAfterAndIsActiveTrue(any(LocalDateTime.class)))
                .thenReturn(gymWorkouts);
        when(gymBookingRepository.findAllByIsActiveTrueAndGymWorkout(gymWorkout2)).thenReturn(List.of(gymBooking2));
        when(gymBookingRepository.findAllByIsActiveTrueAndGymWorkout(gymWorkout3)).thenReturn(List.of(gymBooking3));

        List<GymWorkout> gymWorkoutList = List.of(gymWorkout3);
        assertEquals(gymWorkoutList, gymWorkoutService.getAllGymWorkouts());
    }

    @Test
    void getAllGymWorkouts_ShouldThrowIfAvailableWorkoutsIsEmpty() {
        gymWorkout3.setMaxParticipants(1);
        gymWorkouts.add(gymWorkout3);
        when(gymWorkoutRepository.findAllByDateTimeAfterAndIsActiveTrue(any(LocalDateTime.class)))
                .thenReturn(gymWorkouts);
        when(gymBookingRepository.findAllByIsActiveTrueAndGymWorkout(gymWorkout3)).thenReturn(List.of(gymBooking3));

        ContentNotFoundException contentNotFoundException = assertThrows(
                ContentNotFoundException.class,
                () -> gymWorkoutService.getAllGymWorkouts());

        assertEquals("No upcoming available gym workouts found", contentNotFoundException.getMessage());
    }







    ///addGymWorkout -----------------------------
    @Test
    void addGymWorkout_ShouldAddGymWorkout() {
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.of(gymInstructor1));
        when(gymWorkoutRepository.findAllByGymInstructorAndIsActiveTrue(gymInstructor1)).thenReturn(List.of(gymWorkout1));

        GymWorkout newGymWorkout = gymWorkoutService.addGymWorkout(
                dtoGymWorkout,
                gymInstructor1.getGymInstructorId(),
                mockAuthentication
        );

        assertEquals(dtoGymWorkout.getName(), newGymWorkout.getName());
        assertEquals(TrainingType.DANCE, newGymWorkout.getTrainingType());
        assertEquals(dtoGymWorkout.getMaxParticipants(), newGymWorkout.getMaxParticipants());
        assertEquals(dtoGymWorkout.getPrice(), newGymWorkout.getPrice());
        assertEquals(dtoGymWorkout.getDateTime(), newGymWorkout.getDateTime());

        verify(gymWorkoutRepository).save(any(GymWorkout.class));
        verify(mockAuthentication).getName();
        verify(mockAuthentication).getAuthorities();
    }

    @Test
    void addGymWorkout_ShouldThrowIfGymInstructorIdIsNull(){
        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                ()-> gymWorkoutService.addGymWorkout(
                        dtoGymWorkout,
                        null,
                        mockAuthentication
                )
        );

        assertEquals("InstructorId cannot be null", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void addGymWorkout_ShouldThrowIfGymWorkoutNameIsNull(){
        dtoGymWorkout.setName(null);
        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                ()-> gymWorkoutService.addGymWorkout(
                        dtoGymWorkout,
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication
                )
        );

        assertEquals("GymWorkout name cannot be null", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void addGymWorkout_ShouldThrowIfGymWorkoutTrainingTypeIsNull(){
        dtoGymWorkout.setTrainingType(null);
        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                ()-> gymWorkoutService.addGymWorkout(
                        dtoGymWorkout,
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication
                )
        );

        assertEquals("GymWorkout trainingType cannot be null", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void addGymWorkout_ShouldThrowIfGymWorkoutMaxParticipantsIsZero(){
        dtoGymWorkout.setMaxParticipants(0);
        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                ()-> gymWorkoutService.addGymWorkout(
                        dtoGymWorkout,
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication
                )
        );

        assertEquals("GymWorkout maxParticipants cannot be zero", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void addGymWorkout_ShouldThrowIfGymWorkoutPriceIsZero(){
        dtoGymWorkout.setPrice(0.0);
        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                ()-> gymWorkoutService.addGymWorkout(
                        dtoGymWorkout,
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication
                )
        );

        assertEquals("GymWorkout price cannot be zero", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void addGymWorkout_ShouldThrowIfGymWorkoutDateTimeIsNull(){
        dtoGymWorkout.setDateTime(null);
        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                ()-> gymWorkoutService.addGymWorkout(
                        dtoGymWorkout,
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication
                )
        );

        assertEquals("GymWorkout datetime cannot be null", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void addGymWorkout_ShouldThrowIfGymInstructorIdNotFound(){
        when(gymInstructorRepository.findById(10L)).thenReturn(Optional.empty());
        ResourceNotFoundException resourceNotFoundException = assertThrows(
                ResourceNotFoundException.class,
                ()-> gymWorkoutService.addGymWorkout(
                        dtoGymWorkout,
                        10L,
                        mockAuthentication
                )
        );

        assertEquals("No GymInstructor with id [10] found", resourceNotFoundException.getMessage());

    }

    @Test
    void addGymWorkout_ShouldThrowIfTrainingTypeNotFound() {
        dtoGymWorkout.setTrainingType("invalid");
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.ofNullable(gymInstructor1));

        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                () -> gymWorkoutService.addGymWorkout(
                        dtoGymWorkout,
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication)
        );

        assertEquals("'invalid' is an invalid training type", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());

    }

    @Test
    void addGymWorkout_ShouldThrowIfNameIsEmpty(){
        dtoGymWorkout.setName("");
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.ofNullable(gymInstructor1));

        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                () -> gymWorkoutService.addGymWorkout(
                        dtoGymWorkout,
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication)
        );

        assertEquals("Empty gym workout name", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void addGymWorkout_ShouldThrowIfNameIsLessThanThreeCharacters(){
        dtoGymWorkout.setName("te");
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.ofNullable(gymInstructor1));

        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                () -> gymWorkoutService.addGymWorkout(
                        dtoGymWorkout,
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication)
        );

        assertEquals("To short workout name, has to be at least 3 characters", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void addGymWorkout_ShouldThrowIfMaxParticipantsIsLessThanOne(){
        dtoGymWorkout.setMaxParticipants(-2);
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.ofNullable(gymInstructor1));

        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                () -> gymWorkoutService.addGymWorkout(
                        dtoGymWorkout,
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication)
        );

        assertEquals("To low gym workout max participants, it has to be 1 or more", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void addGymWorkout_ShouldThrowIfPriceIsLessThan70(){
        dtoGymWorkout.setPrice(69.99);
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.ofNullable(gymInstructor1));

        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                () -> gymWorkoutService.addGymWorkout(
                        dtoGymWorkout,
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication)
        );

        assertEquals("To low gym workout price, it has to be more than 70.0", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void addGymWorkout_ShouldThrowIfDateTimeIsBeforeNow(){
        dtoGymWorkout.setDateTime(beforeNow);
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.ofNullable(gymInstructor1));

        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                () -> gymWorkoutService.addGymWorkout(
                        dtoGymWorkout,
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication)
        );

        assertEquals("Invalid gym workout date. Date/time already happened", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void addGymWorkout_ShouldThrowIfDateTimeIsLessThen30MinutesAfterNow(){
        dtoGymWorkout.setDateTime(now.plusMinutes(29));
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.ofNullable(gymInstructor1));

        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                () -> gymWorkoutService.addGymWorkout(
                        dtoGymWorkout,
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication)
        );

        assertEquals("Invalid gym workout date. Have to be at least 30 minutes after now", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void addGymWorkout_ShouldThrowIfInstructorIsInactive(){
        gymInstructor1.setActive(false);
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.ofNullable(gymInstructor1));

        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                () -> gymWorkoutService.addGymWorkout(
                        dtoGymWorkout,
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication)
        );

        assertEquals("GymInstructor with id '1' is not active", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void addGymWorkout_ShouldThrowIfInstructorIsBookedLessThan75MinutesBefore(){
        gymWorkout1.setDateTime(aftereNow.minusMinutes(74));
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.ofNullable(gymInstructor1));
        when(gymWorkoutRepository.findAllByGymInstructorAndIsActiveTrue(gymInstructor1)).thenReturn(List.of(gymWorkout1));
        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                () -> gymWorkoutService.addGymWorkout(
                        dtoGymWorkout,
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication)
        );

        assertEquals("Instructor is already booked around "+gymWorkout1.getDateTime() +". Bookings must be at least 1 hour and 15 minutes (75 minutes) apart", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void addGymWorkout_ShouldThrowIfInstructorIsBookedLessThan75MinutesAfter(){
        gymWorkout1.setDateTime(aftereNow.plusMinutes(74));
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.ofNullable(gymInstructor1));
        when(gymWorkoutRepository.findAllByGymInstructorAndIsActiveTrue(gymInstructor1)).thenReturn(List.of(gymWorkout1));
        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                () -> gymWorkoutService.addGymWorkout(
                        dtoGymWorkout,
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication)
        );

        assertEquals("Instructor is already booked around "+gymWorkout1.getDateTime() +". Bookings must be at least 1 hour and 15 minutes (75 minutes) apart", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }







    ///updateGymWorkout ----------------------------------------
    @Test
    void updateGymWorkout_ShouldUpdateWorkout(){
        when(gymWorkoutRepository.findById(gymWorkout3.getGymWorkoutId())).thenReturn(Optional.ofNullable(gymWorkout3));
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.ofNullable(gymInstructor1));

        GymWorkout oldGymWorkout = new GymWorkout();
        oldGymWorkout.setGymWorkoutId(gymWorkout3.getGymWorkoutId());
        oldGymWorkout.setName(gymWorkout3.getName());
        oldGymWorkout.setTrainingType(gymWorkout3.getTrainingType());
        oldGymWorkout.setMaxParticipants(gymWorkout3.getMaxParticipants());
        oldGymWorkout.setPrice(gymWorkout3.getPrice());
        oldGymWorkout.setDateTime(gymWorkout3.getDateTime());
        oldGymWorkout.setGymInstructor(gymWorkout3.getGymInstructor());
        oldGymWorkout.setActive(gymWorkout3.isActive());
        oldGymWorkout.setGymBookings(gymWorkout3.getGymBookings());

        GymWorkout updatedGymWorkout = gymWorkoutService.updateGymWorkout(
                update,
                gymWorkout3.getGymWorkoutId(),
                gymInstructor1.getGymInstructorId(),
                mockAuthentication
        );

        assertEquals(gymWorkout3.getGymWorkoutId(), updatedGymWorkout.getGymWorkoutId());
        assertEquals(gymWorkout3.isActive(), updatedGymWorkout.isActive());

        assertNotEquals(oldGymWorkout.getName(), updatedGymWorkout.getName());
        assertNotEquals(oldGymWorkout.getTrainingType(), updatedGymWorkout.getTrainingType());
        assertNotEquals(oldGymWorkout.getMaxParticipants(), updatedGymWorkout.getMaxParticipants());
        assertNotEquals(oldGymWorkout.getPrice(), updatedGymWorkout.getPrice());
        assertNotEquals(oldGymWorkout.getGymInstructor(), updatedGymWorkout.getGymInstructor());
        assertNotEquals(oldGymWorkout.getDateTime(), updatedGymWorkout.getDateTime());

        verify(gymWorkoutRepository).save(any(GymWorkout.class));
        verify(mockAuthentication).getName();
        verify(mockAuthentication).getAuthorities();

    }

    @Test
    void updateGymWorkout_ShouldThrowIfWorkoutIdNotFound(){
        when(gymWorkoutRepository.findById(10L)).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = assertThrows(
                ResourceNotFoundException.class,
                ()-> gymWorkoutService.updateGymWorkout(
                        update,
                        10L,
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication
                )
        );

        assertEquals("No GymWorkout with id [10] found", resourceNotFoundException.getMessage());
    }

    @Test
    void updateGymWorkout_ShouldThrowIfInstructorIdNotFound(){
        when(gymWorkoutRepository.findById(gymWorkout3.getGymWorkoutId())).thenReturn(Optional.of(gymWorkout3));
        when(gymInstructorRepository.findById(10L)).thenReturn(Optional.empty());

        ResourceNotFoundException resourceNotFoundException = assertThrows(
                ResourceNotFoundException.class,
                ()-> gymWorkoutService.updateGymWorkout(
                        update,
                        gymWorkout3.getGymWorkoutId(),
                        10L,
                        mockAuthentication
                )
        );

        assertEquals("No GymInstructor with id [10] found", resourceNotFoundException.getMessage());
    }

    @Test
    void updateGymWorkout_ShouldNotChangeValuesIfValueIsNull(){
        when(gymWorkoutRepository.findById(gymWorkout3.getGymWorkoutId())).thenReturn(Optional.of(gymWorkout3));
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.of(gymInstructor1));

        DTOGymWorkout emptyGymWorkout = new DTOGymWorkout(null, null, 0,0.0,null,false);

        GymWorkout oldGymWorkout = new GymWorkout();
        oldGymWorkout.setGymWorkoutId(gymWorkout3.getGymWorkoutId());
        oldGymWorkout.setName(gymWorkout3.getName());
        oldGymWorkout.setTrainingType(gymWorkout3.getTrainingType());
        oldGymWorkout.setMaxParticipants(gymWorkout3.getMaxParticipants());
        oldGymWorkout.setPrice(gymWorkout3.getPrice());
        oldGymWorkout.setDateTime(gymWorkout3.getDateTime());
        oldGymWorkout.setGymInstructor(gymWorkout3.getGymInstructor());
        oldGymWorkout.setActive(gymWorkout3.isActive());
        oldGymWorkout.setGymBookings(gymWorkout3.getGymBookings());

        GymWorkout notUpdatedGymWorkout = gymWorkoutService.updateGymWorkout(
                emptyGymWorkout,
                gymWorkout3.getGymWorkoutId(),
                gymInstructor1.getGymInstructorId(),
                mockAuthentication
        );

        assertEquals(notUpdatedGymWorkout.getGymWorkoutId(), oldGymWorkout.getGymWorkoutId());
        assertEquals(notUpdatedGymWorkout.isActive(), oldGymWorkout.isActive());
        assertEquals(notUpdatedGymWorkout.getName(), oldGymWorkout.getName());
        assertEquals(notUpdatedGymWorkout.getTrainingType(), oldGymWorkout.getTrainingType());
        assertEquals(notUpdatedGymWorkout.getMaxParticipants(), oldGymWorkout.getMaxParticipants());
        assertEquals(notUpdatedGymWorkout.getPrice(), oldGymWorkout.getPrice());

        verify(gymWorkoutRepository).save(any(GymWorkout.class));
        verify(mockAuthentication).getName();
        verify(mockAuthentication).getAuthorities();

    }

    @Test
    void updateGymWorkout_ShouldThrowIfNameIsEmpty(){
        update.setName("");
        when(gymWorkoutRepository.findById(gymWorkout3.getGymWorkoutId())).thenReturn(Optional.of(gymWorkout3));
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.of(gymInstructor1));

        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                ()-> gymWorkoutService.updateGymWorkout(
                        update,
                        gymWorkout3.getGymWorkoutId(),
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication
                )
        );

        assertEquals("Empty gym workout name", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void updateGymWorkout_ShouldThrowIfNameIsLessThanThreeCharacters(){
        update.setName("te");
        when(gymWorkoutRepository.findById(gymWorkout3.getGymWorkoutId())).thenReturn(Optional.of(gymWorkout3));
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.of(gymInstructor1));

        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                ()-> gymWorkoutService.updateGymWorkout(
                        update,
                        gymWorkout3.getGymWorkoutId(),
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication
                )
        );

        assertEquals("To short workout name, has to be at least 3 characters", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void updateGymWorkout_ShouldThrowIfMaxParticipantsIsLessThanOne(){
        update.setMaxParticipants(-2);
        when(gymWorkoutRepository.findById(gymWorkout3.getGymWorkoutId())).thenReturn(Optional.of(gymWorkout3));
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.of(gymInstructor1));

        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                ()-> gymWorkoutService.updateGymWorkout(
                        update,
                        gymWorkout3.getGymWorkoutId(),
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication
                )
        );

        assertEquals("To low gym workout max participants, it has to be 1 or more", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void updateGymWorkout_ShouldThrowIfPriceIsLessThan70(){
        update.setPrice(69.99);
        when(gymWorkoutRepository.findById(gymWorkout3.getGymWorkoutId())).thenReturn(Optional.of(gymWorkout3));
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.of(gymInstructor1));

        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                ()-> gymWorkoutService.updateGymWorkout(
                        update,
                        gymWorkout3.getGymWorkoutId(),
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication
                )
        );

        assertEquals("To low gym workout price, it has to be more than 70.0", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void updateGymWorkout_ShouldThrowIfDateTimeIsBeforeNow(){
        update.setDateTime(beforeNow);
        when(gymWorkoutRepository.findById(gymWorkout3.getGymWorkoutId())).thenReturn(Optional.of(gymWorkout3));
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.of(gymInstructor1));

        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                ()-> gymWorkoutService.updateGymWorkout(
                        update,
                        gymWorkout3.getGymWorkoutId(),
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication
                )
        );

        assertEquals("Invalid gym workout date. Date/time already happened", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void updateGymWorkout_ShouldThrowIfDateTimeIsLessThan30MinutesAfterNow(){
        update.setDateTime(now.plusMinutes(29));
        when(gymWorkoutRepository.findById(gymWorkout3.getGymWorkoutId())).thenReturn(Optional.of(gymWorkout3));
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.of(gymInstructor1));

        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                ()-> gymWorkoutService.updateGymWorkout(
                        update,
                        gymWorkout3.getGymWorkoutId(),
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication
                )
        );

        assertEquals("Invalid gym workout date. Have to be at least 30 minutes after now", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void updateGymWorkout_ShouldThrowIfInstructorIsInactive(){
        gymInstructor1.setActive(false);
        when(gymWorkoutRepository.findById(gymWorkout3.getGymWorkoutId())).thenReturn(Optional.of(gymWorkout3));
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.of(gymInstructor1));

        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                ()-> gymWorkoutService.updateGymWorkout(
                        update,
                        gymWorkout3.getGymWorkoutId(),
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication
                )
        );

        assertEquals("GymInstructor with id '1' is not active", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void updateGymWorkout_ShouldThrowIfInstructorIsBookedLessThan75MinutesAfter(){
        gymWorkout1.setDateTime(aftereNow2);
        update.setDateTime(aftereNow2.minusMinutes(74));
        when(gymWorkoutRepository.findById(gymWorkout3.getGymWorkoutId())).thenReturn(Optional.of(gymWorkout3));
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.of(gymInstructor1));
        when(gymWorkoutRepository.findAllByGymInstructorAndIsActiveTrue(gymInstructor1)).thenReturn(List.of(gymWorkout1));
        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                ()-> gymWorkoutService.updateGymWorkout(
                        update,
                        gymWorkout3.getGymWorkoutId(),
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication
                )
        );

        assertEquals("Instructor is already booked around "+aftereNow2+". Bookings must be at least 1 hour and 15 minutes (75 minutes) apart", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void updateGymWorkout_ShouldThrowIfInstructorIsBookedLessThan75MinutesBefore(){
        gymWorkout1.setDateTime(aftereNow2);
        update.setDateTime(aftereNow2.plusMinutes(74));
        when(gymWorkoutRepository.findById(gymWorkout3.getGymWorkoutId())).thenReturn(Optional.of(gymWorkout3));
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.of(gymInstructor1));
        when(gymWorkoutRepository.findAllByGymInstructorAndIsActiveTrue(gymInstructor1)).thenReturn(List.of(gymWorkout1));
        ResponseStatusException responseStatusException = assertThrows(
                ResponseStatusException.class,
                ()-> gymWorkoutService.updateGymWorkout(
                        update,
                        gymWorkout3.getGymWorkoutId(),
                        gymInstructor1.getGymInstructorId(),
                        mockAuthentication
                )
        );

        assertEquals("Instructor is already booked around "+aftereNow2+". Bookings must be at least 1 hour and 15 minutes (75 minutes) apart", responseStatusException.getReason());
        assertEquals(HttpStatus.BAD_REQUEST, responseStatusException.getStatusCode());
    }

    @Test
    void updateGymWorkout_ShouldNotCompareTimeWithWorkoutToUpdate(){
        gymInstructor1.getGymWorkouts().add(gymWorkout3);
        gymWorkout3.setGymInstructor(gymInstructor1);

        when(gymWorkoutRepository.findById(gymWorkout3.getGymWorkoutId())).thenReturn(Optional.of(gymWorkout3));
        when(gymInstructorRepository.findById(gymInstructor1.getGymInstructorId())).thenReturn(Optional.of(gymInstructor1));
        when(gymWorkoutRepository.findAllByGymInstructorAndIsActiveTrue(gymInstructor1)).thenReturn(List.of(gymWorkout3));

        assertDoesNotThrow(()->{
            GymWorkout updatedGymWorkout = gymWorkoutService.updateGymWorkout(
                    update,
                    gymWorkout3.getGymWorkoutId(),
                    gymInstructor1.getGymInstructorId(),
                    mockAuthentication
            );

            assertNotNull(updatedGymWorkout);
        });

    }






    ///removeGymWorkout -----------------------------------------------
    @Test
    void removeGymWorkout_ShouldSetWorkoutToInactive() {
        gymWorkout3.setDateTime(beforeNow);
        when(gymWorkoutRepository.findById(gymWorkout3.getGymWorkoutId())).thenReturn(Optional.ofNullable(gymWorkout3));

        String result = gymWorkoutService.removeGymWorkout(
                gymWorkout3.getGymWorkoutId(),
                mockAuthentication);

        assertEquals("Gym workout has been set to inactive. Workout had '1' booking", result);

        verify(gymWorkoutRepository).save(gymWorkout3);
        verify(gymBookingRepository).saveAll(gymWorkout3.getGymBookings());
        verify(mockAuthentication).getName();
        verify(mockAuthentication).getAuthorities();
    }

    @Test
    void removeGymWorkout_ShouldSetWorkoutToInactiveAndShowNumberOfBookingsChangedOneBooking() {
        gymBooking2.setGymWorkout(gymWorkout3);
        gymWorkout3.getGymBookings().add(gymBooking2);
        gymBooking3.setActive(false);
        when(gymWorkoutRepository.findById(gymWorkout3.getGymWorkoutId())).thenReturn(Optional.ofNullable(gymWorkout3));

        String result = gymWorkoutService.removeGymWorkout(
                gymWorkout3.getGymWorkoutId(),
                mockAuthentication);

        assertEquals("Gym workout has been set to inactive. Workout had '2' bookings.\n '1' booking set to inactive", result);
        assertFalse(gymBooking2.isActive());

        verify(gymWorkoutRepository).save(gymWorkout3);
        verify(gymBookingRepository).saveAll(gymWorkout3.getGymBookings());
        verify(mockAuthentication).getName();
        verify(mockAuthentication).getAuthorities();
    }

    @Test
    void removeGymWorkout_ShouldSetWorkoutToInactiveAndShowNumberOfBookingsChangedTwoBookings() {
        gymBooking2.setGymWorkout(gymWorkout3);
        gymWorkout3.getGymBookings().add(gymBooking2);

        when(gymWorkoutRepository.findById(gymWorkout3.getGymWorkoutId())).thenReturn(Optional.ofNullable(gymWorkout3));

        String result = gymWorkoutService.removeGymWorkout(
                gymWorkout3.getGymWorkoutId(),
                mockAuthentication);

        assertEquals("Gym workout has been set to inactive. Workout had '2' bookings.\n '2' bookings set to inactive", result);
        assertFalse(gymBooking2.isActive());
        assertFalse(gymBooking3.isActive());

        verify(gymWorkoutRepository).save(gymWorkout3);
        verify(gymBookingRepository).saveAll(gymWorkout3.getGymBookings());
        verify(mockAuthentication).getName();
        verify(mockAuthentication).getAuthorities();
    }

    @Test
    void removeGymWorkout_ShouldSetWorkoutToInactiveAndIfWorkoutAlreadyHappenedNotChangeTheBookings() {
        gymWorkout3.setDateTime(beforeNow);
        gymBooking2.setGymWorkout(gymWorkout3);
        gymWorkout3.getGymBookings().add(gymBooking2);

        when(gymWorkoutRepository.findById(gymWorkout3.getGymWorkoutId())).thenReturn(Optional.ofNullable(gymWorkout3));

        String result = gymWorkoutService.removeGymWorkout(
                gymWorkout3.getGymWorkoutId(),
                mockAuthentication);

        assertEquals("Gym workout has been set to inactive. Workout had '2' bookings", result);


        verify(gymWorkoutRepository).save(gymWorkout3);
        verify(gymBookingRepository).saveAll(gymWorkout3.getGymBookings());
        verify(mockAuthentication).getName();
        verify(mockAuthentication).getAuthorities();
    }

    @Test
    void removeGymWorkout_ShouldSkipSettingBookingsToFalseIfWorkoutHasNoBookings() {
        GymWorkout workout = new GymWorkout(12L, "Spinn", TrainingType.COREFLEX, 10, 99.89,gymInstructor1,  aftereNow, true);

        when(gymWorkoutRepository.findById(workout.getGymWorkoutId())).thenReturn(Optional.of(workout));

        String result = gymWorkoutService.removeGymWorkout(
                workout.getGymWorkoutId(),
                mockAuthentication);

        assertEquals("Gym workout has been set to inactive. Workout had '0' bookings", result);


        verify(gymWorkoutRepository).save(workout);
        verify(gymBookingRepository).saveAll(workout.getGymBookings());
        verify(mockAuthentication).getName();
        verify(mockAuthentication).getAuthorities();
    }

    @Test
    void removeGymWorkout_ShouldThrowIfGymWorkoutNotFound() {
        when(gymWorkoutRepository.findById(10L)).thenReturn(Optional.empty());
        ResourceNotFoundException resourceNotFoundException = assertThrows(
                ResourceNotFoundException.class,
                ()-> gymWorkoutService.removeGymWorkout(
                        10L,
                        mockAuthentication)
        );

        assertEquals("No GymWorkout with id [10] found", resourceNotFoundException.getMessage());
    }


}