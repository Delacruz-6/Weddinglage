package com.weddinglage.repository;

import com.weddinglage.domain.GuestOrnament;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestOrnamentRepository extends JpaRepository<GuestOrnament, Long> {
    Optional<GuestOrnament> findByInvitationId(Long invitationId);

    @EntityGraph(attributePaths = {"ornamentType", "invitation"})
    Optional<GuestOrnament> findDetailedByInvitationId(Long invitationId);

    @EntityGraph(attributePaths = {"ornamentType", "invitation"})
    Optional<GuestOrnament> findDetailedByInvitationInviteCodeIgnoreCase(String inviteCode);

    @EntityGraph(attributePaths = {"ornamentType", "invitation"})
    @org.springframework.data.jpa.repository.Query("select g from GuestOrnament g")
    java.util.List<GuestOrnament> findAllDetailedBy();

    long countByOrnamentTypeId(Long ornamentTypeId);
}
