package com.weddinglage.service;

import com.weddinglage.domain.GuestOrnament;
import com.weddinglage.domain.Invitation;
import com.weddinglage.domain.Rsvp;
import com.weddinglage.domain.RsvpChild;
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
        StringBuilder sb = new StringBuilder(
                "fullName,attending,adults,children,childNames,notes,submittedAt\n");
        List<Rsvp> rsvps = rsvpRepository.findAll();
        for (Rsvp rsvp : rsvps) {
            sb.append(csv(rsvp.getFullName())).append(',')
                    .append(rsvp.isAttending()).append(',')
                    .append(rsvp.getAdults()).append(',')
                    .append(rsvp.getChildren()).append(',')
                    .append(csv(childNames(rsvp))).append(',')
                    .append(csv(rsvp.getNotes())).append(',')
                    .append(csv(rsvp.getSubmittedAt() != null ? rsvp.getSubmittedAt().toString() : null))
                    .append('\n');
        }
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public String exportCatering() {
        StringBuilder sb = new StringBuilder("fullName,guestType,name,age,type,detail,adults,children\n");
        for (Rsvp rsvp : rsvpRepository.findAll()) {
            if (rsvp.getDietaryRequirements().isEmpty() && rsvp.getChildGuests().isEmpty()) {
                sb.append(csv(rsvp.getFullName())).append(",adult,,,,")
                        .append(rsvp.getAdults()).append(',').append(rsvp.getChildren()).append('\n');
            }
            for (var d : rsvp.getDietaryRequirements()) {
                sb.append(csv(rsvp.getFullName())).append(",adult,,,")
                        .append(csv(d.getRequirementType())).append(',')
                        .append(csv(d.getDetail())).append(',')
                        .append(rsvp.getAdults()).append(',')
                        .append(rsvp.getChildren()).append('\n');
            }
            for (RsvpChild child : rsvp.getChildGuests()) {
                sb.append(csv(rsvp.getFullName())).append(",child,")
                        .append(csv(child.getFullName())).append(',')
                        .append(child.getAge() == null ? "" : child.getAge()).append(',')
                        .append(csv("intolerances")).append(',')
                        .append(csv(child.getIntolerances())).append(',')
                        .append(rsvp.getAdults()).append(',')
                        .append(rsvp.getChildren()).append('\n');
            }
        }
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public String exportTransport() {
        StringBuilder sb = new StringBuilder("fullName,busSlot,stopId,status,seats\n");
        for (Rsvp rsvp : rsvpRepository.findAll()) {
            for (var t : rsvp.getTransportReservations()) {
                sb.append(csv(rsvp.getFullName())).append(',')
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
        StringBuilder sb = new StringBuilder("fullName,title,artist\n");
        for (Rsvp rsvp : rsvpRepository.findAll()) {
            for (var s : rsvp.getSongSuggestions()) {
                sb.append(csv(rsvp.getFullName())).append(',')
                        .append(csv(s.getTitle())).append(',')
                        .append(csv(s.getArtist())).append('\n');
            }
        }
        return sb.toString();
    }

    @Transactional(readOnly = true)
    public String exportOrnaments() {
        StringBuilder sb = new StringBuilder("label,ornamentCode,ornamentName\n");
        for (GuestOrnament ornament : guestOrnamentRepository.findAllDetailedBy()) {
            Invitation inv = ornament.getInvitation();
            sb.append(csv(inv != null ? inv.getLabel() : null)).append(',')
                    .append(csv(ornament.getOrnamentType().getCode())).append(',')
                    .append(csv(ornament.getOrnamentType().getName())).append('\n');
        }
        for (Invitation inv : invitationRepository.findAll()) {
            if (guestOrnamentRepository.findByInvitationId(inv.getId()).isEmpty()) {
                sb.append(csv(inv.getLabel())).append(",,\n");
            }
        }
        return sb.toString();
    }

    private static String childNames(Rsvp rsvp) {
        if (rsvp.getChildGuests() == null || rsvp.getChildGuests().isEmpty()) {
            return null;
        }
        StringBuilder names = new StringBuilder();
        for (RsvpChild child : rsvp.getChildGuests()) {
            if (names.length() > 0) {
                names.append("; ");
            }
            names.append(child.getFullName());
            if (child.getAge() != null) {
                names.append(" (").append(child.getAge()).append(')');
            }
            if (child.getIntolerances() != null && !child.getIntolerances().isBlank()) {
                names.append(" [").append(child.getIntolerances().trim()).append(']');
            }
        }
        return names.toString();
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
