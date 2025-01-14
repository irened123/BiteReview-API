package com.irened.diningreviewapi.repository;

import com.irened.diningreviewapi.model.Restaurant;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends CrudRepository<Restaurant, Long> {
    // Find a restaurant by its name and zip code
    Optional<Restaurant> findByNameAndZipCode(String name, String zipCode);

    // Fetch restaurants by zip code where dairy score is available, ordered by dairy score
    List<Restaurant> findByZipCodeAndDairyScoreIsNotNullOrderByDairyScoreDesc(String zipCode);

    // Fetch restaurants by zip code where egg score is available, ordered by egg score
    List<Restaurant> findByZipCodeAndEggScoreIsNotNullOrderByEggScoreDesc(String zipCode);

    // Fetch restaurants by zip code where peanut score is available, ordered by peanut score
    List<Restaurant> findByZipCodeAndPeanutScoreIsNotNullOrderByPeanutScoreDesc(String zipCode);
}
