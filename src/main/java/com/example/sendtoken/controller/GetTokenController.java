package com.example.sendtoken.controller;

import com.example.sendtoken.entity.Person;
import com.example.sendtoken.exeption.BadRequestExeption;
import com.example.sendtoken.repository.PersonRepository;
import com.example.sendtoken.util.JWTUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import java.util.Map;
import java.util.Objects;

@RestController
public class GetTokenController {

    @Autowired
   private PersonRepository personRepository;

    @Autowired
    private JWTUtils jwtUtils;

    @PostMapping("/api/confirmGetToken")
    public Map<String, String> confirmGetToken(@RequestBody Person person) {

        String name = person.getName();
        String password = person.getPassword();
        // check name and password empty
        if (name.trim().isEmpty() || password.trim().isEmpty()) {
            throw new BadRequestExeption("username or password must not be empty");
        }

        // check person name in db
        Person personFromDB = personRepository.findByName(name).orElseThrow(
                () -> new BadRequestExeption("User with this name not found in db"));
        // decode person wassword from db
        byte[] decodedBytesPassword = Base64.getDecoder().decode(personFromDB.getPassword());
        String decodedpassword = new String(decodedBytesPassword);
        // password comparison
        if (Objects.equals(password, decodedpassword)) {

            String jwt = jwtUtils.createJWT(person.getName());
            return Map.of("token", jwt);

        } else throw new BadRequestExeption("User password does not match");

    }
}
