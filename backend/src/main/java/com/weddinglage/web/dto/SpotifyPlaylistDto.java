package com.weddinglage.web.dto;

import java.util.List;

public record SpotifyPlaylistDto(
        String playlistUrl,
        String playlistUri,
        String playlistId,
        String name,
        List<SpotifyTrackDto> tracks
) {
    public record SpotifyTrackDto(
            String id,
            String uri,
            String title,
            String artist,
            String previewUrl
    ) {
    }
}
