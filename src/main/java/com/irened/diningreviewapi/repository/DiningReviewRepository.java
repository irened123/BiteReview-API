package com.irened.diningreviewapi.repository;

import com.irened.diningreviewapi.model.DiningReview;
import com.irened.diningreviewapi.model.ReviewStatus;
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

