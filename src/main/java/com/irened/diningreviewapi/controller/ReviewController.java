package com.irened.diningreviewapi.controller;

import com.irened.diningreviewapi.model.Review;
import com.irened.diningreviewapi.repository.ReviewRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;

    public ReviewController(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @PostMapping
    public Review createReview(@RequestBody Review review) {
        return reviewRepository.save(review);
    }

    @GetMapping
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }
}
