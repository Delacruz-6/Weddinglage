package com.weddinglage.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class TokenHasherTest {

    @Test
    void hashMatchIsDeterministicAndPepperSensitive() {
        String token = "edit-token-abc";
        String pepper = "dev-pepper";
        String hash = TokenHasher.hash(token, pepper);

        assertTrue(TokenHasher.matches(token, pepper, hash));
        assertFalse(TokenHasher.matches(token + "x", pepper, hash));
        assertFalse(TokenHasher.matches(token, "other-pepper", hash));
        assertNotEquals(hash, TokenHasher.hash(token, "other-pepper"));
    }
}
