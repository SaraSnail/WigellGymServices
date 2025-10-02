package com.example.wigellgymservice.models.entities;

import com.example.wigellgymservice.enums.TrainingType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity(name = "gym_instructor")
public class GymInstructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gymInstructorId;

    @Column(name="gym_instructor_name", nullable=false,length = 35)
    private String gymInstructorName;

    @Enumerated(EnumType.STRING)
    @Column(name="gym_instructor_specialty", nullable=false)
    private TrainingType trainingType;

    @OneToMany(mappedBy = "gymInstructor")
    private List<GymWorkout> gymWorkouts = new ArrayList<>();

    @Column(name = "gym_instructor_is_active",nullable = false)
    private boolean isActive;


    public GymInstructor() {
    }

    public GymInstructor(String gymInstructorName, TrainingType trainingType,boolean isActive) {
        this.gymInstructorName = gymInstructorName;
        this.trainingType = trainingType;
        this.isActive = isActive;
    }

    public GymInstructor(Long gymInstructorId, String gymInstructorName, TrainingType trainingType,boolean isActive) {
        this.gymInstructorId = gymInstructorId;
        this.gymInstructorName = gymInstructorName;
        this.trainingType = trainingType;
        this.isActive = isActive;
    }

    public GymInstructor(String gymInstructorName, TrainingType trainingType, List<GymWorkout> gymWorkouts, boolean isActive) {
        this.gymInstructorName = gymInstructorName;
        this.trainingType = trainingType;
        this.gymWorkouts = gymWorkouts;
        this.isActive = isActive;
    }

    public GymInstructor(boolean isActive, List<GymWorkout> gymWorkouts, TrainingType trainingType, String gymInstructorName, Long gymInstructorId) {
        this.isActive = isActive;
        this.gymWorkouts = gymWorkouts;
        this.trainingType = trainingType;
        this.gymInstructorName = gymInstructorName;
        this.gymInstructorId = gymInstructorId;
    }

    public Long getGymInstructorId() {
        return gymInstructorId;
    }

    public void setGymInstructorId(Long gymInstructorId) {
        this.gymInstructorId = gymInstructorId;
    }

    public String getGymInstructorName() {
        return gymInstructorName;
    }

    public void setGymInstructorName(String gymInstructorName) {
        this.gymInstructorName = gymInstructorName;
    }

    public TrainingType getTrainingType() {
        return trainingType;
    }

    public void setTrainingType(TrainingType trainingType) {
        this.trainingType = trainingType;
    }

    @JsonIgnore
    public List<GymWorkout> getGymWorkouts() {
        return gymWorkouts;
    }

    public void setGymWorkouts(List<GymWorkout> gymWorkouts) {
        this.gymWorkouts = gymWorkouts;
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
        return "GymInstructor{" +
                "gymInstructorId=" + gymInstructorId +
                ", gymInstructorName='" + gymInstructorName + '\'' +
                ", trainingType=" + trainingType +
                ", gymWorkouts=" + gymWorkouts +
                ", isActive=" + isActive +
                '}';
    }
}
