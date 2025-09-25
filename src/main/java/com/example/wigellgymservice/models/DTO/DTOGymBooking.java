package com.example.wigellgymservice.models.DTO;

import com.example.wigellgymservice.models.entities.GymCustomer;
import com.example.wigellgymservice.models.entities.GymWorkout;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class DTOGymBooking {
    private Long gymBookingId;
    private GymCustomer gymCustomer;
    private GymWorkout gymWorkout;
    private LocalDateTime bookingDate;
    private double priceSek;
    private double priceEuro;
    private boolean isActive;

    public DTOGymBooking(Long gymBookingId, GymCustomer gymCustomer, GymWorkout gymWorkout, LocalDateTime bookingDate, double priceSek, double priceEuro, boolean isActive) {
        this.gymBookingId = gymBookingId;
        this.gymCustomer = gymCustomer;
        this.gymWorkout = gymWorkout;
        this.bookingDate = bookingDate;
        this.priceSek = priceSek;
        this.priceEuro = priceEuro;
        this.isActive = isActive;
    }

    public Long getGymBookingId() {
        return gymBookingId;
    }

    public void setGymBookingId(Long gymBookingId) {
        this.gymBookingId = gymBookingId;
    }

    public GymCustomer getGymCustomer() {
        return gymCustomer;
    }

    public void setGymCustomer(GymCustomer gymCustomer) {
        this.gymCustomer = gymCustomer;
    }

    public GymWorkout getGymWorkout() {
        return gymWorkout;
    }

    public void setGymWorkout(GymWorkout gymWorkout) {
        this.gymWorkout = gymWorkout;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public double getPriceSek() {
        return priceSek;
    }

    public void setPriceSek(double priceSek) {
        this.priceSek = priceSek;
    }

    public double getPriceEuro() {
        return priceEuro;
    }

    public void setPriceEuro(double priceEuro) {
        this.priceEuro = priceEuro;
    }

    @JsonProperty("isActive")
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
