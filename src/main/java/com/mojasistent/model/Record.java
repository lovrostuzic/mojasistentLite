package com.mojasistent.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Record {

    @Id
    @GeneratedValue
    private Long id;


    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
    private int painLevel;
    private int happinessLevel = -1;
    private String text;
    private Date date;
    private String causeofPain;
    private String activity;


    public Record(Patient patient, int painLevel, int happinessLevel, String text) {
        this.patient = patient;
        this.painLevel = painLevel;
        this.happinessLevel = happinessLevel;
        this.text = text;
    }



}
