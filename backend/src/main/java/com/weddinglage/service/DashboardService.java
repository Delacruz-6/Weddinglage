package com.weddinglage.service;

import com.weddinglage.domain.Photo;
import com.weddinglage.repository.GuestOrnamentRepository;
import com.weddinglage.repository.InvitationRepository;
import com.weddinglage.repository.PhotoRepository;
import com.weddinglage.repository.RsvpRepository;
import com.weddinglage.web.dto.DashboardDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {

    private final InvitationRepository invitationRepository;
    private final RsvpRepository rsvpRepository;
    private final PhotoRepository photoRepository;
    private final GuestOrnamentRepository guestOrnamentRepository;
    private final PhotoService photoService;

    public DashboardService(
            InvitationRepository invitationRepository,
            RsvpRepository rsvpRepository,
            PhotoRepository photoRepository,
            GuestOrnamentRepository guestOrnamentRepository,
            PhotoService photoService) {
        this.invitationRepository = invitationRepository;
        this.rsvpRepository = rsvpRepository;
        this.photoRepository = photoRepository;
        this.guestOrnamentRepository = guestOrnamentRepository;
        this.photoService = photoService;
    }

    @Transactional(readOnly = true)
    public DashboardDto getDashboard() {
        long invitationsTotal = invitationRepository.count();
        long invitationsActive = invitationRepository.findAll().stream().filter(i -> i.isActive()).count();
        long rsvpTotal = rsvpRepository.count();
        long rsvpAttending = rsvpRepository.countByAttendingTrue();
        long rsvpDeclined = rsvpTotal - rsvpAttending;
        long guestsExpected = rsvpRepository.findAll().stream()
                .filter(r -> r.isAttending())
                .mapToLong(r -> r.getAdults() + r.getChildren())
                .sum();
        return new DashboardDto(
                invitationsTotal,
                invitationsActive,
                rsvpTotal,
                rsvpAttending,
                rsvpDeclined,
                guestsExpected,
                photoRepository.countByStatus(Photo.STATUS_PENDING),
                photoRepository.countByStatus(Photo.STATUS_APPROVED),
                photoRepository.countByStatus(Photo.STATUS_REJECTED),
                photoService.isGalleryPublished(),
                guestOrnamentRepository.count());
    }
}
