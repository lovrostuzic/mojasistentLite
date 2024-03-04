package com.mojasistent.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.Date;
import java.util.Optional;

@AllArgsConstructor
@Entity(name = "PATIENT")
@Getter
@Setter
@NoArgsConstructor

public class Patient {


    @Id
    @GeneratedValue
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String phoneNumber;
    private Date date;
    private String token;


    LocalTime morningReminderTime;
    LocalTime noonReminderTime;
    LocalTime eveningReminderTime;

    @ManyToOne
    @JoinColumn(name = "coordinator_id")
    Coordinator coordinator;

    public Patient(String firstName, String lastName, String email, String password, String phoneNumber, Date date, Optional<Coordinator> coordinator) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.date = date;
        setCoordinator(coordinator.orElse(null));
    }

    public Patient(String firstName, String lastName, String email, String password, String phoneNumber, Date date, Coordinator coordinator) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.date = date;
        this.phoneNumber = phoneNumber;
        setCoordinator(coordinator);
    }

    @Override
    public String toString() {
        return "Patient{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}