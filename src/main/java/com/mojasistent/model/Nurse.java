package com.mojasistent.model;

import jakarta.persistence.*;

@Entity(name = "Nurse")
public class Nurse {
    @Id
    @GeneratedValue
    long id;
    String name;
    String lastName;
    String email;
    String password;
    @ManyToOne
    @JoinColumn(name = "coordinatorId")
    Coordinator coordinator;

    public Nurse() {
    }

    public Nurse(String name, String lastName, String email, String password, Coordinator coordinator) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.coordinator = coordinator;
    }

    public Coordinator getCoordinator() {
        return coordinator;
    }

    public void setCoordinator(Coordinator coordinator) {
        this.coordinator = coordinator;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
