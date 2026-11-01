package com.weddinglage.service;

import com.weddinglage.domain.WeddingConfiguration;
import com.weddinglage.repository.WeddingConfigurationRepository;
import com.weddinglage.web.dto.PublicWeddingDto;
import com.weddinglage.web.dto.WeddingConfigurationDto;
import com.weddinglage.web.error.ApiException;
import java.time.Instant;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigurationService {

    private final WeddingConfigurationRepository repository;
    private final AuditService auditService;

    public ConfigurationService(WeddingConfigurationRepository repository, AuditService auditService) {
        this.repository = repository;
        this.auditService = auditService;
    }

    @Transactional(readOnly = true)
    public PublicWeddingDto getPublic() {
        WeddingConfiguration cfg = requireConfig();
        return new PublicWeddingDto(
                cfg.getPartnerNames(),
                cfg.getWeddingAt(),
                cfg.getWelcomeMessage(),
                cfg.getStoryHtml(),
                cfg.getGiftIban(),
                cfg.getGiftHolder(),
                new PublicWeddingDto.SectionFlagsDto(
                        cfg.isShowStory(),
                        cfg.isShowVenue(),
                        cfg.isShowTimeline(),
                        cfg.isShowPlaylist(),
                        cfg.isShowGifts(),
                        cfg.isShowRsvp(),
                        cfg.isShowPhotos(),
                        cfg.isShowOrnaments()));
    }

    @Transactional(readOnly = true)
    public WeddingConfigurationDto getAdmin() {
        return toDto(requireConfig());
    }

    @Transactional
    public WeddingConfigurationDto update(WeddingConfigurationDto dto, String actor) {
        WeddingConfiguration cfg = requireConfig();
        cfg.setPartnerNames(dto.partnerNames());
        cfg.setWeddingAt(dto.weddingAt() != null ? dto.weddingAt() : cfg.getWeddingAt());
        cfg.setWelcomeMessage(dto.welcomeMessage());
        cfg.setStoryHtml(dto.storyHtml());
        cfg.setGiftIban(dto.giftIban());
        cfg.setGiftHolder(dto.giftHolder());
        cfg.setShowStory(dto.showStory());
        cfg.setShowVenue(dto.showVenue());
        cfg.setShowTimeline(dto.showTimeline());
        cfg.setShowPlaylist(dto.showPlaylist());
        cfg.setShowGifts(dto.showGifts());
        cfg.setShowRsvp(dto.showRsvp());
        cfg.setShowPhotos(dto.showPhotos());
        cfg.setShowOrnaments(dto.showOrnaments());
        cfg.setUpdatedAt(Instant.now());
        repository.save(cfg);
        auditService.record(actor, "CONFIG_UPDATE", "WeddingConfiguration", "1", null);
        return toDto(cfg);
    }

    private WeddingConfiguration requireConfig() {
        return repository.findById((short) 1)
                .orElseThrow(() -> new ApiException("NOT_FOUND", "Configuración no encontrada", HttpStatus.NOT_FOUND));
    }

    private static WeddingConfigurationDto toDto(WeddingConfiguration cfg) {
        return new WeddingConfigurationDto(
                cfg.getPartnerNames(),
                cfg.getWeddingAt(),
                cfg.getWelcomeMessage(),
                cfg.getStoryHtml(),
                cfg.getGiftIban(),
                cfg.getGiftHolder(),
                cfg.isShowStory(),
                cfg.isShowVenue(),
                cfg.isShowTimeline(),
                cfg.isShowPlaylist(),
                cfg.isShowGifts(),
                cfg.isShowRsvp(),
                cfg.isShowPhotos(),
                cfg.isShowOrnaments());
    }
}
