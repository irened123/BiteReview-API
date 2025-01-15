package com.irened.bitereviewapi.repository;

import com.irened.bitereviewapi.model.DiningReview;
import com.irened.bitereviewapi.model.ReviewStatus;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

public interface DiningReviewRepository extends CrudRepository<DiningReview, Long> {

    // Fetch reviews by restaurant ID and specific status
    List<DiningReview> findByRestaurantIdAndStatus(Long restaurantId, ReviewStatus status);

    // Fetch all reviews by status
    List<DiningReview> findAllByStatus(ReviewStatus status);

    // Fetch all reviews by restaurant ID without filtering by status
    List<DiningReview> findByRestaurantId(Long restaurantId);
}

