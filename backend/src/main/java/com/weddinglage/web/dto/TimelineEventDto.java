package com.weddinglage.web.dto;

import java.time.LocalTime;

public record TimelineEventDto(
        Long id,
        LocalTime eventTime,
        String title,
        String description,
        String icon,
        int sortOrder
) {
}
