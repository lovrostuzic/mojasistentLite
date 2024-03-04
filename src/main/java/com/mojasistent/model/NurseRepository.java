package com.mojasistent.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NurseRepository extends CrudRepository<Nurse,Long> {

    Nurse findByEmailAndPassword(String email, String password);

    Nurse findById(long i);

    List<Nurse> findByCoordinator(Coordinator coordinator);

    @Query("select n from Nurse n where n.email = ?1")
    Nurse findByEmail(String email);
}
