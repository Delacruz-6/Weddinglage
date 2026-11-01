package com.weddinglage.repository;

import com.weddinglage.domain.PlaylistTrack;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistTrackRepository extends JpaRepository<PlaylistTrack, Long> {
    List<PlaylistTrack> findAllByOrderBySortOrderAscIdAsc();
}
