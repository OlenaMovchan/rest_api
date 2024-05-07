package com.example.rest_api.repository;

import com.example.rest_api.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    List<UserEntity> findByBirthDateBetween(LocalDate fromDate, LocalDate toDate);
}
