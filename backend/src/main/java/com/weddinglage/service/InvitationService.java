package com.weddinglage.service;

import com.weddinglage.domain.GuestOrnament;
import com.weddinglage.domain.Invitation;
import com.weddinglage.domain.OrnamentType;
import com.weddinglage.repository.GuestOrnamentRepository;
import com.weddinglage.repository.InvitationRepository;
import com.weddinglage.repository.OrnamentTypeRepository;
import com.weddinglage.repository.RsvpRepository;
import com.weddinglage.web.dto.InvitationDtos;
import com.weddinglage.web.dto.OrnamentPublicDto;
import com.weddinglage.web.error.ApiException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final RsvpRepository rsvpRepository;
    private final GuestOrnamentRepository guestOrnamentRepository;
    private final OrnamentTypeRepository ornamentTypeRepository;
    private final RsvpService rsvpService;

    public InvitationService(
            InvitationRepository invitationRepository,
            RsvpRepository rsvpRepository,
            GuestOrnamentRepository guestOrnamentRepository,
            OrnamentTypeRepository ornamentTypeRepository,
            RsvpService rsvpService) {
        this.invitationRepository = invitationRepository;
        this.rsvpRepository = rsvpRepository;
        this.guestOrnamentRepository = guestOrnamentRepository;
        this.ornamentTypeRepository = ornamentTypeRepository;
        this.rsvpService = rsvpService;
    }

    @Transactional(readOnly = true)
    public List<InvitationDtos.InvitationDto> list() {
        return invitationRepository.findAll().stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public InvitationDtos.InvitationDto get(Long id) {
        return toDto(require(id));
    }

    @Transactional
    public InvitationDtos.InvitationDto create(InvitationDtos.InvitationRequest request) {
        if (invitationRepository.existsByInviteCodeIgnoreCase(request.inviteCode())) {
            throw new ApiException("CONFLICT", "El código de invitación ya existe", HttpStatus.CONFLICT);
        }
        Invitation invitation = new Invitation();
        apply(invitation, request);
        return toDto(invitationRepository.save(invitation));
    }

    @Transactional
    public InvitationDtos.InvitationDto update(Long id, InvitationDtos.InvitationRequest request) {
        Invitation invitation = require(id);
        invitationRepository.findByInviteCodeIgnoreCase(request.inviteCode())
                .filter(other -> !other.getId().equals(id))
                .ifPresent(other -> {
                    throw new ApiException("CONFLICT", "El código de invitación ya existe", HttpStatus.CONFLICT);
                });
        apply(invitation, request);
        invitation.setUpdatedAt(Instant.now());
        return toDto(invitationRepository.save(invitation));
    }

    @Transactional
    public void delete(Long id) {
        if (!invitationRepository.existsById(id)) {
            throw new ApiException("NOT_FOUND", "Invitación no encontrada", HttpStatus.NOT_FOUND);
        }
        invitationRepository.deleteById(id);
    }

    @Transactional
    public OrnamentPublicDto assignOrnament(Long invitationId, InvitationDtos.AssignOrnamentRequest request) {
        Invitation invitation = require(invitationId);
        OrnamentType type;
        if (Boolean.TRUE.equals(request.random()) || request.ornamentCode() == null || request.ornamentCode().isBlank()) {
            List<OrnamentType> active = new ArrayList<>(ornamentTypeRepository.findByActiveTrue());
            if (active.isEmpty()) {
                throw new ApiException("NOT_FOUND", "No hay tipos de adorno activos", HttpStatus.NOT_FOUND);
            }
            Collections.shuffle(active, ThreadLocalRandom.current());
            type = active.getFirst();
        } else {
            type = ornamentTypeRepository.findByCodeIgnoreCase(request.ornamentCode())
                    .orElseThrow(() -> new ApiException("NOT_FOUND", "Tipo de adorno no encontrado", HttpStatus.NOT_FOUND));
        }

        GuestOrnament ornament = guestOrnamentRepository.findByInvitationId(invitationId).orElseGet(GuestOrnament::new);
        ornament.setInvitation(invitation);
        ornament.setOrnamentType(type);
        ornament.setAssignedAt(Instant.now());
        guestOrnamentRepository.save(ornament);
        return new OrnamentPublicDto(invitation.getInviteCode(), type.getCode(), type.getName(), type.getImageKey());
    }

    @Transactional
    public InvitationDtos.RegenerateTokenResponse regenerateToken(Long invitationId) {
        return new InvitationDtos.RegenerateTokenResponse(rsvpService.regenerateEditToken(invitationId));
    }

    @Transactional(readOnly = true)
    public OrnamentPublicDto getPublicOrnament(String inviteCode) {
        Invitation invitation = invitationRepository.findByInviteCodeIgnoreCase(inviteCode)
                .filter(Invitation::isActive)
                .orElseThrow(() -> new ApiException("INVALID_INVITE", "Código de invitación no válido", HttpStatus.NOT_FOUND));
        GuestOrnament ornament = guestOrnamentRepository.findDetailedByInvitationInviteCodeIgnoreCase(inviteCode)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Todavía no hay adorno asignado", HttpStatus.NOT_FOUND));
        OrnamentType type = ornament.getOrnamentType();
        return new OrnamentPublicDto(invitation.getInviteCode(), type.getCode(), type.getName(), type.getImageKey());
    }

    private Invitation require(Long id) {
        return invitationRepository.findById(id)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Invitación no encontrada", HttpStatus.NOT_FOUND));
    }

    private void apply(Invitation invitation, InvitationDtos.InvitationRequest request) {
        invitation.setInviteCode(request.inviteCode().trim().toUpperCase());
        invitation.setLabel(request.label());
        invitation.setMaxAttendees(request.maxAttendees());
        invitation.setActive(request.active() == null || request.active());
    }

    private InvitationDtos.InvitationDto toDto(Invitation invitation) {
        boolean hasRsvp = rsvpRepository.existsByInvitationId(invitation.getId());
        String ornamentCode = guestOrnamentRepository.findDetailedByInvitationId(invitation.getId())
                .map(o -> o.getOrnamentType().getCode())
                .orElse(null);
        return new InvitationDtos.InvitationDto(
                invitation.getId(),
                invitation.getInviteCode(),
                invitation.getLabel(),
                invitation.getMaxAttendees(),
                invitation.isActive(),
                hasRsvp,
                ornamentCode);
    }
}
