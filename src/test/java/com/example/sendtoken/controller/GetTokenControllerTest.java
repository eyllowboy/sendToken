package com.example.sendtoken.controller;

import com.example.sendtoken.entity.Person;
import com.example.sendtoken.repository.PersonRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@WithMockUser

public class GetTokenControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    PersonRepository personRepository;


    @Container
    public static final PostgreSQLContainer<?> postgreSQLContainer =
            (new PostgreSQLContainer<>("postgres"))
                    .withDatabaseName("project")
                    .withUsername("root")
                    .withPassword("root");


    @DynamicPropertySource
    public static void postgreSQLProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
    }


    @Test
    public void whenGetTokenSuccess() throws Exception {
        Person person = Person.builder().name("ivan").password("12345").build();
        // when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/confirmGetToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(person))
                        .with(csrf()))
                .andDo(print())
                // then
                .andExpect(status().is2xxSuccessful())
                .andExpect(jsonPath("$.token", notNullValue()));

    }
    @Test
    public void whenGetWrongPassword() throws Exception {
        Person person = Person.builder().name("ivan").password("qqqqqqqqq").build();
        // when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/confirmGetToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(person))
                        .with(csrf()))
                .andDo(print())
                // then
                .andExpect(status().is4xxClientError());


    }
    @Test
    public void whenGetUserDotNotExistInDb() throws Exception {
        Person person = Person.builder().name("ivanpetr").password("12345").build();
        // when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/confirmGetToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(person))
                        .with(csrf()))
                .andDo(print())
                // then
                .andExpect(status().is4xxClientError());


    }
}
