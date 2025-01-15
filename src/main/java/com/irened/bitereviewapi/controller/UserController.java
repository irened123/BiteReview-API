package com.irened.bitereviewapi.controller;


import com.irened.bitereviewapi.model.User;
import com.irened.bitereviewapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Add a new user profile.
     * Ensures that the display name is unique and mandatory.
     *
     * @param user The user profile to be added.
     * @return JSON response indicating the user was successfully added.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> addUser(@RequestBody User user) {  
        validateUser(user);

        userRepository.save(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User profile created successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieve a user profile by their display name.
     * If the user does not exist, a 404 error is returned.
     * The ID field is excluded from the returned profile for security purposes.
     *
     * @param displayName The unique display name of the user.
     * @return JSON response containing the user profile.
     */
    @GetMapping("/{displayName}")
    public ResponseEntity<?> getUser(@PathVariable String displayName) {  
        validateDisplayName(displayName);

        Optional<User> optionalUser = userRepository.findByDisplayName(displayName);
        if (optionalUser.isEmpty()) {
            return createErrorResponse("User not found", HttpStatus.NOT_FOUND);  
        }

        User user = optionalUser.get();
        user.setId(null); // Exclude ID for privacy

        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Update an existing user profile.
     * Only non-null fields from the updated profile will be applied.
     *
     * @param displayName The display name of the user to update.
     * @param updatedUser The updated user profile details.
     * @return JSON response indicating the user profile was updated.
     */
    @PutMapping("/{displayName}")
    public ResponseEntity<Map<String, String>> updateUser(@PathVariable String displayName, @RequestBody User updatedUser) {  
        validateDisplayName(displayName);

        Optional<User> optionalUser = userRepository.findByDisplayName(displayName);
        if (optionalUser.isEmpty()) {
            return createErrorResponse("User not found", HttpStatus.NOT_FOUND);  
        }

        User existingUser = optionalUser.get();
        copyUpdatedFields(updatedUser, existingUser);
        userRepository.save(existingUser);

        Map<String, String> response = new HashMap<>();
        response.put("message", "User information updated successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Validate the provided user profile.
     * Ensures the display name is not empty and is unique.
     *
     * @param user The user profile to validate.
     */
    private void validateUser(User user) {
        validateDisplayName(user.getDisplayName());

        if (userRepository.findByDisplayName(user.getDisplayName()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Display name already exists");
        }
    }

    /**
     * Validate that the display name is not empty.
     *
     * @param displayName The display name to validate.
     */
    private void validateDisplayName(String displayName) {
        if (!StringUtils.hasText(displayName)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Display name cannot be empty");
        }
    }

    /**
     * Copy non-null fields from the updated user profile to the existing one.
     * Only fields that are provided (non-null) will be updated.
     *
     * @param updatedUser  The user profile containing updated fields.
     * @param existingUser The existing user profile to be updated.
     */
    private void copyUpdatedFields(User updatedUser, User existingUser) {
        if (StringUtils.hasText(updatedUser.getCity())) {
            existingUser.setCity(updatedUser.getCity());
        }

        if (StringUtils.hasText(updatedUser.getState())) {
            existingUser.setState(updatedUser.getState());
        }

        if (StringUtils.hasText(updatedUser.getZipCode())) {
            existingUser.setZipCode(updatedUser.getZipCode());
        }

        if (updatedUser.getInterestedInPeanutAllergies() != null) {
            existingUser.setInterestedInPeanutAllergies(updatedUser.getInterestedInPeanutAllergies());
        }

        if (updatedUser.getInterestedInDairyAllergies() != null) {
            existingUser.setInterestedInDairyAllergies(updatedUser.getInterestedInDairyAllergies());
        }

        if (updatedUser.getInterestedInEggAllergies() != null) {
            existingUser.setInterestedInEggAllergies(updatedUser.getInterestedInEggAllergies());
        }
    }

    /**
     * Helper method to create a JSON error response.
     *
     * @param message The error message.
     * @param status  The HTTP status.
     * @return ResponseEntity containing the error message and status.
     */
    private ResponseEntity<Map<String, String>> createErrorResponse(String message, HttpStatus status) {  
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", message);
        return new ResponseEntity<>(errorResponse, status);
    }
}
