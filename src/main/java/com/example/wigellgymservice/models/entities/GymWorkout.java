package com.example.wigellgymservice.models.entities;

import com.example.wigellgymservice.enums.TrainingType;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity(name = "gym_workout")
public class GymWorkout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long gymWorkoutId;

    @Column(name = "gym_workout_name", nullable = false, length = 30)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "gym_workout_traning_type",nullable = false)
    private TrainingType trainingType;

    @Column(name = "gym_workout_max_participants",length = 80,nullable = false)
    private int maxParticipants;

    @Column(name = "gym_workout_price", nullable = false)
    private double price;

    @ManyToOne//name=gym_instructor_gym_instructor_id
    @JoinColumn(name = "gym_instructor_gym_instructor_id",nullable = false)
    private GymInstructor gymInstructor;

    @Column(name = "gym_workout_date_time",nullable = false)
    private LocalDateTime dateTime;


    @Column(name = "gym_workout_is_active",nullable = false)
    private boolean isActive;

    public GymWorkout() {
    }

    public GymWorkout(String name, TrainingType trainingType, int maxParticipants, double price, LocalDateTime dateTime, boolean isActive) {
        this.name = name;
        this.trainingType = trainingType;
        this.maxParticipants = maxParticipants;
        this.price = price;
        this.dateTime = dateTime;
        this.isActive = isActive;
    }

    public GymWorkout(String name, TrainingType trainingType, int maxParticipants, double price, GymInstructor gymInstructor, LocalDateTime dateTime, boolean isActive) {
        this.name = name;
        this.trainingType = trainingType;
        this.maxParticipants = maxParticipants;
        this.price = price;
        this.gymInstructor = gymInstructor;
        this.dateTime = dateTime;
        this.isActive = isActive;
    }

    public GymWorkout(Long gymWorkoutId, String name, TrainingType trainingType, int maxParticipants, double price, LocalDateTime dateTime, boolean isActive) {
        this.gymWorkoutId = gymWorkoutId;
        this.name = name;
        this.trainingType = trainingType;
        this.maxParticipants = maxParticipants;
        this.price = price;
        this.dateTime = dateTime;
        this.isActive = isActive;
    }

    public GymWorkout(Long gymWorkoutId, String name, TrainingType trainingType, int maxParticipants, double price, GymInstructor gymInstructor, LocalDateTime dateTime, boolean isActive) {
        this.gymWorkoutId = gymWorkoutId;
        this.name = name;
        this.trainingType = trainingType;
        this.maxParticipants = maxParticipants;
        this.price = price;
        this.gymInstructor = gymInstructor;
        this.dateTime = dateTime;
        this.isActive = isActive;
    }


    public Long getGymWorkoutId() {
        return gymWorkoutId;
    }

    public void setGymWorkoutId(Long gymWorkoutId) {
        this.gymWorkoutId = gymWorkoutId;
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

    public GymInstructor getGymInstructor() {
        return gymInstructor;
    }

    public void setGymInstructor(GymInstructor gymInstructor) {
        this.gymInstructor = gymInstructor;
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

    @Override
    public String toString() {
        return "GymWorkout{" +
                "gymWorkoutId=" + gymWorkoutId +
                ", name='" + name + '\'' +
                ", trainingType=" + trainingType +
                ", maxParticipants=" + maxParticipants +
                ", price=" + price +
                ", gymInstructor=" + gymInstructor +
                ", dateTime=" + dateTime +
                ", isActive=" + isActive +
                '}';
    }
}
