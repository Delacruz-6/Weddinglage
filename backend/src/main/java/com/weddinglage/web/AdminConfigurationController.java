package com.weddinglage.web;

import com.weddinglage.service.ConfigurationService;
import com.weddinglage.web.dto.WeddingConfigurationDto;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/configuration")
public class AdminConfigurationController {

    private final ConfigurationService configurationService;

    public AdminConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @GetMapping
    public WeddingConfigurationDto get() {
        return configurationService.getAdmin();
    }

    @PutMapping
    public WeddingConfigurationDto put(@Valid @RequestBody WeddingConfigurationDto dto, Authentication auth) {
        return configurationService.update(dto, auth.getName());
    }
}
