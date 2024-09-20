package com.company_name.ums.utils;
import com.company_name.ums.exception.BadEntryException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class UUIDGenerator {

    public static UUID generateUUID(String email) {
        // Get the current timestamp
        long timestamp = System.currentTimeMillis();

        // Combine email and timestamp
        String combined = email + ":" + timestamp;

        try {
            // Create a SHA-256 hash of the combined string
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(combined.getBytes(StandardCharsets.UTF_8));

            // Convert the hash to a UUID
            long mostSigBits = 0;
            long leastSigBits = 0;

            for (int i = 0; i < 8; i++) {
                mostSigBits <<= 8;
                mostSigBits |= (hash[i] & 0xff);
            }
            for (int i = 8; i < 16; i++) {
                leastSigBits <<= 8;
                leastSigBits |= (hash[i] & 0xff);
            }

            return new UUID(mostSigBits, leastSigBits);
        } catch (NoSuchAlgorithmException e) {
            throw new BadEntryException("Unable to generate UUID");
        }
    }
}

