package com.weddinglage.repository;

import com.weddinglage.domain.Rsvp;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RsvpRepository extends JpaRepository<Rsvp, Long> {
    boolean existsByInvitationId(Long invitationId);

    Optional<Rsvp> findByInvitationId(Long invitationId);

    Optional<Rsvp> findByEditTokenHash(String editTokenHash);

    @EntityGraph(attributePaths = {"invitation", "dietaryRequirements", "transportReservations", "songSuggestions", "childGuests"})
    Optional<Rsvp> findDetailedByEditTokenHash(String editTokenHash);

    @EntityGraph(attributePaths = {"invitation", "dietaryRequirements", "transportReservations", "songSuggestions", "childGuests"})
    Optional<Rsvp> findDetailedById(Long id);

    @EntityGraph(attributePaths = {"invitation", "dietaryRequirements", "transportReservations", "songSuggestions", "childGuests"})
    @Query("select r from Rsvp r")
    List<Rsvp> findAllDetailedBy();

    long countByAttendingTrue();
}
