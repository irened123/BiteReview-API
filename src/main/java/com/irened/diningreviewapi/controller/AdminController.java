package com.irened.diningreviewapi.controller;

import com.irened.diningreviewapi.model.AdminReviewAction;
import com.irened.diningreviewapi.model.DiningReview;
import com.irened.diningreviewapi.model.ReviewStatus;
import com.irened.diningreviewapi.repository.DiningReviewRepository;
import com.irened.diningreviewapi.repository.RestaurantRepository;
import com.irened.diningreviewapi.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final DiningReviewRepository diningReviewRepository;
    private final RestaurantRepository restaurantRepository;
    private final UserRepository userRepository;
    private static final DecimalFormat decimalFormat = new DecimalFormat("0.00");
        private static final ReviewStatus PENDING = null;
    
        public AdminController(DiningReviewRepository diningReviewRepository,
                               RestaurantRepository restaurantRepository,
                               UserRepository userRepository) {
            this.diningReviewRepository = diningReviewRepository;
            this.restaurantRepository = restaurantRepository;
            this.userRepository = userRepository;
        }
    
        /**
         * Fetch all dining reviews with a pending status.
         *
         * @return List of pending dining reviews.
         */
        @GetMapping("/reviews/pending")
        public ResponseEntity<List<DiningReview>> getPendingReviews() {
                    List<DiningReview> pendingReviews = diningReviewRepository.findAllByStatus(PENDING);
        return new ResponseEntity<>(pendingReviews, HttpStatus.OK);
    }

    /**
     * Approve or reject a dining review based on an admin's action.
     * If approved, the restaurant's scores are recomputed.
     *
     * @param id     ID of the dining review.
     * @param action Admin action indicating whether to approve or reject the review.
     * @return Response message indicating success or failure.
     */
    @PutMapping("/reviews/{id}")
    public ResponseEntity<String> approveOrRejectReview(@PathVariable Long id, @RequestBody AdminReviewAction action) {
        Optional<DiningReview> optionalReview = diningReviewRepository.findById(id);
        if (optionalReview.isEmpty()) {
            return new ResponseEntity<>("Review not found", HttpStatus.NOT_FOUND);
        }

        DiningReview review = optionalReview.get();

        // Validate that the user associated with the review exists
        if (userRepository.findById(review.getId()).isEmpty()) {
            return new ResponseEntity<>("User not found for the review", HttpStatus.BAD_REQUEST);
        }

        // Determine review status based on admin action
        ReviewStatus status = action.getAccepted() ? ReviewStatus.APPROVED : ReviewStatus.REJECTED;
        review.setStatus(status);
        diningReviewRepository.save(review);

        // Recompute restaurant scores if the review is approved
        if (status == ReviewStatus.APPROVED) {
            recomputeRestaurantScores(review.getRestaurantId());
        }

        return new ResponseEntity<>("Review status updated successfully", HttpStatus.OK);
    }

    /**
     * Recompute the average peanut, dairy, egg, and overall scores for a restaurant.
     * The scores are recalculated based on all approved reviews.
     *
     * @param restaurantId ID of the restaurant.
     */
    private void recomputeRestaurantScores(Long restaurantId) {
        List<DiningReview> approvedReviews = diningReviewRepository.findByRestaurantIdAndStatus(restaurantId, ReviewStatus.APPROVED);

        if (approvedReviews.isEmpty()) {
            return; // No approved reviews, nothing to recompute
        }

        double totalPeanutScore = 0;
        double totalDairyScore = 0;
        double totalEggScore = 0;

        for (DiningReview review : approvedReviews) {
            totalPeanutScore += review.getPeanutScore();
            totalDairyScore += review.getDairyScore();
            totalEggScore += review.getEggScore();
        }

        int reviewCount = approvedReviews.size();
        double totalOverallScore = totalPeanutScore + totalDairyScore + totalEggScore;
        double averagePeanutScore = totalPeanutScore / reviewCount;
        double averageDairyScore = totalDairyScore / reviewCount;
        double averageEggScore = totalEggScore / reviewCount;
        double averageOverallScore = totalOverallScore / reviewCount;

        // Update restaurant scores
        restaurantRepository.findById(restaurantId).ifPresent(restaurant -> {
            restaurant.setPeanutScore(Double.parseDouble(decimalFormat.format(averagePeanutScore)));
            restaurant.setDairyScore(Double.parseDouble(decimalFormat.format(averageDairyScore)));
            restaurant.setEggScore(Double.parseDouble(decimalFormat.format(averageEggScore)));
            restaurant.setOverallScore(Double.parseDouble(decimalFormat.format(averageOverallScore)));
            restaurantRepository.save(restaurant);
        });
    }
}
