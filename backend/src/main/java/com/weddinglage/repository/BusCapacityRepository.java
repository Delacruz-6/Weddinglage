package com.weddinglage.repository;

import com.weddinglage.domain.BusCapacity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BusCapacityRepository extends JpaRepository<BusCapacity, Long> {
    Optional<BusCapacity> findByBusSlot(String busSlot);
}
