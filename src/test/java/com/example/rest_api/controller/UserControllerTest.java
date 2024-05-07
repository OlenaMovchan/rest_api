package com.example.rest_api.controller;

import com.example.rest_api.dto.UserDto;
import com.example.rest_api.dto.UserUpdateDto;
import com.example.rest_api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testCreateUser() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setEmail("test@example.com");
        userDto.setFirstName("TestName");
        userDto.setLastName("TestLastName");

        when(userService.createUser(any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                        .contentType("application/json")
                        .content("{\"email\":\"test@example.com\",\"firstName\":\"TestName\",\"lastName\":\"TestLastName\"}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testUpdateUser() throws Exception {
        UserUpdateDto userDto = new UserUpdateDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setAddress("newAddress");
        userDto.setPhoneNumber("newPhoneNumber");

        when(userService.updateUser(any(Long.class), any(UserUpdateDto.class))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.patch("/users/1")
                        .contentType("application/json")
                        .content("{\"email\":\"test@example.com\",\"address\":\"newAddress\",\"phoneNumber\":\"newPhoneNumber\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testUpdateAllFields() throws Exception {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setEmail("test@example.com");
        userDto.setFirstName("John");
        userDto.setLastName("Doe");

        when(userService.updateAllFields(any(Long.class), any(UserDto.class))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/1")
                        .contentType("application/json")
                        .content("{\"email\":\"test@example.com\",\"firstName\":\"John\",\"lastName\":\"Doe\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/users/1"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testSearchUsersByBirthDateRange() throws Exception {
        UserDto userDto1 = new UserDto();
        userDto1.setId(1L);
        userDto1.setEmail("test1@example.com");
        userDto1.setFirstName("John");
        userDto1.setLastName("Doe");
        userDto1.setBirthDate(LocalDate.of(2001, 1, 1));

        UserDto userDto2 = new UserDto();
        userDto2.setId(2L);
        userDto2.setEmail("test2@example.com");
        userDto2.setFirstName("Jane");
        userDto2.setLastName("Doe");
        userDto2.setBirthDate(LocalDate.of(2001, 3, 3));

        LocalDate fromDate = LocalDate.of(2000, 1, 1);
        LocalDate toDate = LocalDate.of(2020, 12, 31);

        when(userService.searchUsersByBirthDateRange(fromDate, toDate)).thenReturn(Arrays.asList(userDto1, userDto2));

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                        .param("from", fromDate.toString())
                        .param("to", toDate.toString()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].email").value("test1@example.com"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].email").value("test2@example.com"));
    }
}

