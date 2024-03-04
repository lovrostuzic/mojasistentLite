package com.mojasistent.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter @NoArgsConstructor
public class Coordinator {
    @Id
    @GeneratedValue
    long id;
    String email;
    String password;
    String name;
    String lastName;


    public Coordinator(long id, String email, String password, String name, String lastName, Nurse nurse) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
           }

    public Coordinator(long id) {
        this.id = id;
    }
}
