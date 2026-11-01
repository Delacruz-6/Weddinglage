package com.weddinglage.web.dto;

import java.time.Instant;

public record PublicWeddingDto(
        String partnerNames,
        Instant weddingAt,
        String welcomeMessage,
        String storyHtml,
        String giftIban,
        String giftHolder,
        SectionFlagsDto sections
) {
    public record SectionFlagsDto(
            boolean story,
            boolean venue,
            boolean timeline,
            boolean playlist,
            boolean gifts,
            boolean rsvp,
            boolean photos,
            boolean ornaments
    ) {
    }
}
