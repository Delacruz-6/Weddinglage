package com.weddinglage.service;

import com.weddinglage.config.WeddinglageProperties;
import com.weddinglage.domain.BusCapacity;
import com.weddinglage.domain.DietaryRequirement;
import com.weddinglage.domain.Invitation;
import com.weddinglage.domain.Rsvp;
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
        Invitation invitation = invitationRepository.findByInviteCodeIgnoreCase(request.inviteCode())
                .filter(Invitation::isActive)
                .orElseThrow(() -> new ApiException("INVALID_INVITE", "Código de invitación no válido", HttpStatus.NOT_FOUND));

        if (rsvpRepository.existsByInvitationId(invitation.getId())) {
            throw new ApiException(
                    "RSVP_EXISTS",
                    "Ya existe una respuesta. Usa el enlace de edición.",
                    HttpStatus.CONFLICT);
        }

        RsvpValidation.validateAttendeeCount(request.adults(), request.children(), invitation.getMaxAttendees());

        String rawToken = TokenHasher.generateToken(32);
        Rsvp rsvp = new Rsvp();
        rsvp.setInvitation(invitation);
        rsvp.setEditTokenHash(TokenHasher.hash(rawToken, properties.getTokenPepper()));
        applyPayload(rsvp, request.attending(), request.adults(), request.children(), request.notes(),
                request.dietaryRequirements(), request.transport(), request.songs(), null);
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
        RsvpValidation.validateAttendeeCount(request.adults(), request.children(), invitation.getMaxAttendees());
        applyPayload(rsvp, request.attending(), request.adults(), request.children(), request.notes(),
                request.dietaryRequirements(), request.transport(), request.songs(), rsvp.getId());
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

    private String resolveTransportStatus(String busSlot, int seats, Long existingRsvpId) {
        return busCapacityRepository.findByBusSlot(busSlot)
                .map(capacity -> evaluateCapacity(capacity, seats, existingRsvpId))
                .orElse("CONFIRMED");
    }

    private String evaluateCapacity(BusCapacity capacity, int seats, Long existingRsvpId) {
        long used = transportReservationRepository.sumConfirmedSeats(capacity.getBusSlot());
        // When updating, confirmed seats already include this RSVP's previous reservation briefly after clear —
        // orphanRemoval clears first within the same persistence context; sum query may still see old rows.
        // Capacity check uses current DB confirmed seats; soft waitlist if full.
        if (used + seats <= capacity.getCapacity()) {
            return "CONFIRMED";
        }
        if (capacity.isWaitlistEnabled()) {
            return "WAITLIST";
        }
        throw new ApiException("BUS_FULL", "No quedan plazas en el autobús de las " + capacity.getBusSlot(), HttpStatus.CONFLICT);
    }

    private RsvpDtos.RsvpViewDto toView(Rsvp rsvp) {
        return new RsvpDtos.RsvpViewDto(
                rsvp.getInvitation().getInviteCode(),
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
                        .toList());
    }
}
