package com.weddinglage.web;

import com.weddinglage.service.InvitationService;
import com.weddinglage.service.RsvpService;
import com.weddinglage.web.dto.OrnamentPublicDto;
import com.weddinglage.web.dto.RsvpDtos;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicRsvpController {

    private final RsvpService rsvpService;
    private final InvitationService invitationService;

    public PublicRsvpController(RsvpService rsvpService, InvitationService invitationService) {
        this.rsvpService = rsvpService;
        this.invitationService = invitationService;
    }

    @PostMapping("/rsvp")
    public RsvpDtos.RsvpSubmitResponse submit(@Valid @RequestBody RsvpDtos.RsvpSubmitRequest request) {
        return rsvpService.submit(request);
    }

    @GetMapping("/rsvp/{editToken}")
    public RsvpDtos.RsvpViewDto get(@PathVariable String editToken) {
        return rsvpService.getByEditToken(editToken);
    }

    @PutMapping("/rsvp/{editToken}")
    public RsvpDtos.RsvpViewDto update(
            @PathVariable String editToken, @Valid @RequestBody RsvpDtos.RsvpUpdateRequest request) {
        return rsvpService.updateByEditToken(editToken, request);
    }

    @GetMapping("/ornaments/{inviteCode}")
    public OrnamentPublicDto ornament(@PathVariable String inviteCode) {
        return invitationService.getPublicOrnament(inviteCode);
    }
}
