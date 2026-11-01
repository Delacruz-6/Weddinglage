package com.weddinglage.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.weddinglage.domain.PhotoPublicationConfig;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class PublicationPolicyTest {

    @Test
    void requiresBothPublishAtReachedAndAdminAuthorized() {
        Instant now = Instant.parse("2027-12-25T12:00:00Z");
        PhotoPublicationConfig config = new PhotoPublicationConfig();

        config.setPublishAt(Instant.parse("2027-12-26T00:00:00Z"));
        config.setAdminAuthorized(false);
        assertFalse(PublicationPolicy.isPublished(config, now));

        config.setAdminAuthorized(true);
        assertFalse(PublicationPolicy.isPublished(config, now));

        config.setPublishAt(Instant.parse("2027-12-24T00:00:00Z"));
        config.setAdminAuthorized(false);
        assertFalse(PublicationPolicy.isPublished(config, now));

        config.setAdminAuthorized(true);
        assertTrue(PublicationPolicy.isPublished(config, now));
    }

    @Test
    void nullPublishAtNeverPublishes() {
        PhotoPublicationConfig config = new PhotoPublicationConfig();
        config.setAdminAuthorized(true);
        config.setPublishAt(null);
        assertFalse(PublicationPolicy.isPublished(config, Instant.now()));
    }
}
