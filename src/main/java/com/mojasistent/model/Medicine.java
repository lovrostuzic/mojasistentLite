package com.mojasistent.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Medicine {
    @Id
    @GeneratedValue
    long id;

    String medicineName;

    String takingTime;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    public Medicine(String medicineName, String takingTime, Patient patient) {
        this.medicineName = medicineName;
        this.takingTime = takingTime;
        this.patient = patient;
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "id=" + id +
                ", medicineName='" + medicineName + '\'' +
                ", takingTime='" + takingTime + '\'' +
                ", patient=" + patient +
                '}';
    }
}
