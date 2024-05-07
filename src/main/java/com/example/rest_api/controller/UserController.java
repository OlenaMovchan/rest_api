package com.example.rest_api.controller;

import com.example.rest_api.dto.UserDto;
import com.example.rest_api.dto.UserUpdateDto;
import com.example.rest_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User management endpoints.")
@ApiResponse(responseCode = "401", content = {@Content})
@AllArgsConstructor
public class UserController {

    private UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create user", description = "Create user")
    @ApiResponse(responseCode = "201", description = "User created successfully",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))})
    @ApiResponse(responseCode = "400", description = "Invalid user data", content = {@Content})
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        return userService.createUser(userDto);
    }

    @Operation(summary = "Update user", description = "Update user")
    @ApiResponse(responseCode = "200", description = "User updated successfully",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserUpdateDto.class))})
    @ApiResponse(responseCode = "400", description = "Invalid user data", content = {@Content})
    @PatchMapping ("/{userId}")
    public UserUpdateDto updateUser(@PathVariable("userId") Long userId, @Valid @RequestBody UserUpdateDto userUpdateDto) {
        return userService.updateUser(userId, userUpdateDto);
    }

    @Operation(summary = "Update user (all data)", description = "Update user (all data)")
    @ApiResponse(responseCode = "200", description = "User updated successfully",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))})
    @ApiResponse(responseCode = "400", description = "Invalid user data", content = {@Content})
    @PutMapping("/{userId}")
    public UserDto updateAllFields(@PathVariable("userId") Long userId, @Valid @RequestBody UserDto userDto) {
        return userService.updateAllFields(userId, userDto);
    }

        @Operation(summary = "Delete user by id", description = "Delete user by id")
    @ApiResponse(responseCode = "200", description = "User delete successfully",
            content = {@Content(mediaType = "application/json")})
    @ApiResponse(responseCode = "400", description = "User not found", content = {@Content})
    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable(value = "userId") Long userId) {
        userService.deleteUser(userId);
    }

    @Operation(summary = "Get users by birth date range", description = "Get users by birth date range")
    @ApiResponse(responseCode = "200", description = "Users found by birth date range successfully",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))})
    @GetMapping
    public List<UserDto> searchUsersByBirthDateRange(
            @RequestParam("from") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam("to") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {
        return userService.searchUsersByBirthDateRange(fromDate, toDate);
    }
}
