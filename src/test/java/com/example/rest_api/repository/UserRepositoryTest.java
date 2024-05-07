package com.example.rest_api.repository;

import com.example.rest_api.entity.UserEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    UserEntity userEntity;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity(1L, "email@gmail.com",
                "firstName", "lastName", LocalDate.of(2000, 1, 1),
                "address", "phoneNumber");
        userRepository.save(userEntity);
    }

    @AfterEach
    void tearDown() {
        userEntity = null;
        userRepository.deleteAll();
    }

    @Test
    void testFindByBirthDateBetween_found(){
        List<UserEntity> userEntityList = userRepository.findByBirthDateBetween(LocalDate.of(1995, 1, 1), LocalDate.of(2005, 1, 1));
        assertThat(userEntityList.get(0).getId()).isEqualTo(userEntity.getId());
        assertThat(userEntityList.get(0).getEmail()).isEqualTo(userEntity.getEmail());
        assertThat(userEntityList.get(0).getFirstName()).isEqualTo(userEntity.getFirstName());
        assertThat(userEntityList.get(0).getLastName()).isEqualTo(userEntity.getLastName());
        assertThat(userEntityList.get(0).getBirthDate()).isEqualTo(userEntity.getBirthDate());
        assertThat(userEntityList.get(0).getAddress()).isEqualTo(userEntity.getAddress());
        assertThat(userEntityList.get(0).getPhoneNumber()).isEqualTo(userEntity.getPhoneNumber());
    }

    @Test
    void testFindByBirthDateBetween_notFound(){
        List<UserEntity> userEntityList = userRepository.findByBirthDateBetween(LocalDate.of(2001, 1, 1), LocalDate.of(2005, 1, 1));
        assertThat(userEntityList.isEmpty()).isTrue();
    }

}
