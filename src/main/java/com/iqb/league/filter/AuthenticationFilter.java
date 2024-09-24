package com.iqb.league.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class AuthenticationFilter extends OncePerRequestFilter {
    private static final long VALIDITY_DURATION = 30 * 60; // 30 dakika

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String key = request.getRequestURI().split("/")[1]; // URL'den anahtarı al

        if (isKeyValid(key)) {
            filterChain.doFilter(request, response); // Geçerliyse isleme devam et
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write("Authentication Error"); // Hata mesajı
        }
    }

    private boolean isKeyValid(String key) {
        long timestamp;
        try {
            timestamp = extractTimestamp(key); // Zaman damgasını çıkar
        } catch (NumberFormatException e) {
            return false; // Geçersiz zaman damgası durumunda false döndür
        }

        long currentTime = System.currentTimeMillis() / 1000; // Mevcut zaman (saniye cinsinden)


        // Geçerlilik kontrolü

        return (0L < (currentTime - timestamp) && (currentTime - timestamp) <= VALIDITY_DURATION); // Farkı hesapla
    }

    private long extractTimestamp(String key) {
        StringBuilder timestampBuilder = new StringBuilder();

        // Anahtarın 2., 4., 6., 8., ... 20. basamaklarını al
        for (int i = 1; i < key.length() && i < 20; i += 2) { // 0 tabanlı indekste 1, 3, 5, ... kullan
            timestampBuilder.append(key.charAt(i));
        }

        // Zaman damgasını long olarak döndür

        return Long.parseLong(timestampBuilder.toString());
    }
}
