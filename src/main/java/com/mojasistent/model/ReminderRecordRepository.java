package com.mojasistent.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReminderRecordRepository extends CrudRepository<ReminderRecord,Long> {
    @Query("SELECT r FROM ReminderRecord r WHERE r.patient = ?1 ORDER BY r.date DESC LIMIT 1")
    ReminderRecord findByPatientAndDate(Patient patient);

    List<ReminderRecord> findByPatient(Optional<Patient> patient);

    long deleteByPatient(Optional<Patient> patient);
}
