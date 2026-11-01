package com.weddinglage.repository;

import com.weddinglage.domain.Invitation;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InvitationRepository extends JpaRepository<Invitation, Long> {
    Optional<Invitation> findByInviteCodeIgnoreCase(String inviteCode);
    boolean existsByInviteCodeIgnoreCase(String inviteCode);
}
