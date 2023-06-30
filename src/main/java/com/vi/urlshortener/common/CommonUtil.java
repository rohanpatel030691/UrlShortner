package com.vi.urlshortener.common;

import java.security.SecureRandom;

public class CommonUtil {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int CODE_LENGTH = 7;

    public static String generateShortUrl() {
        StringBuilder shortUrl = new StringBuilder();

        SecureRandom random = new SecureRandom();
        for (int i = 0; i < CODE_LENGTH; i++) {
            int randomIndex = random.nextInt(BASE62_CHARS.length());
            shortUrl.append(BASE62_CHARS.charAt(randomIndex));
        }
        return shortUrl.toString();
    }
}
