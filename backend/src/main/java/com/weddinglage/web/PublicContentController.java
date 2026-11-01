package com.weddinglage.web;

import com.weddinglage.service.ConfigurationService;
import com.weddinglage.service.ContentService;
import com.weddinglage.web.dto.PlaylistTrackDto;
import com.weddinglage.web.dto.PublicWeddingDto;
import com.weddinglage.web.dto.TimelineEventDto;
import com.weddinglage.web.dto.VenueDto;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class PublicContentController {

    private final ConfigurationService configurationService;
    private final ContentService contentService;

    public PublicContentController(ConfigurationService configurationService, ContentService contentService) {
        this.configurationService = configurationService;
        this.contentService = contentService;
    }

    @GetMapping("/wedding")
    public PublicWeddingDto wedding() {
        return configurationService.getPublic();
    }

    @GetMapping("/venue")
    public VenueDto venue() {
        return contentService.getVenue();
    }

    @GetMapping("/timeline")
    public List<TimelineEventDto> timeline() {
        return contentService.listTimeline();
    }

    @GetMapping("/playlist")
    public List<PlaylistTrackDto> playlist() {
        return contentService.listPlaylist();
    }
}
