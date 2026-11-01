package com.weddinglage.web.dto;

import java.time.Instant;

public record WeddingConfigurationDto(
        String partnerNames,
        Instant weddingAt,
        String welcomeMessage,
        String storyHtml,
        String giftIban,
        String giftHolder,
        boolean showStory,
        boolean showVenue,
        boolean showTimeline,
        boolean showPlaylist,
        boolean showGifts,
        boolean showRsvp,
        boolean showPhotos,
        boolean showOrnaments
) {
}
