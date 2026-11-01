package com.weddinglage.repository;

import com.weddinglage.domain.TimelineEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimelineEventRepository extends JpaRepository<TimelineEvent, Long> {
    List<TimelineEvent> findAllByOrderBySortOrderAscEventTimeAsc();
}
