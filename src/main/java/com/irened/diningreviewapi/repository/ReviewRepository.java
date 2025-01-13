package com.irened.diningreviewapi.repository;

import com.irened.diningreviewapi.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    
}
