package com.irened.bitereviewapi.controller;


import com.irened.bitereviewapi.model.Restaurant;
import com.irened.bitereviewapi.repository.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api/restaurants")
public class RestaurantController {
    private final RestaurantRepository restaurantRepository;
    private final Pattern zipCodePattern = Pattern.compile("\\d{5}");

    public RestaurantController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    /**
     * Add a new restaurant.
     * The restaurant name must be provided, and the zip code must be valid.
     * If a restaurant with the same name and zip code already exists, the request will fail.
     *
     * @param restaurant The restaurant to be added.
     * @return JSON response indicating success or failure.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> addRestaurant(@RequestBody Restaurant restaurant) {  
        validateNewRestaurant(restaurant);
        restaurantRepository.save(restaurant);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Restaurant added successfully");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Retrieve restaurant details by ID.
     * If the restaurant with the given ID is not found, a 404 error is returned.
     *
     * @param id The ID of the restaurant.
     * @return JSON response containing the restaurant details or an error message.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getRestaurant(@PathVariable Long id) {  
        Optional<Restaurant> restaurant = restaurantRepository.findById(id);
        if (restaurant.isPresent()) {
            return new ResponseEntity<>(restaurant.get(), HttpStatus.OK);
        }

        return createErrorResponse("Restaurant not found", HttpStatus.NOT_FOUND);  
    }

    /**
     * Retrieve all restaurants.
     *
     * @return JSON response containing a list of all restaurants.
     */
    @GetMapping
    public ResponseEntity<Iterable<Restaurant>> getAllRestaurants() {
        Iterable<Restaurant> restaurants = restaurantRepository.findAll();
        return new ResponseEntity<>(restaurants, HttpStatus.OK);
    }

    /**
     * Search for restaurants by zip code and allergy score.
     * The allergy type can be "peanut", "dairy", or "egg".
     * Restaurants are sorted in descending order of their scores.
     *
     * @param zipcode The zip code to filter restaurants by.
     * @param allergy The allergy type to filter restaurants by (peanut, dairy, or egg).
     * @return JSON response containing a list of restaurants matching the criteria.
     */
    @GetMapping("/search")
    public ResponseEntity<?> searchRestaurants(
            @RequestParam String zipcode, @RequestParam String allergy) {  

        validateZipCode(zipcode);

        Iterable<Restaurant> result;
        switch (allergy.toLowerCase()) {
            case "peanut":
                result = restaurantRepository.findByZipCodeAndPeanutScoreIsNotNullOrderByPeanutScoreDesc(zipcode);
                break;
            case "dairy":
                result = restaurantRepository.findByZipCodeAndDairyScoreIsNotNullOrderByDairyScoreDesc(zipcode);
                break;
            case "egg":
                result = restaurantRepository.findByZipCodeAndEggScoreIsNotNullOrderByEggScoreDesc(zipcode);
                break;
            default:
                return createErrorResponse("Invalid allergy type", HttpStatus.BAD_REQUEST);  
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * Validate a new restaurant before saving.
     * Ensures the restaurant has a non-empty name and a valid zip code.
     * Checks for duplicate restaurants based on name and zip code.
     *
     * @param restaurant The restaurant to be validated.
     */
    private void validateNewRestaurant(Restaurant restaurant) {
        if (!StringUtils.hasText(restaurant.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Restaurant name cannot be empty");
        }
        validateZipCode(restaurant.getZipCode());

        Optional<Restaurant> existingRestaurant =
                restaurantRepository.findByNameAndZipCode(restaurant.getName(), restaurant.getZipCode());
        if (existingRestaurant.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Restaurant already exists");
        }
    }

    /**
     * Validate the zip code format.
     * The zip code must be a 5-digit numeric value.
     *
     * @param zipcode The zip code to be validated.
     */
    private void validateZipCode(String zipcode) {
        if (!zipCodePattern.matcher(zipcode).matches()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid zip code format");
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
