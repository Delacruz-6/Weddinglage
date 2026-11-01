package com.weddinglage.repository;

import com.weddinglage.domain.OrnamentType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrnamentTypeRepository extends JpaRepository<OrnamentType, Long> {
    List<OrnamentType> findByActiveTrue();
    Optional<OrnamentType> findByCodeIgnoreCase(String code);
}
