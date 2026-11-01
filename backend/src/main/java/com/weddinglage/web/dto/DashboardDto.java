package com.weddinglage.web.dto;

public record DashboardDto(
        long invitationsTotal,
        long invitationsActive,
        long rsvpTotal,
        long rsvpAttending,
        long rsvpDeclined,
        long guestsExpected,
        long photosPending,
        long photosApproved,
        long photosRejected,
        boolean galleryPublished,
        long ornamentsAssigned
) {
}
