package com.weddinglage.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import java.util.List;

public final class RsvpDtos {

    private RsvpDtos() {
    }

    public record DietaryDto(String type, String detail) {
    }

    public record TransportDto(String busSlot, String stopId, Integer seats) {
    }

    public record SongDto(String title, String artist) {
    }

    public record RsvpSubmitRequest(
            @NotBlank String inviteCode,
            @NotNull Boolean attending,
            @Min(0) int adults,
            @Min(0) int children,
            String notes,
            @Valid List<DietaryDto> dietaryRequirements,
            @Valid List<TransportDto> transport,
            @Valid List<SongDto> songs
    ) {
    }

    public record RsvpUpdateRequest(
            @NotNull Boolean attending,
            @Min(0) int adults,
            @Min(0) int children,
            String notes,
            @Valid List<DietaryDto> dietaryRequirements,
            @Valid List<TransportDto> transport,
            @Valid List<SongDto> songs
    ) {
    }

    public record RsvpViewDto(
            String inviteCode,
            boolean attending,
            int adults,
            int children,
            String notes,
            Instant submittedAt,
            Instant updatedAt,
            List<DietaryDto> dietaryRequirements,
            List<TransportViewDto> transport,
            List<SongDto> songs
    ) {
    }

    public record TransportViewDto(String busSlot, String stopId, String status, int seats) {
    }

    public record RsvpSubmitResponse(String editToken, RsvpViewDto rsvp) {
    }
}
