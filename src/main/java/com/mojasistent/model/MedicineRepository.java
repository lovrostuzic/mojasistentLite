package com.mojasistent.model;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedicineRepository extends CrudRepository<Medicine,Long> {

    List<Medicine> findByPatient(Patient patient);

    List<Medicine> findByPatientAndTakingTime(Patient patient, String takingTime);

    List<Medicine> findByPatientAndTakingTime(Optional<Patient> patient, String m);

    long deleteByPatient(Optional<Patient> patient);
}
