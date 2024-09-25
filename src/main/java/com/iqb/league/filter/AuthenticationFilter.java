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
    private static final long VALIDITY_DURATION = 30 * 60; // 30 dakika

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String key = request.getRequestURI().split("/")[1]; // URL'den anahtarı al

        if (isKeyValid(key)) {
            filterChain.doFilter(request, response); // Geçerliyse işleme devam et
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            response.getWriter().write("Authentication Error"); // Hata mesajı
        }
    }

    // Zaman ve hash validasyonunu yapan metod
    private boolean isKeyValid(String key) {
        long timestamp;
        try {
            timestamp = extractTimestamp(key); // Zaman damgasını çıkar
        } catch (NumberFormatException e) {
            return false; // Geçersiz zaman damgası durumunda false döndür
        }

        long currentTime = System.currentTimeMillis() / 1000; // Mevcut zaman (saniye cinsinden)


        // Geçerlilik süresi kontrolü
        if (!(0L < (currentTime - timestamp) && (currentTime - timestamp) <= VALIDITY_DURATION)) {
            return false; // Zaman damgası geçerli değilse
        }

        // Kalan hash karakterlerini çıkart
        String extractedHash = extractHash(key);


        // Zaman damgasının sha256 hashini hesapla
        String computedHash = computeSHA256(String.valueOf(timestamp));


        // Hash değerlerini karşılaştır
        return computedHash.equals(extractedHash);
    }

    // Anahtardan hash kısmını ayıkla (1., 3., 5., ... 19., ... karakterleri al)
    private String extractHash(String key) {
        StringBuilder hashBuilder = new StringBuilder();

        // 1., 3., 5., ... 19. karakterleri al
        for (int i = 0; i < key.length() && i < 20; i += 2) {
            hashBuilder.append(key.charAt(i));
        }

        // 20. indeksten itibaren kalan tüm karakterleri de al
        if (key.length() > 20) {
            hashBuilder.append(key.substring(20));
        }


        return hashBuilder.toString();
    }

    // Anahtardan timestamp kısmını ayıkla (2., 4., 6., ... karakterleri al)
    private long extractTimestamp(String key) {
        StringBuilder timestampBuilder = new StringBuilder();

        // Anahtarın 2., 4., 6., ... karakterlerini al
        for (int i = 1; i < key.length() && i < 20; i += 2) {
            timestampBuilder.append(key.charAt(i));
        }


        // Zaman damgasını long olarak döndür
        return Long.parseLong(timestampBuilder.toString());
    }

    // Zaman damgasının sha256 hashini hesapla ve hexadecimal formatında döndür
    private String computeSHA256(String data) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash); // Hash sonucunu Hexadecimal'e çeviriyoruz
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algoritması bulunamadı!", e);
        }
    }

    // Byte dizisini hexadecimal string'e çevir
    private String bytesToHex(byte[] hashBytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0'); // Tek haneli hex için sıfır ekle
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
