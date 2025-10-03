package com.example.wigellgymservice.models.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "gym_booking")
public class GymBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gymBookingId;

    @ManyToOne//name=gym_customer_gym_customer_id
    @JoinColumn(name = "gym_customer_gym_customer_id",nullable = false)
    private GymCustomer gymCustomer;

    @ManyToOne//name=gym_workout_gym_workout_id
    @JoinColumn(name = "gym_workout_gym_workout_id",nullable = false)
    private GymWorkout gymWorkout;

    @Column(name = "gym_booking_date", nullable = false, length = 10)
    private LocalDateTime bookingDate;

    @Column(name = "gym_booking_price", nullable = false)
    private double price;

    @Column(name = "gym_booking_is_active",nullable = false)
    private boolean isActive;

    public GymBooking() {
    }

    public GymBooking(GymCustomer gymCustomer, GymWorkout gymWorkout, LocalDateTime bookingDate, double price, boolean isActive) {
        this.gymCustomer = gymCustomer;
        this.gymWorkout = gymWorkout;
        this.bookingDate = bookingDate;
        this.price = price;
        this.isActive = isActive;
    }

    public GymBooking(Long gymBookingId, GymCustomer gymCustomer, GymWorkout gymWorkout, LocalDateTime bookingDate, double price, boolean isActive) {
        this.gymBookingId = gymBookingId;
        this.gymCustomer = gymCustomer;
        this.gymWorkout = gymWorkout;
        this.bookingDate = bookingDate;
        this.price = price;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @JsonProperty("isActive")
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    @Override
    public String toString() {
        return "GymBooking{" +
                "gymBookingId=" + gymBookingId +
                ", gymCustomer=" + gymCustomer +
                ", gymWorkout=" + gymWorkout +
                ", bookingDate=" + bookingDate +
                ", price=" + price +
                ", isActive=" + isActive +
                '}';
    }
}
