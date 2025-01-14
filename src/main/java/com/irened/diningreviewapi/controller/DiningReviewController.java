package com.irened.diningreviewapi.controller;

import com.irened.diningreviewapi.model.DiningReview;
import com.irened.diningreviewapi.model.ReviewStatus;
import com.irened.diningreviewapi.repository.DiningReviewRepository;
import com.irened.diningreviewapi.repository.RestaurantRepository;
import com.irened.diningreviewapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@RestController
@RequestMapping("/reviews")
public class DiningReviewController {

    private final DiningReviewRepository diningReviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;

    public DiningReviewController(DiningReviewRepository diningReviewRepository, RestaurantRepository restaurantRepository, UserRepository userRepository) {
        this.diningReviewRepository = diningReviewRepository;
        this.restaurantRepository = restaurantRepository;
        this.userRepository = userRepository;
    }

    /**
     * Submit a new dining review.
     * The review is initially set to a pending status.
     *
     * @param diningReview The dining review to be submitted.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void submitReview(@RequestBody DiningReview diningReview) {
        validateDiningReview(diningReview);

        Optional<?> restaurant = restaurantRepository.findById(diningReview.getRestaurantId());
        if (restaurant.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Restaurant not found.");
        }

        diningReview.setStatus(ReviewStatus.PENDING);
        diningReviewRepository.save(diningReview);
    }

    /**
     * Retrieve a dining review by its ID.
     *
     * @param id ID of the dining review.
     * @return The dining review with the given ID.
     */
    @GetMapping("/{id}")
    public DiningReview getReview(@PathVariable Long id) {
        Optional<DiningReview> review = diningReviewRepository.findById(id);
        if (review.isPresent()) {
            return review.get();
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Review not found.");
    }

    /**
     * Update the status of an existing dining review.
     *
     * @param id     ID of the dining review.
     * @param status New status to be set for the review.
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateReviewStatus(@PathVariable Long id, @RequestParam String status) {
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
    }

    /**
     * Retrieve all reviews for a specific restaurant by its ID and status.
     *
     * @param restaurantId ID of the restaurant.
     * @param status       Status of the reviews to retrieve.
     * @return List of dining reviews matching the criteria.
     */
    @GetMapping("/restaurant/{restaurantId}")
    public Iterable<DiningReview> getReviewsByRestaurant(@PathVariable Long restaurantId, @RequestParam ReviewStatus status) {
        return diningReviewRepository.findByRestaurantIdAndStatus(restaurantId, status);
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
