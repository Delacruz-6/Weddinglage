package com.weddinglage.repository;

import com.weddinglage.domain.WeddingConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WeddingConfigurationRepository extends JpaRepository<WeddingConfiguration, Short> {
}
