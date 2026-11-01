package com.weddinglage.web.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class InvitationDtos {

    private InvitationDtos() {
    }

    public record InvitationRequest(
            @NotBlank String inviteCode,
            String label,
            @NotNull @Min(1) Integer maxAttendees,
            Boolean active
    ) {
    }

    public record InvitationDto(
            Long id,
            String inviteCode,
            String label,
            int maxAttendees,
            boolean active,
            boolean hasRsvp,
            String ornamentCode
    ) {
    }

    public record AssignOrnamentRequest(String ornamentCode, Boolean random) {
    }

    public record RegenerateTokenResponse(String editToken) {
    }
}
