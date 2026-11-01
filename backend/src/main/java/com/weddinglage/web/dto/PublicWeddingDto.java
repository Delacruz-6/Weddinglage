package com.weddinglage.web.dto;

import java.time.Instant;

public record PublicWeddingDto(
        String partnerNames,
        Instant weddingAt,
        String welcomeMessage,
        String storyHtml,
        String giftIban,
        String giftHolder,
        String heroImageUrl,
        String storyImageUrl,
        String spotifyPlaylistUrl,
        String ornamentSuggestion,
        SectionFlagsDto sections,
        RsvpFormConfigDto rsvpForm
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

    public record RsvpFormConfigDto(
            boolean showNotes,
            boolean showDietary,
            boolean showTransport,
            boolean showSongs,
            boolean showChildren
    ) {
    }
}
