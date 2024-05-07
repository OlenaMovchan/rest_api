package com.example.rest_api.service;

import com.example.rest_api.config.UserRegistrationConfig;
import com.example.rest_api.dto.UserDto;
import com.example.rest_api.dto.UserUpdateDto;
import com.example.rest_api.entity.UserEntity;
import com.example.rest_api.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private ModelMapper mapper;
    @Mock
    private UserRegistrationConfig userRegistrationConfig;
    @Mock
    private UserRepository userRepository;

    private UserService userService;
    AutoCloseable autoCloseable;
    UserEntity userEntity;
    UserDto userDto;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, mapper, userRegistrationConfig);
        userEntity = new UserEntity(1L, "email@gmail.com",
                "firstName", "lastName", LocalDate.of(2000, 1, 1),
                "address", "phoneNumber");
        userDto = new UserDto(1L, "email@gmail.com",
                "firstName", "lastName", LocalDate.of(2000, 1, 1),
                "address", "phoneNumber");
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void testCreateUserThrowsIllegalArgumentException() {
        UserDto userDto = new UserDto();
        userDto.setBirthDate(LocalDate.now());
        when(userRegistrationConfig.getMinAge()).thenReturn(18);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto));
        assertEquals("User must be at least 18 years old to register.", exception.getMessage());
        verifyNoInteractions(userRepository);
    }

    @Test
    void testCreateUserWithInvalidBirthDate() {

        UserDto userDto = new UserDto();
        userDto.setBirthDate(LocalDate.now().minusYears(17));
        when(userRegistrationConfig.getMinAge()).thenReturn(18);
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(userDto));
    }

    @Test
    void testCreateUser() {
        UserDto userDto1 = new UserDto();
        userDto1.setId(1L);
        userDto1.setEmail("test1@example.com");
        userDto1.setFirstName("Name");
        userDto1.setLastName("Surname");
        userDto1.setBirthDate(LocalDate.of(2000, 1, 1));
        userDto1.setAddress("address");
        userDto1.setPhoneNumber("phoneNumber");
        when(userRegistrationConfig.getMinAge()).thenReturn(18);

        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);

        when(mapper.map(any(UserEntity.class), eq(UserDto.class))).thenReturn(userDto1);
        when(userService.createUser(userDto1)).thenReturn(userDto1);
        UserDto createdUserDto = userService.createUser(userDto1);

        assertThat(createdUserDto.getId()).isEqualTo(userEntity.getId());
    }

    @Test
    void testUpdateNonExistentUser() {
        UserUpdateDto nonExistentUserDto = new UserUpdateDto();
        nonExistentUserDto.setId(100L);

        assertThrows(ResponseStatusException.class, () -> userService.updateUser(100L, nonExistentUserDto));
    }

    @Test
    void testUpdateUser() {
        UserUpdateDto updatedUserDto = new UserUpdateDto();
        updatedUserDto.setId(1L);
        updatedUserDto.setEmail("test@example.com");
        updatedUserDto.setAddress("new address");
        updatedUserDto.setPhoneNumber("new phoneNumber");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(mapper.map(any(UserEntity.class), eq(UserUpdateDto.class))).thenReturn(updatedUserDto);

        assertThat(userService.updateUser(updatedUserDto.getId(), updatedUserDto)).isEqualTo(updatedUserDto);
    }

    @Test
    void testUpdateAllFields() {
        UserDto updatedUserDto = new UserDto();
        updatedUserDto.setId(1L);
        updatedUserDto.setEmail("test@example.com");
        updatedUserDto.setFirstName("new firstName");
        updatedUserDto.setLastName("new lastName");
        updatedUserDto.setBirthDate(LocalDate.of(2003, 3, 3));
        updatedUserDto.setAddress("new address");
        updatedUserDto.setPhoneNumber("new phoneNumber");

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        when(mapper.map(any(UserEntity.class), eq(UserDto.class))).thenReturn(updatedUserDto);

        assertThat(userService.updateAllFields(updatedUserDto.getId(), updatedUserDto)).isEqualTo(updatedUserDto);
    }

    @Test
    void testUpdateAllFieldsWithInvalidAge() {
        UserDto userDto = new UserDto();
        userDto.setBirthDate(LocalDate.now().minusYears(17));
        when(userRegistrationConfig.getMinAge()).thenReturn(18);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            userService.updateAllFields(1L, userDto);
        });

        assertEquals("User must be at least 18 years old to register.", exception.getMessage());
    }

    @Test
    void testDeleteUser() {

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(userEntity));
        userService.deleteUser(userEntity.getId());
        verify(userRepository, times(1)).delete(userEntity);
    }

    @Test
    void testDeleteNonExistentUser() {
        long nonExistentUserId = 100L;
        assertThrows(ResponseStatusException.class, () -> userService.deleteUser(nonExistentUserId));
    }

    @Test
    void testSearchUsersByBirthDateRange() {
        List<UserEntity> users = new ArrayList<>();
        users.add(userEntity);
        LocalDate startDate = LocalDate.of(1990, 1, 1);
        LocalDate endDate = LocalDate.of(2010, 1, 2);
        when(userRepository.findByBirthDateBetween(startDate, endDate)).thenReturn(users);
        when(mapper.map(any(UserEntity.class), eq(UserDto.class))).thenReturn(userDto);
        List<UserDto> expectedUserDtos = users.stream().map(el -> mapper.map(el, UserDto.class)).toList();
        List<UserDto> actualUserDtos = userService.searchUsersByBirthDateRange(startDate, endDate);
        assertEquals(expectedUserDtos.size(), actualUserDtos.size());
        assertEquals(expectedUserDtos.get(0), actualUserDtos.get(0));
    }

    @Test
    void testSearchUsersByBirthDateRange_EmptyList() {
        List<UserEntity> users = new ArrayList<>();
        users.add(userEntity);
        LocalDate startDate = LocalDate.of(2001, 1, 1);
        LocalDate endDate = LocalDate.of(2010, 1, 2);
        when(userRepository.findByBirthDateBetween(startDate, endDate)).thenReturn(users);
        List<UserDto> expectedUserDtos = users.stream().map(el -> mapper.map(el, UserDto.class)).toList();
        List<UserDto> actualUserDtos = userService.searchUsersByBirthDateRange(startDate, endDate);
        assertEquals(expectedUserDtos.size(), actualUserDtos.size());
    }

    @Test
    void testToEntity() {
        when(mapper.map(any(UserDto.class), eq(UserEntity.class))).thenReturn(userEntity);
        assertThat(userService.toEntity(userDto)).isEqualTo(userEntity);
    }

    @Test
    void testToDto() {
        when(mapper.map(any(UserEntity.class), eq(UserDto.class))).thenReturn(userDto);
        assertThat(userService.toDto(userEntity)).isEqualTo(userDto);
    }
}

