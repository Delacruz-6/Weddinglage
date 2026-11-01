package com.weddinglage.web.dto;

import java.time.Instant;

public record WeddingConfigurationDto(
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
        boolean showStory,
        boolean showVenue,
        boolean showTimeline,
        boolean showPlaylist,
        boolean showGifts,
        boolean showRsvp,
        boolean showPhotos,
        boolean showOrnaments,
        boolean rsvpShowNotes,
        boolean rsvpShowDietary,
        boolean rsvpShowTransport,
        boolean rsvpShowSongs,
        boolean rsvpShowChildren
) {
}
