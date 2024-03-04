package com.mojasistent.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FamilyMemberRepository extends CrudRepository<FamilyMember,Long> {


    FamilyMember findByEmailAndPassword(String email, String password);

    @Query("select f from FAMILY_MEMBER f where f.email = ?1")
    FamilyMember findByEmail(String email);

    long deleteByPatient(Optional<Patient> patient);
}
