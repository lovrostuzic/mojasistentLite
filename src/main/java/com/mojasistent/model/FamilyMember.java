package com.mojasistent.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Optional;

@Entity(name = "FAMILY_MEMBER")
@Getter
@Setter
@NoArgsConstructor
public class FamilyMember {
    @Id
    @GeneratedValue
    long id;
    String name;
    String lastName;
    String email;
    String password;
    @OneToOne
    @JoinColumn(name = "patientId")
    Patient patient;

    public FamilyMember(String email, String password, Patient patient) {
        this.email = email;
        this.password = password;
        this.patient = patient;
    }

    public FamilyMember(String name, String lastName, String email, String password) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;

    }

}
