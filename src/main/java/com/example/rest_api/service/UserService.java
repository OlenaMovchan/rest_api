package com.example.rest_api.service;

import com.example.rest_api.config.UserRegistrationConfig;
import com.example.rest_api.dto.UserDto;
import com.example.rest_api.dto.UserUpdateDto;
import com.example.rest_api.entity.UserEntity;
import com.example.rest_api.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@Slf4j
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper mapper;
    private UserRegistrationConfig userRegistrationConfig;

    public UserDto createUser(UserDto userDto) {
        int minAge = userRegistrationConfig.getMinAge();
        if (!isUserOldEnough(userDto.getBirthDate(), minAge)) {
            throw new IllegalArgumentException("User must be at least " + minAge + " years old to register.");
        }
        UserEntity userEntity = userRepository.save(mapper.map(userDto, UserEntity.class));
        return toDto(userEntity);
    }

    private boolean isUserOldEnough(LocalDate birthDate, int minAge) {
        LocalDate minAgeDate = LocalDate.now().minusYears(minAge);
        return birthDate.isBefore(minAgeDate);
    }

    public UserDto updateAllFields(Long userId, UserDto userDto) {
        int minAge = userRegistrationConfig.getMinAge();
        if (!isUserOldEnough(userDto.getBirthDate(), minAge)) {
            throw new IllegalArgumentException("User must be at least " + minAge + " years old to register.");
        }
        UserEntity userEntity = getUserEntity(userId);
        userEntity.setEmail(userDto.getEmail());
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        userEntity.setBirthDate(userDto.getBirthDate());
        userEntity.setAddress(userDto.getAddress());
        userEntity.setPhoneNumber(userDto.getPhoneNumber());

        UserEntity updatedUserEntity = userRepository.save(userEntity);

        return toDto(updatedUserEntity);
    }

    public UserUpdateDto updateUser(Long userId, UserUpdateDto userUpdateDto) {
        UserEntity userEntity = getUserEntity(userId);
        if (userUpdateDto.getAddress() != null) {
            userEntity.setAddress(userUpdateDto.getAddress());
        }
        if (userUpdateDto.getPhoneNumber() != null) {
            userEntity.setPhoneNumber(userUpdateDto.getPhoneNumber());
        }
        userEntity.setEmail(userUpdateDto.getEmail());
        UserEntity updatedUserEntity = userRepository.save(userEntity);
        return toUpdateDto(updatedUserEntity);
    }

    public UserEntity getUserEntity(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "User not found with id: " + userId));
    }

    public void deleteUser(Long userId) {
        UserEntity userEntity = getUserEntity(userId);
        userRepository.delete(userEntity);
        log.info("User deleted successfully");
    }


    public List<UserDto> searchUsersByBirthDateRange(LocalDate fromDate, LocalDate toDate) {
        List<UserEntity> users = userRepository.findByBirthDateBetween(fromDate, toDate);
        return users.stream().map(this::toDto).toList();
    }

    public UserEntity toEntity(UserDto userDto) {
        return mapper.map(userDto, UserEntity.class);
    }

    public UserDto toDto(UserEntity userEntity) {
        return mapper.map(userEntity, UserDto.class);
    }

    public UserUpdateDto toUpdateDto(UserEntity userEntity) {
        return mapper.map(userEntity, UserUpdateDto.class);
    }
}
