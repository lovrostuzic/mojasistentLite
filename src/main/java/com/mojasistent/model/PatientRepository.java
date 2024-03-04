package com.mojasistent.model;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface PatientRepository extends CrudRepository<Patient, Long> {

    Patient findByEmailAndPassword(String email, String password);

    List<Patient> findByCoordinator(Coordinator coordinator);

    @Query("select p from PATIENT p where p.email = ?1")
    Patient findByEmail(String email);

    @Transactional
    @Modifying
    @Query("update PATIENT p set p.token = ?1 where p.id = ?2")
    int updateTokenById(String token, Long id);


    @Transactional
    @Modifying
    @Query("""
            update PATIENT p set p.morningReminderTime = ?1, p.noonReminderTime = ?2, p.eveningReminderTime = ?3
            where p.morningReminderTime = ?4""")
    void updateMorningReminderTimeAndNoonReminderTimeAndEveningReminderTimeByMorningReminderTime(LocalTime morningReminderTime, LocalTime noonReminderTime, LocalTime eveningReminderTime, LocalTime morningReminderTime1);

    @Transactional
    @Modifying
    @Query("""
            update PATIENT p set p.morningReminderTime = ?1, p.noonReminderTime = ?2, p.eveningReminderTime = ?3
            where p.id = ?4""")
    int updateMorningReminderTimeAndNoonReminderTimeAndEveningReminderTimeById(LocalTime morningReminderTime, LocalTime noonReminderTime, LocalTime eveningReminderTime, Long id);


    @Query("select p from PATIENT p where p.token = ?1")
    Patient findByToken(String token);
}