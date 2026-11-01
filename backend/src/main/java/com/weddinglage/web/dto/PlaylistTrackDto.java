package com.weddinglage.web.dto;

public record PlaylistTrackDto(
        Long id,
        String title,
        String artist,
        String sourceUrl,
        String licenseNote,
        int sortOrder
) {
}
