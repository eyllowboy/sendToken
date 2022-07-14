package com.example.sendtoken.controller;

import com.example.sendtoken.entity.Message;
import com.example.sendtoken.repository.MessageRepository;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@WithMockUser
public class MessageControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    MessageRepository messageRepository;

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
    public void whenPostNotValidToken() throws Exception {
        Message message = Message.builder().name("ivan").message("message").build();
        // when
        final MvcResult result;
        result=mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(message))
                        .header("Bearer_token", "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE2NTc3NTM0OTMsInN1YiI6Iml2YW4iLCJleHAiOjE2NTc3NjM0OTN9.XoxJFgEVtIDZCPqkdSCtvlazMWeHOm5oP_L4Yqgxp-w")
                        .with(csrf()))
                .andReturn();
                // then
        assertEquals(400, result.getResponse().getStatus());


    }

    @Test
    public void whenPostMessageValidTokenValidNameSuccess() throws Exception {
        Integer CountMessagesBefore = messageRepository.findAll().size();

        //get token
        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/confirmGetToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("name", "ivan")
                        .param("password", "12345")
                        .with(csrf()))
                .andReturn();

        JSONObject jsonToken = new JSONObject(result.getResponse().getContentAsString());
        String token = jsonToken.getString("token");

        // when
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/message")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("name", "ivan")
                        .param("message", "12345")
                        .header("Bearer_token", token)
                        .with(csrf()))
                .andDo(print())
                // then
                .andExpect(status().is2xxSuccessful());

        Integer CountMessageAfter = messageRepository.findAll().size();
        assertEquals(CountMessagesBefore + 1, CountMessageAfter);

    }

//    @Test
//    public void whenGetTenMessageValidTokenValidUserSuccess() throws Exception {
//
//        //get token
//        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/api/confirmGetToken")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .param("name", "ivan")
//                        .param("password", "12345")
//                        .with(csrf()))
//                .andReturn();
//
//        JSONObject jsonToken = new JSONObject(result.getResponse().getContentAsString());
//        String token = jsonToken.getString("token");
//
//        // when
//        final MvcResult resultMessage =mockMvc.perform(MockMvcRequestBuilders
//                        .post("/api/message")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .param("name", "ivan")
//                        .param("message", "history 10")
//                        .header("Bearer_token", token)
//                        .with(csrf()))
//                .andReturn();
//                // then
//        JSONArray jsonMessage = new JSONArray(resultMessage.getResponse().getContentAsString());
//        assertEquals(10, jsonMessage.length());
//
//    }
//
//    @Test
//    public void whenPostMessageValidTokenWrongUser() throws Exception {
//
//        //get token
//        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/api/confirmGetToken")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .param("name", "ivan")
//                        .param("password", "12345")
//                        .with(csrf()))
//                .andReturn();
//
//        JSONObject jsonToken = new JSONObject(result.getResponse().getContentAsString());
//        String token = jsonToken.getString("token");
//
//        // when
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/api/message")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .param("name", "wwwwwww")
//                        .param("message", "history 10")
//                        .header("Bearer_token", token)
//                        .with(csrf()))
//                .andDo(print())
//                // then
//                .andExpect(status().is4xxClientError());
//
//    }
//    @Test
//    public void whenPostEmptyMessageValidTokenValidUser() throws Exception {
//
//        //get token
//        final MvcResult result = mockMvc.perform(MockMvcRequestBuilders
//                        .post("/api/confirmGetToken")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .param("name", "ivan")
//                        .param("password", "12345")
//                        .with(csrf()))
//                .andReturn();
//
//        JSONObject jsonToken = new JSONObject(result.getResponse().getContentAsString());
//        String token = jsonToken.getString("token");
//
//        // when
//        mockMvc.perform(MockMvcRequestBuilders
//                        .post("/api/message")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .param("name", "ivan")
//                        .param("message", "  ")
//                        .header("Bearer_token", token)
//                        .with(csrf()))
//                .andDo(print())
//                // then
//                .andExpect(status().is4xxClientError());
//
//    }
}
