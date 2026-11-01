package com.weddinglage.repository;

import com.weddinglage.domain.Venue;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Short> {

    @EntityGraph(attributePaths = "images")
    Optional<Venue> findWithImagesById(Short id);
}
