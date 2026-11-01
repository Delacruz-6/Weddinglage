package com.weddinglage.service;

import com.weddinglage.config.WeddinglageProperties;
import com.weddinglage.domain.BusCapacity;
import com.weddinglage.domain.DietaryRequirement;
import com.weddinglage.domain.Invitation;
import com.weddinglage.domain.Rsvp;
import com.weddinglage.domain.RsvpChild;
import com.weddinglage.domain.SongSuggestion;
import com.weddinglage.domain.TransportReservation;
import com.weddinglage.repository.BusCapacityRepository;
import com.weddinglage.repository.InvitationRepository;
import com.weddinglage.repository.RsvpRepository;
import com.weddinglage.repository.TransportReservationRepository;
import com.weddinglage.util.TokenHasher;
import com.weddinglage.web.dto.RsvpDtos;
import com.weddinglage.web.error.ApiException;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RsvpService {

    private final InvitationRepository invitationRepository;
    private final RsvpRepository rsvpRepository;
    private final BusCapacityRepository busCapacityRepository;
    private final TransportReservationRepository transportReservationRepository;
    private final WeddinglageProperties properties;

    public RsvpService(
            InvitationRepository invitationRepository,
            RsvpRepository rsvpRepository,
            BusCapacityRepository busCapacityRepository,
            TransportReservationRepository transportReservationRepository,
            WeddinglageProperties properties) {
        this.invitationRepository = invitationRepository;
        this.rsvpRepository = rsvpRepository;
        this.busCapacityRepository = busCapacityRepository;
        this.transportReservationRepository = transportReservationRepository;
        this.properties = properties;
    }

    @Transactional
    public RsvpDtos.RsvpSubmitResponse submit(RsvpDtos.RsvpSubmitRequest request) {
        String fullName = blankToNull(request.fullName());
        if (fullName == null) {
            throw new ApiException(
                    "VALIDATION_ERROR",
                    "Indicad nombre y apellidos.",
                    HttpStatus.BAD_REQUEST);
        }

        List<RsvpDtos.ChildDto> childGuests = request.childGuests() == null ? List.of() : request.childGuests();
        int childrenCount = resolveChildrenCount(request.children(), childGuests);
        int max = properties.getOpenRsvpMaxAttendees();
        RsvpValidation.validateAttendeeCount(request.adults(), childrenCount, max);

        String rawToken = TokenHasher.generateToken(32);
        Rsvp rsvp = new Rsvp();
        rsvp.setInvitation(null);
        rsvp.setFullName(fullName);
        rsvp.setEditTokenHash(TokenHasher.hash(rawToken, properties.getTokenPepper()));
        applyPayload(
                rsvp,
                request.attending(),
                request.adults(),
                childrenCount,
                request.notes(),
                request.dietaryRequirements(),
                request.transport(),
                request.songs(),
                childGuests,
                null);
        rsvpRepository.save(rsvp);
        return new RsvpDtos.RsvpSubmitResponse(rawToken, toView(rsvp));
    }

    @Transactional(readOnly = true)
    public RsvpDtos.RsvpViewDto getByEditToken(String editToken) {
        return toView(findByToken(editToken));
    }

    @Transactional
    public RsvpDtos.RsvpViewDto updateByEditToken(String editToken, RsvpDtos.RsvpUpdateRequest request) {
        Rsvp rsvp = findByToken(editToken);
        Invitation invitation = rsvp.getInvitation();
        List<RsvpDtos.ChildDto> childGuests = request.childGuests() == null ? List.of() : request.childGuests();
        int childrenCount = resolveChildrenCount(request.children(), childGuests);
        int max = invitation != null ? invitation.getMaxAttendees() : properties.getOpenRsvpMaxAttendees();
        RsvpValidation.validateAttendeeCount(request.adults(), childrenCount, max);
        if (blankToNull(request.fullName()) != null) {
            rsvp.setFullName(blankToNull(request.fullName()));
        }
        applyPayload(
                rsvp,
                request.attending(),
                request.adults(),
                childrenCount,
                request.notes(),
                request.dietaryRequirements(),
                request.transport(),
                request.songs(),
                childGuests,
                rsvp.getId());
        rsvp.setUpdatedAt(Instant.now());
        return toView(rsvpRepository.save(rsvp));
    }

    @Transactional
    public String regenerateEditToken(Long invitationId) {
        Invitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Invitación no encontrada", HttpStatus.NOT_FOUND));
        Rsvp rsvp = rsvpRepository.findByInvitationId(invitation.getId())
                .orElseThrow(() -> new ApiException("NOT_FOUND", "RSVP no encontrado", HttpStatus.NOT_FOUND));
        String rawToken = TokenHasher.generateToken(32);
        rsvp.setEditTokenHash(TokenHasher.hash(rawToken, properties.getTokenPepper()));
        rsvp.setUpdatedAt(Instant.now());
        rsvpRepository.save(rsvp);
        return rawToken;
    }

    private Rsvp findByToken(String editToken) {
        String hash = TokenHasher.hash(editToken, properties.getTokenPepper());
        return rsvpRepository.findDetailedByEditTokenHash(hash)
                .orElseThrow(() -> new ApiException("INVALID_EDIT_TOKEN", "Token de edición no válido", HttpStatus.UNAUTHORIZED));
    }

    private void applyPayload(
            Rsvp rsvp,
            boolean attending,
            int adults,
            int children,
            String notes,
            List<RsvpDtos.DietaryDto> dietary,
            List<RsvpDtos.TransportDto> transport,
            List<RsvpDtos.SongDto> songs,
            List<RsvpDtos.ChildDto> childGuests,
            Long existingRsvpId) {
        rsvp.setAttending(attending);
        rsvp.setAdults(adults);
        rsvp.setChildren(children);
        rsvp.setNotes(notes);

        rsvp.getDietaryRequirements().clear();
        if (dietary != null) {
            for (RsvpDtos.DietaryDto dto : dietary) {
                DietaryRequirement req = new DietaryRequirement();
                req.setRsvp(rsvp);
                req.setRequirementType(dto.type());
                req.setDetail(dto.detail());
                rsvp.getDietaryRequirements().add(req);
            }
        }

        rsvp.getChildGuests().clear();
        if (attending && childGuests != null) {
            int order = 0;
            for (RsvpDtos.ChildDto dto : childGuests) {
                if (dto.fullName() == null || dto.fullName().isBlank()) {
                    continue;
                }
                RsvpChild child = new RsvpChild();
                child.setRsvp(rsvp);
                child.setFullName(dto.fullName().trim());
                child.setAge(dto.age());
                child.setIntolerances(blankToNull(dto.intolerances()));
                child.setSortOrder(order++);
                rsvp.getChildGuests().add(child);
            }
        }

        rsvp.getTransportReservations().clear();
        if (attending && transport != null) {
            for (RsvpDtos.TransportDto dto : transport) {
                if (dto.busSlot() == null || dto.busSlot().isBlank()) {
                    continue;
                }
                if (!dto.busSlot().equals("18:00") && !dto.busSlot().equals("22:00")) {
                    throw new ApiException("VALIDATION_ERROR", "busSlot debe ser 18:00 o 22:00", HttpStatus.BAD_REQUEST);
                }
                int seats = dto.seats() == null ? Math.max(1, adults + children) : dto.seats();
                String status = resolveTransportStatus(dto.busSlot(), seats, existingRsvpId);
                TransportReservation reservation = new TransportReservation();
                reservation.setRsvp(rsvp);
                reservation.setBusSlot(dto.busSlot());
                reservation.setStopId(dto.stopId());
                reservation.setSeats(seats);
                reservation.setStatus(status);
                rsvp.getTransportReservations().add(reservation);
            }
        }

        rsvp.getSongSuggestions().clear();
        if (songs != null) {
            for (RsvpDtos.SongDto dto : songs) {
                if (dto.title() == null || dto.title().isBlank()) {
                    continue;
                }
                SongSuggestion song = new SongSuggestion();
                song.setRsvp(rsvp);
                song.setTitle(dto.title());
                song.setArtist(dto.artist());
                rsvp.getSongSuggestions().add(song);
            }
        }
    }

    private static int resolveChildrenCount(int childrenField, List<RsvpDtos.ChildDto> childGuests) {
        long named = childGuests.stream()
                .filter(c -> c.fullName() != null && !c.fullName().isBlank())
                .count();
        if (named > 0) {
            return (int) named;
        }
        return childrenField;
    }

    private String resolveTransportStatus(String busSlot, int seats, Long existingRsvpId) {
        return busCapacityRepository.findByBusSlot(busSlot)
                .map(capacity -> evaluateCapacity(capacity, seats, existingRsvpId))
                .orElse("CONFIRMED");
    }

    private String evaluateCapacity(BusCapacity capacity, int seats, Long existingRsvpId) {
        long used = transportReservationRepository.sumConfirmedSeats(capacity.getBusSlot());
        if (used + seats <= capacity.getCapacity()) {
            return "CONFIRMED";
        }
        if (capacity.isWaitlistEnabled()) {
            return "WAITLIST";
        }
        throw new ApiException("BUS_FULL", "No quedan plazas en el autobús de las " + capacity.getBusSlot(), HttpStatus.CONFLICT);
    }

    private RsvpDtos.RsvpViewDto toView(Rsvp rsvp) {
        Invitation invitation = rsvp.getInvitation();
        return new RsvpDtos.RsvpViewDto(
                invitation != null ? invitation.getInviteCode() : null,
                rsvp.getFullName(),
                rsvp.isAttending(),
                rsvp.getAdults(),
                rsvp.getChildren(),
                rsvp.getNotes(),
                rsvp.getSubmittedAt(),
                rsvp.getUpdatedAt(),
                rsvp.getDietaryRequirements().stream()
                        .map(d -> new RsvpDtos.DietaryDto(d.getRequirementType(), d.getDetail()))
                        .toList(),
                rsvp.getTransportReservations().stream()
                        .map(t -> new RsvpDtos.TransportViewDto(t.getBusSlot(), t.getStopId(), t.getStatus(), t.getSeats()))
                        .toList(),
                rsvp.getSongSuggestions().stream()
                        .map(s -> new RsvpDtos.SongDto(s.getTitle(), s.getArtist()))
                        .toList(),
                rsvp.getChildGuests().stream()
                        .map(c -> new RsvpDtos.ChildDto(c.getFullName(), c.getAge(), c.getIntolerances()))
                        .toList());
    }

    private static String blankToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }
}
