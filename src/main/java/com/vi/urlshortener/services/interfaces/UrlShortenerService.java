package com.vi.urlshortener.services.interfaces;

public interface UrlShortenerService {
    String shortenUrl(String longUrl);
    String getLongUrl(String shortUrl);
}
