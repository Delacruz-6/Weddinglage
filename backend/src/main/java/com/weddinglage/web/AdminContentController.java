package com.weddinglage.web;

import com.weddinglage.service.ContentService;
import com.weddinglage.web.dto.PlaylistTrackDto;
import com.weddinglage.web.dto.TimelineEventDto;
import com.weddinglage.web.dto.VenueDto;
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
@RequestMapping("/api/admin")
public class AdminContentController {

    private final ContentService contentService;

    public AdminContentController(ContentService contentService) {
        this.contentService = contentService;
    }

    @GetMapping("/venue")
    public VenueDto getVenue() {
        return contentService.getVenue();
    }

    @PutMapping("/venue")
    public VenueDto putVenue(@RequestBody VenueDto dto) {
        return contentService.updateVenue(dto);
    }

    @GetMapping("/timeline")
    public List<TimelineEventDto> listTimeline() {
        return contentService.listTimeline();
    }

    @PostMapping("/timeline")
    public TimelineEventDto createTimeline(@RequestBody TimelineEventDto dto) {
        return contentService.createTimeline(dto);
    }

    @PutMapping("/timeline/{id}")
    public TimelineEventDto updateTimeline(@PathVariable Long id, @RequestBody TimelineEventDto dto) {
        return contentService.updateTimeline(id, dto);
    }

    @DeleteMapping("/timeline/{id}")
    public Map<String, Boolean> deleteTimeline(@PathVariable Long id) {
        contentService.deleteTimeline(id);
        return Map.of("ok", true);
    }

    @GetMapping("/playlist")
    public List<PlaylistTrackDto> listPlaylist() {
        return contentService.listPlaylist();
    }

    @PostMapping("/playlist")
    public PlaylistTrackDto createTrack(@RequestBody PlaylistTrackDto dto) {
        return contentService.createTrack(dto);
    }

    @PutMapping("/playlist/{id}")
    public PlaylistTrackDto updateTrack(@PathVariable Long id, @RequestBody PlaylistTrackDto dto) {
        return contentService.updateTrack(id, dto);
    }

    @DeleteMapping("/playlist/{id}")
    public Map<String, Boolean> deleteTrack(@PathVariable Long id) {
        contentService.deleteTrack(id);
        return Map.of("ok", true);
    }
}
