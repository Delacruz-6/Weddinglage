package com.weddinglage.service;

import com.weddinglage.domain.Photo;
import com.weddinglage.domain.Rsvp;
import com.weddinglage.domain.TransportReservation;
import com.weddinglage.repository.GuestOrnamentRepository;
import com.weddinglage.repository.InvitationRepository;
import com.weddinglage.repository.PhotoRepository;
import com.weddinglage.repository.PlaylistTrackRepository;
import com.weddinglage.repository.RsvpRepository;
import com.weddinglage.web.dto.DashboardDto;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final InvitationRepository invitationRepository;
    private final RsvpRepository rsvpRepository;
    private final PhotoRepository photoRepository;
    private final GuestOrnamentRepository guestOrnamentRepository;
    private final PlaylistTrackRepository playlistTrackRepository;
    private final PhotoService photoService;

    public DashboardService(
            InvitationRepository invitationRepository,
            RsvpRepository rsvpRepository,
            PhotoRepository photoRepository,
            GuestOrnamentRepository guestOrnamentRepository,
            PlaylistTrackRepository playlistTrackRepository,
            PhotoService photoService) {
        this.invitationRepository = invitationRepository;
        this.rsvpRepository = rsvpRepository;
        this.photoRepository = photoRepository;
        this.guestOrnamentRepository = guestOrnamentRepository;
        this.playlistTrackRepository = playlistTrackRepository;
        this.photoService = photoService;
    }

    @Transactional(readOnly = true)
    public DashboardDto getDashboard() {
        long invitationsTotal = invitationRepository.count();
        long invitationsActive = invitationRepository.findAll().stream().filter(i -> i.isActive()).count();
        List<Rsvp> rsvps = rsvpRepository.findAll();
        long rsvpTotal = rsvps.size();
        long rsvpAttending = rsvps.stream().filter(Rsvp::isAttending).count();
        long rsvpDeclined = rsvpTotal - rsvpAttending;
        long invitationsWithoutRsvp = Math.max(0, invitationsActive - rsvpTotal);
        double responseRate = invitationsActive == 0 ? 0.0 : (100.0 * rsvpTotal / invitationsActive);

        long adultsExpected = rsvps.stream().filter(Rsvp::isAttending).mapToLong(Rsvp::getAdults).sum();
        long childrenExpected = rsvps.stream().filter(Rsvp::isAttending).mapToLong(Rsvp::getChildren).sum();
        long guestsExpected = adultsExpected + childrenExpected;

        long dietaryEntries = rsvps.stream().mapToLong(r -> r.getDietaryRequirements().size()).sum();
        long transportReservations = rsvps.stream().mapToLong(r -> r.getTransportReservations().size()).sum();
        long songSuggestions = rsvps.stream().mapToLong(r -> r.getSongSuggestions().size()).sum();

        Instant weekAgo = Instant.now().minus(7, ChronoUnit.DAYS);
        long rsvpLast7Days = rsvps.stream()
                .filter(r -> r.getSubmittedAt() != null && !r.getSubmittedAt().isBefore(weekAgo))
                .count();

        Map<String, long[]> busAgg = new LinkedHashMap<>();
        for (Rsvp rsvp : rsvps) {
            if (!rsvp.isAttending()) {
                continue;
            }
            for (TransportReservation t : rsvp.getTransportReservations()) {
                String slot = t.getBusSlot() == null || t.getBusSlot().isBlank() ? "SIN_HORARIO" : t.getBusSlot();
                long[] agg = busAgg.computeIfAbsent(slot, k -> new long[] {0, 0});
                agg[0] += Math.max(0, t.getSeats());
                agg[1] += 1;
            }
        }
        List<DashboardDto.BusSlotMetric> busSlots = busAgg.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(e -> new DashboardDto.BusSlotMetric(e.getKey(), e.getValue()[0], e.getValue()[1]))
                .collect(Collectors.toList());

        return new DashboardDto(
                invitationsTotal,
                invitationsActive,
                invitationsWithoutRsvp,
                rsvpTotal,
                rsvpAttending,
                rsvpDeclined,
                Math.round(responseRate * 10.0) / 10.0,
                guestsExpected,
                adultsExpected,
                childrenExpected,
                dietaryEntries,
                transportReservations,
                songSuggestions,
                photoRepository.countByStatus(Photo.STATUS_PENDING),
                photoRepository.countByStatus(Photo.STATUS_APPROVED),
                photoRepository.countByStatus(Photo.STATUS_REJECTED),
                photoService.isGalleryPublished(),
                guestOrnamentRepository.count(),
                playlistTrackRepository.count(),
                rsvpLast7Days,
                busSlots);
    }
}
