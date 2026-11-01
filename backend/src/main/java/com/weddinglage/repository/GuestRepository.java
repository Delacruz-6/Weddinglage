package com.weddinglage.repository;

import com.weddinglage.domain.Guest;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepository extends JpaRepository<Guest, Long> {
    List<Guest> findByInvitationId(Long invitationId);
}
