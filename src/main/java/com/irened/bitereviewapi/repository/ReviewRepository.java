package com.irened.bitereviewapi.repository;

import com.irened.bitereviewapi.model.DiningReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<DiningReview, Long> {
    
}
