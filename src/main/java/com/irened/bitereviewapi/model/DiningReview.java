package com.irened.bitereviewapi.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "dining_reviews")  // Specifies the table name
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiningReview {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // Unique database ID

    private String displayName;  // User's display name

    private Long restaurantId;  // ID of the restaurant being reviewed
    private Integer peanutScore;  // Optional peanut score (1-5)
    private Integer eggScore;  // Optional egg score (1-5)
    private Integer dairyScore;  // Optional dairy score (1-5)

    private String commentary;  // Optional commentary

    @Enumerated(EnumType.STRING)
    private ReviewStatus status = ReviewStatus.PENDING; // Default status
}
