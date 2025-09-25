package com.example.wigellgymservice.models.DTO;

import com.example.wigellgymservice.enums.TrainingType;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DTOGymInstructor {
    private String gymInstructorName;
    private TrainingType trainingType;
    private boolean isActive;

    public DTOGymInstructor(String gymInstructorName, TrainingType trainingType, boolean isActive) {
        this.gymInstructorName = gymInstructorName;
        this.trainingType = trainingType;
        this.isActive = isActive;
    }

    public String getGymInstructorName() {
        return gymInstructorName;
    }

    public void setGymInstructorName(String gymInstructorName) {
        this.gymInstructorName = gymInstructorName;
    }

    @JsonProperty("isActive")
    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }
}
