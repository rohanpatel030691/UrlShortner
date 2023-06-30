package com.vi.urlshortener.controller;

import com.vi.urlshortener.models.UrlMapping;
import com.vi.urlshortener.models.UrlStats;
import com.vi.urlshortener.repository.UrlMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class StatsControllerTest {

    private final UrlMappingRepository urlMappingRepository = mock(UrlMappingRepository.class);
    private final StatsController statsController = new StatsController(urlMappingRepository);
    private final String baseUrl = "https://mock-base.com/";
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(statsController, "baseUrl", baseUrl);
    }

    @Test
    void getShortenedUrlStats_WithExistingShortUrl_ReturnsStats() {
        String shortUrl = "abc123";
        String longUrl = "https://example.com/original";

        int accessCount = 10;
        int shortenedCount = 5;

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setLongUrl(longUrl);
        urlMapping.setAccessCount(accessCount);
        urlMapping.setShortenedCount(shortenedCount);

        when(urlMappingRepository.findByShortUrl(shortUrl)).thenReturn(Optional.of(urlMapping));

        ResponseEntity<?> response = statsController.getShortenedUrlStats(shortUrl);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());

        UrlStats urlStats = (UrlStats) response.getBody();
        assertEquals(longUrl, urlStats.getLongUrl());
        assertEquals( baseUrl+shortUrl, urlStats.getShortUrl());
        assertEquals(accessCount, urlStats.getAccessCount());
        assertEquals(shortenedCount, urlStats.getShortenedCount());

        verify(urlMappingRepository, times(1)).findByShortUrl(shortUrl);
    }

    @Test
    void getShortenedUrlStats_WithNonExistingShortUrl_ReturnsNotFound() {
        String shortUrl = "nonexistent";

        when(urlMappingRepository.findByShortUrl(shortUrl)).thenReturn(Optional.empty());

        ResponseEntity<?> response = statsController.getShortenedUrlStats(shortUrl);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(urlMappingRepository, times(1)).findByShortUrl(shortUrl);
    }
}
