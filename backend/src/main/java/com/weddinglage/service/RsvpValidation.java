package com.weddinglage.service;

import com.weddinglage.web.error.ApiException;
import org.springframework.http.HttpStatus;

public final class RsvpValidation {

    private RsvpValidation() {
    }

    public static void validateAttendeeCount(int adults, int children, int maxAttendees) {
        if (adults < 0 || children < 0) {
            throw new ApiException("VALIDATION_ERROR", "Los conteos no pueden ser negativos", HttpStatus.BAD_REQUEST);
        }
        if (adults + children > maxAttendees) {
            throw new ApiException(
                    "MAX_ATTENDEES_EXCEEDED",
                    "adults + children no puede superar maxAttendees (" + maxAttendees + ")",
                    HttpStatus.BAD_REQUEST);
        }
    }
}
