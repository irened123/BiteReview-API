package com.irened.diningreviewapi.controller;

import com.irened.diningreviewapi.model.DiningReview;
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
    public DiningReview createReview(@RequestBody DiningReview review) {
        return reviewRepository.save(review);
    }

    @GetMapping
    public List<DiningReview> getAllReviews() {
        return reviewRepository.findAll();
    }
}
