package com.example.wigellgymservice.models.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "gym_customer")
public class GymCustomer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gymCustomerId;

    @Column(name = "gym_customer_name", nullable = false,unique = true)
    private String username;

    @OneToMany(mappedBy = "gymCustomer")
    private List<GymBooking> gymBookings = new ArrayList<>();

    @Column(name = "gym_customer_is_active", nullable = false)
    private boolean isActive;

    public GymCustomer() {
    }

    public GymCustomer(String username, boolean isActive) {
        this.username = username;
        this.isActive = isActive;
    }

    public GymCustomer(Long gymCustomerId, String username, boolean isActive) {
        this.gymCustomerId = gymCustomerId;
        this.username = username;
        this.isActive = isActive;
    }

    public GymCustomer(String username, List<GymBooking> gymBookings, boolean isActive) {
        this.username = username;
        this.gymBookings = gymBookings;
        this.isActive = isActive;
    }

    public GymCustomer(Long gymCustomerId, String username, List<GymBooking> gymBookings,boolean isActive) {
        this.gymCustomerId = gymCustomerId;
        this.username = username;
        this.gymBookings = gymBookings;
        this.isActive = isActive;
    }

    public Long getGymCustomerId() {
        return gymCustomerId;
    }

    public void setGymCustomerId(Long gymCustomerId) {
        this.gymCustomerId = gymCustomerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonIgnore
    public List<GymBooking> getGymBookings() {
        return gymBookings;
    }

    public void setGymBookings(List<GymBooking> gymBookings) {
        this.gymBookings = gymBookings;
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
        return "GymCustomer{" +
                "gymCustomerId=" + gymCustomerId +
                ", username='" + username + '\'' +
                ", gymBookings=" + gymBookings +
                ", isActive=" + isActive +
                '}';
    }
}
