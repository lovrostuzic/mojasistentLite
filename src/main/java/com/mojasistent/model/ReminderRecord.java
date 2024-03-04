package com.mojasistent.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ReminderRecord {
    @Id
    @GeneratedValue
    long id;
    String medicine;
    LocalDateTime date;
    String status;
    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    public ReminderRecord(String medicine, LocalDateTime date, String status, Patient patient) {
        this.medicine = medicine;
        this.date = date;
        this.status = status;
        this.patient = patient;
    }
}
