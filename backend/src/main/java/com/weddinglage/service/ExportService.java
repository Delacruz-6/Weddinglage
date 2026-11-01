package com.weddinglage.service;

import com.weddinglage.domain.GuestOrnament;
import com.weddinglage.domain.Invitation;
import com.weddinglage.domain.Rsvp;
import com.weddinglage.repository.GuestOrnamentRepository;
import com.weddinglage.repository.InvitationRepository;
import com.weddinglage.repository.RsvpRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExportService {

    private final InvitationRepository invitationRepository;
    private final RsvpRepository rsvpRepository;
    private final GuestOrnamentRepository guestOrnamentRepository;

    public ExportService(
            InvitationRepository invitationRepository,
            RsvpRepository rsvpRepository,
            GuestOrnamentRepository guestOrnamentRepository) {
        this.invitationRepository = invitationRepository;
        this.rsvpRepository = rsvpRepository;
        this.guestOrnamentRepository = guestOrnamentRepository;
    }

    @Transactional(readOnly = true)
    public String exportGuests() {
        StringBuilder sb = new StringBuilder("inviteCode,label,attending,adults,children,notes\n");
        List<Rsvp> rsvps = rsvpRepository.findAll();
        for (Rsvp rsvp : rsvps) {
            Invitation inv = rsvp.getInvitation();
            sb.append(csv(inv.getInviteCode())).append(',')
                    .append(csv(inv.getLabel())).append(',')
                    .append(rsvp.isAttending()).append(',')
                    .append(rsvp.getAdults()).append(',')
                    .append(rsvp.getChildren()).append(',')
                    .append(csv(rsvp.getNotes())).append('\n');
        }
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public String exportCatering() {
        StringBuilder sb = new StringBuilder("inviteCode,type,detail,adults,children\n");
        for (Rsvp rsvp : rsvpRepository.findAll()) {
            if (rsvp.getDietaryRequirements().isEmpty()) {
                sb.append(csv(rsvp.getInvitation().getInviteCode())).append(",,,,")
                        .append(rsvp.getAdults()).append(',').append(rsvp.getChildren()).append('\n');
            } else {
                for (var d : rsvp.getDietaryRequirements()) {
                    sb.append(csv(rsvp.getInvitation().getInviteCode())).append(',')
                            .append(csv(d.getRequirementType())).append(',')
                            .append(csv(d.getDetail())).append(',')
                            .append(rsvp.getAdults()).append(',')
                            .append(rsvp.getChildren()).append('\n');
                }
            }
        }
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public String exportTransport() {
        StringBuilder sb = new StringBuilder("inviteCode,busSlot,stopId,status,seats\n");
        for (Rsvp rsvp : rsvpRepository.findAll()) {
            for (var t : rsvp.getTransportReservations()) {
                sb.append(csv(rsvp.getInvitation().getInviteCode())).append(',')
                        .append(csv(t.getBusSlot())).append(',')
                        .append(csv(t.getStopId())).append(',')
                        .append(csv(t.getStatus())).append(',')
                        .append(t.getSeats()).append('\n');
            }
        }
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public String exportSongs() {
        StringBuilder sb = new StringBuilder("inviteCode,title,artist\n");
        for (Rsvp rsvp : rsvpRepository.findAll()) {
            for (var s : rsvp.getSongSuggestions()) {
                sb.append(csv(rsvp.getInvitation().getInviteCode())).append(',')
                        .append(csv(s.getTitle())).append(',')
                        .append(csv(s.getArtist())).append('\n');
            }
        }
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public String exportOrnaments() {
        StringBuilder sb = new StringBuilder("inviteCode,label,ornamentCode,ornamentName\n");
        for (GuestOrnament ornament : guestOrnamentRepository.findAllDetailedBy()) {
            Invitation inv = ornament.getInvitation();
            sb.append(csv(inv.getInviteCode())).append(',')
                    .append(csv(inv.getLabel())).append(',')
                    .append(csv(ornament.getOrnamentType().getCode())).append(',')
                    .append(csv(ornament.getOrnamentType().getName())).append('\n');
        }
        for (Invitation inv : invitationRepository.findAll()) {
            if (guestOrnamentRepository.findByInvitationId(inv.getId()).isEmpty()) {
                sb.append(csv(inv.getInviteCode())).append(',')
                        .append(csv(inv.getLabel())).append(",,\n");
            }
        }
        return sb.toString();
    }

    private static String csv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("\n")) {
            return "\"" + escaped + "\"";
        }
        return escaped;
    }
}
