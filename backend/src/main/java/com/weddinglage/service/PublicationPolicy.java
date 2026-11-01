package com.weddinglage.service;

import com.weddinglage.domain.PhotoPublicationConfig;
import java.time.Instant;

/**
 * Double condition for public photo gallery visibility:
 * publishAt must be reached AND adminAuthorized must be true.
 */
public final class PublicationPolicy {

    private PublicationPolicy() {
    }

    public static boolean isPublished(PhotoPublicationConfig config, Instant now) {
        if (config == null) {
            return false;
        }
        if (!config.isAdminAuthorized()) {
            return false;
        }
        Instant publishAt = config.getPublishAt();
        if (publishAt == null) {
            return false;
        }
        return !publishAt.isAfter(now);
    }
}
