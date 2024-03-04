package com.mojasistent.model;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CoordinatorRepository extends CrudRepository<Coordinator,Long> {

    Coordinator findByEmailAndPassword(String email, String password);

    @Query("select c from Coordinator c where c.email = ?1")
    Coordinator findByEmail(String email);



}
