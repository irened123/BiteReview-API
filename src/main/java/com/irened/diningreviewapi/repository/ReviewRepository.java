package com.irened.diningreviewapi.repository;

import com.irened.diningreviewapi.model.DiningReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<DiningReview, Long> {
    
}
