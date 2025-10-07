package com.example.wigellgymservice.models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class DTOGymWorkout {
    private String name;
    private String trainingType;
    private int maxParticipants;
    private double price;
    private Long gymInstructorId;
    private LocalDateTime dateTime;
    private boolean isActive;

    public DTOGymWorkout(String name, String trainingType, int maxParticipants, double price, LocalDateTime dateTime, boolean isActive) {
        this.name = name;
        this.trainingType = trainingType;
        this.maxParticipants = maxParticipants;
        this.price = price;
        this.dateTime = dateTime;
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Long getGymInstructorId() {
        return gymInstructorId;
    }

    public void setGymInstructorId(Long gymInstructorId) {
        this.gymInstructorId = gymInstructorId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    @JsonProperty("isActive")
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
