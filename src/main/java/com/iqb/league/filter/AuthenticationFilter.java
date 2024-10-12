package com.iqb.league.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthenticationFilter extends OncePerRequestFilter {
    private static final long VALIDITY_DURATION = 30 * 60; // 30 mins

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String key = request.getRequestURI().split("/")[1]; // take the first part of the URI as key

        if (isKeyValid(key)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write("Authentication Error");
        }
    }

    // Zaman ve hash validasyonunu yapan metod
    private boolean isKeyValid(String key) {
        long timestamp;
        try {
            timestamp = extractTimestamp(key);
        } catch (NumberFormatException e) {
            return false;
        }

        long currentTime = System.currentTimeMillis() / 1000; //current time in seconds


        // control the validity of the timestamp
        if (!(0L < (currentTime - timestamp) && (currentTime - timestamp) <= VALIDITY_DURATION)) {
            return false;
        }

        String extractedHash = extractHash(key);


        String computedHash = computeSHA256(String.valueOf(timestamp));


        // compare the computed hash with the extracted hash
        return computedHash.equals(extractedHash);
    }

    // extract hash from the key (1., 3., 5., ... 19., ... characters)
    private String extractHash(String key) {
        StringBuilder hashBuilder = new StringBuilder();

        // take 1., 3., 5., ... 19., ... characters
        for (int i = 0; i < key.length() && i < 20; i += 2) {
            hashBuilder.append(key.charAt(i));
        }

        // take the rest of the key if it is longer than 20 characters
        if (key.length() > 20) {
            hashBuilder.append(key.substring(20));
        }


        return hashBuilder.toString();
    }

    // Extract timestamp from the key (2., 4., 6., ... characters)
    private long extractTimestamp(String key) {
        StringBuilder timestampBuilder = new StringBuilder();

        // Take 2., 4., 6., ... characters
        for (int i = 1; i < key.length() && i < 20; i += 2) {
            timestampBuilder.append(key.charAt(i));
        }



        return Long.parseLong(timestampBuilder.toString());
    }

    // Method to compute SHA-256 hash of a string
    private String computeSHA256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algoritması bulunamadı!", e);
        }
    }

    //Method to convert byte array to hex string
    private String bytesToHex(byte[] hashBytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0'); // pad with zero if it is one character
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
