package com.example.sendtoken.repository;

import com.example.sendtoken.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person,Long> {


    Optional<Person> findByName(String name);


}
