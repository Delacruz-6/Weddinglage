package com.weddinglage.repository;

import com.weddinglage.domain.SongSuggestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SongSuggestionRepository extends JpaRepository<SongSuggestion, Long> {
}
