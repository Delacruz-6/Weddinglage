package com.weddinglage.repository;

import com.weddinglage.domain.TransportReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransportReservationRepository extends JpaRepository<TransportReservation, Long> {

    @Query("""
            select coalesce(sum(t.seats), 0) from TransportReservation t
            where t.busSlot = :slot and t.status = 'CONFIRMED'
            """)
    long sumConfirmedSeats(@Param("slot") String slot);
}
