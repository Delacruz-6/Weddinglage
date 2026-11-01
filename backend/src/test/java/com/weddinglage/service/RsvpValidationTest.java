package com.weddinglage.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.weddinglage.web.error.ApiException;
import org.junit.jupiter.api.Test;

class RsvpValidationTest {

    @Test
    void allowsCountsWithinMaxAttendees() {
        assertDoesNotThrow(() -> RsvpValidation.validateAttendeeCount(2, 1, 4));
        assertDoesNotThrow(() -> RsvpValidation.validateAttendeeCount(0, 0, 2));
    }

    @Test
    void rejectsWhenAdultsPlusChildrenExceedMax() {
        ApiException ex = assertThrows(ApiException.class, () -> RsvpValidation.validateAttendeeCount(3, 2, 4));
        assertEquals("MAX_ATTENDEES_EXCEEDED", ex.getCode());
    }
}
