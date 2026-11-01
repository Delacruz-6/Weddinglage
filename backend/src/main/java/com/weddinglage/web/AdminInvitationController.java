package com.weddinglage.web;

import com.weddinglage.service.InvitationService;
import com.weddinglage.web.dto.InvitationDtos;
import com.weddinglage.web.dto.OrnamentPublicDto;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/invitations")
public class AdminInvitationController {

    private final InvitationService invitationService;

    public AdminInvitationController(InvitationService invitationService) {
        this.invitationService = invitationService;
    }

    @GetMapping
    public List<InvitationDtos.InvitationDto> list() {
        return invitationService.list();
    }

    @GetMapping("/{id}")
    public InvitationDtos.InvitationDto get(@PathVariable Long id) {
        return invitationService.get(id);
    }

    @PostMapping
    public InvitationDtos.InvitationDto create(@Valid @RequestBody InvitationDtos.InvitationRequest request) {
        return invitationService.create(request);
    }

    @PutMapping("/{id}")
    public InvitationDtos.InvitationDto update(
            @PathVariable Long id, @Valid @RequestBody InvitationDtos.InvitationRequest request) {
        return invitationService.update(id, request);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> delete(@PathVariable Long id) {
        invitationService.delete(id);
        return Map.of("ok", true);
    }

    @PostMapping("/{id}/ornament")
    public OrnamentPublicDto assignOrnament(
            @PathVariable Long id, @RequestBody(required = false) InvitationDtos.AssignOrnamentRequest request) {
        InvitationDtos.AssignOrnamentRequest body =
                request == null ? new InvitationDtos.AssignOrnamentRequest(null, true) : request;
        return invitationService.assignOrnament(id, body);
    }

    @PostMapping("/{id}/regenerate-token")
    public InvitationDtos.RegenerateTokenResponse regenerate(@PathVariable Long id) {
        return invitationService.regenerateToken(id);
    }
}
