package com.weddinglage.repository;

import com.weddinglage.domain.PhotoUploadToken;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoUploadTokenRepository extends JpaRepository<PhotoUploadToken, Long> {
    Optional<PhotoUploadToken> findByTokenHash(String tokenHash);
}
