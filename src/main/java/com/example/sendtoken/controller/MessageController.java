package com.example.sendtoken.controller;

import com.example.sendtoken.entity.Message;
import com.example.sendtoken.entity.Person;
import com.example.sendtoken.exeption.BadRequestExeption;
import com.example.sendtoken.repository.MessageRepository;
import com.example.sendtoken.repository.PersonRepository;
import com.example.sendtoken.util.JWTUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class MessageController {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    JWTUtils jwtUtils;


    @PostMapping("/api/message")
    public List<Message> postMessage(@RequestBody Message message,
                                     @RequestHeader String Bearer_token) {


        String name = message.getName();
        String text = message.getMessage();

        // check name and password empty
        if (name.trim().isEmpty() || text.trim().isEmpty()) {
            throw new BadRequestExeption("name or message must not be empty");
        }

        // check person name in db
        Person person = personRepository.findByName(name).orElseThrow(
                () -> new BadRequestExeption("User with this name not found in DB"));

        // decode token
        try {
            Claims claims = jwtUtils.decodeJWT(Bearer_token);

            // check username in token and recived message
            if (Objects.equals(name, claims.getSubject()) && !Objects.equals(text, "history 10")) {

                Message savedMessage =messageRepository.save(Message.builder().message(text).name(name).person(person).build());
                return List.of(savedMessage);

            } else if (Objects.equals(name, claims.getSubject()) && Objects.equals(text, "history 10")) {

                Page<Message> lastTenMessages = messageRepository.findAll(
                        PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id")));

                return lastTenMessages.getContent();

            } else {
                throw new BadRequestExeption("Token not validated");
            }
        } catch (ExpiredJwtException e) {
            throw new BadRequestExeption("Token expired at");
        }

    }
}
