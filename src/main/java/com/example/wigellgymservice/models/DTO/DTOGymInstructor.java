package com.example.wigellgymservice.models.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DTOGymInstructor {
    private String gymInstructorName;
    private String trainingType;
    private boolean isActive;

    public DTOGymInstructor(String gymInstructorName, String trainingType, boolean isActive) {
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

    public String getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(String trainingType) {
        this.trainingType = trainingType;
    }
}
