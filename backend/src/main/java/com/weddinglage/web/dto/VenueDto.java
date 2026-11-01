package com.weddinglage.web.dto;

import java.util.List;

public record VenueDto(
        String name,
        String addressLine,
        String city,
        String postalCode,
        String country,
        Double latitude,
        Double longitude,
        String howToArrive,
        String parkingNotes,
        List<VenueImageDto> images
) {
    public record VenueImageDto(Long id, String imageUrl, String altText, int sortOrder) {
    }
}
