package com.mojasistent.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface RecordRepository extends CrudRepository<Record, Long> {


    List<Record> findByPatient(Patient patient);

    @Query("SELECT r FROM Record r WHERE r.patient = ?1 ORDER BY r.date DESC LIMIT 1")
    Record findByPatientAndDate(Patient patient);

    long deleteByPatient(Optional<Patient> patient);

}