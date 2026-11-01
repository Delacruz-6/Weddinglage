package com.weddinglage.web.dto;

import java.util.List;

public record DashboardDto(
        long invitationsTotal,
        long invitationsActive,
        long invitationsWithoutRsvp,
        long rsvpTotal,
        long rsvpAttending,
        long rsvpDeclined,
        double responseRatePercent,
        long guestsExpected,
        long adultsExpected,
        long childrenExpected,
        long dietaryEntries,
        long transportReservations,
        long songSuggestions,
        long photosPending,
        long photosApproved,
        long photosRejected,
        boolean galleryPublished,
        long ornamentsAssigned,
        long playlistTracks,
        long rsvpLast7Days,
        List<BusSlotMetric> busSlots
) {
    public record BusSlotMetric(String busSlot, long seats, long reservations) {
    }
}
