package com.irened.diningreviewapi.controller;

import com.irened.diningreviewapi.model.DiningReview;
import com.irened.diningreviewapi.model.ReviewStatus;
import com.irened.diningreviewapi.repository.DiningReviewRepository;
import com.irened.diningreviewapi.repository.RestaurantRepository;
import com.irened.diningreviewapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@RestController
@RequestMapping("/api/reviews")
public class DiningReviewController {

    private final DiningReviewRepository diningReviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public DiningReviewController(DiningReviewRepository diningReviewRepository, 
                                  RestaurantRepository restaurantRepository, 
                                  UserRepository userRepository) {
        this.diningReviewRepository = diningReviewRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    /**
     * Submit a new dining review.
     * The review is initially set to a pending status.
     *
     * @param diningReview The dining review to be submitted.
     * @return A JSON response indicating successful submission.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> submitReview(@RequestBody DiningReview diningReview) {
        validateDiningReview(diningReview);

        Optional<?> restaurant = restaurantRepository.findById(diningReview.getRestaurantId());
        if (restaurant.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Restaurant not found.");
        }

        diningReview.setStatus(ReviewStatus.PENDING);
        diningReviewRepository.save(diningReview);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Review submitted successfully");
        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    /**
     * Retrieve a dining review by its ID.
     *
     * @param id ID of the dining review.
     * @return The dining review with the given ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<DiningReview> getReview(@PathVariable Long id) {
        Optional<DiningReview> review = diningReviewRepository.findById(id);
        if (review.isPresent()) {
            return new ResponseEntity<>(review.get(), HttpStatus.OK);
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found.");
    }

    /**
     * Update the status of an existing dining review.
     *
     * @param id     ID of the dining review.
     * @param status New status to be set for the review.
     * @return A JSON response indicating the status update.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateReviewStatus(@PathVariable Long id, @RequestParam String status) {
        Optional<DiningReview> optionalReview = diningReviewRepository.findById(id);
        if (optionalReview.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found.");
        }

        DiningReview diningReview = optionalReview.get();

        try {
            ReviewStatus reviewStatus = ReviewStatus.valueOf(status.toUpperCase());
            diningReview.setStatus(reviewStatus);
            diningReviewRepository.save(diningReview);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid review status.");
        }

        Map<String, String> response = new HashMap<>();
        response.put("message", "Review status updated successfully");
        return new ResponseEntity<>(response, HttpStatus.NO_CONTENT);
    }

    /**
     * Retrieve all reviews for a specific restaurant by its ID and status.
     *
     * @param restaurantId ID of the restaurant.
     * @param status       Status of the reviews to retrieve (optional).
     * @return A list of dining reviews matching the criteria.
     */
    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<DiningReview>> getReviewsByRestaurant(@PathVariable Long restaurantId, 
                                                                     @RequestParam(required = false) String status) {
        List<DiningReview> reviews;

        if (status == null) {
            reviews = diningReviewRepository.findByRestaurantId(restaurantId);
        } else {
            try {
                ReviewStatus reviewStatus = ReviewStatus.valueOf(status.toUpperCase());
                reviews = diningReviewRepository.findByRestaurantIdAndStatus(restaurantId, reviewStatus);
            } catch (IllegalArgumentException ex) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid review status.");
            }
        }

        return new ResponseEntity<>(reviews, HttpStatus.OK);
    }

    /**
     * Validate the dining review before processing.
     *
     * @param diningReview The dining review to validate.
     */
    private void validateDiningReview(DiningReview diningReview) {
        if (ObjectUtils.isEmpty(diningReview.getDisplayName()) || ObjectUtils.isEmpty(diningReview.getRestaurantId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Submitted by and restaurant ID are required.");
        }

        if (ObjectUtils.isEmpty(diningReview.getPeanutScore()) &&
                ObjectUtils.isEmpty(diningReview.getDairyScore()) &&
                ObjectUtils.isEmpty(diningReview.getEggScore())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one score must be provided.");
        }

        Optional<?> user = userRepository.findByDisplayName(diningReview.getDisplayName());
        if (user.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "User not found.");
        }
    }
}
