package com.example.wigellgymservice.models.DTO;

import com.example.wigellgymservice.enums.TrainingType;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class DTOGymWorkout {
    private String name;
    private TrainingType trainingType;
    private int maxParticipants;
    private double price;
    private int gymInstructorId;
    private LocalDateTime dateTime;
    private boolean isActive;

    public DTOGymWorkout(String name, TrainingType trainingType, int maxParticipants, double price, int gymInstructorId, LocalDateTime dateTime, boolean isActive) {
        this.name = name;
        this.trainingType = trainingType;
        this.maxParticipants = maxParticipants;
        this.price = price;
        this.gymInstructorId = gymInstructorId;
        this.dateTime = dateTime;
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
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

    public int getGymInstructorId() {
        return gymInstructorId;
    }

    public void setGymInstructorId(int gymInstructorId) {
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
