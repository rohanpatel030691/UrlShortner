package com.vi.urlshortener.services;

import com.vi.urlshortener.common.CommonUtil;
import com.vi.urlshortener.models.UrlMapping;
import com.vi.urlshortener.repository.UrlMappingRepository;
import com.vi.urlshortener.services.implementation.UrlShortenerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;


public class UrlShortenerServiceImplTest {

    private UrlMappingRepository urlMappingRepository=mock(UrlMappingRepository.class);

    private UrlShortenerServiceImpl urlShortenerService = new UrlShortenerServiceImpl(urlMappingRepository);

    private final String baseUrl = "https://mock-base.com/";
    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(urlShortenerService, "baseUrl", baseUrl);
    }

    @Test
    void shortenUrl_WithNewLongUrl_ReturnsShortenedUrl() {
        String longUrl = "https://example.com/original";
        String shortUrl = "abcd";
        UrlMapping newMapping = new UrlMapping();
        newMapping.setLongUrl(longUrl);
        newMapping.setShortUrl(shortUrl);

        try (MockedStatic<CommonUtil> encodedValue = Mockito.mockStatic(CommonUtil.class)) {
            encodedValue.when(CommonUtil::generateShortUrl).thenReturn(shortUrl);

            when(urlMappingRepository.findByLongUrl(longUrl)).thenReturn(Optional.empty());
            when(urlMappingRepository.save(any(UrlMapping.class))).thenReturn(newMapping);

            String shortenedUrl = urlShortenerService.shortenUrl(longUrl);

            assertEquals(baseUrl + "abcd", shortenedUrl);
            verify(urlMappingRepository, times(1)).findByLongUrl(longUrl);
            verify(urlMappingRepository, times(1)).save(any(UrlMapping.class));
        }
    }

    @Test
    void shortenUrl_WithExistingLongUrl_ReturnsExistingShortenedUrl() {
        String longUrl = "https://example.com/original";
        String existingShortUrl = "existing";

        // Mock the scenario where the long URL already exists in the repository
        UrlMapping existingMapping = new UrlMapping();
        existingMapping.setLongUrl(longUrl);
        existingMapping.setShortUrl(existingShortUrl);
        when(urlMappingRepository.findByLongUrl(longUrl)).thenReturn(Optional.of(existingMapping));

        // Call the method
        String shortenedUrl = urlShortenerService.shortenUrl(longUrl);

        // Assert the result
        assertEquals(baseUrl + existingShortUrl, shortenedUrl);
        verify(urlMappingRepository, times(1)).findByLongUrl(longUrl);
    }

    @Test
    void getLongUrl_WithValidShortUrl_ReturnsLongUrl() {
        String shortUrl = "abcd";
        String longUrl = "https://example.com/original";

        UrlMapping urlMapping = new UrlMapping();
        urlMapping.setShortUrl(shortUrl);
        urlMapping.setLongUrl(longUrl);
        urlMapping.setAccessCount(0);

        when(urlMappingRepository.findByShortUrl(shortUrl)).thenReturn(Optional.of(urlMapping));

        String result = urlShortenerService.getLongUrl(shortUrl);

        assertEquals(longUrl, result);
        assertEquals(1, urlMapping.getAccessCount());
        verify(urlMappingRepository, times(1)).findByShortUrl(shortUrl);
        verify(urlMappingRepository, times(1)).save(urlMapping);
    }

    @Test
    void getLongUrl_WithInvalidShortUrl_ReturnsNull() {
        String shortUrl = "invalid";

        when(urlMappingRepository.findByShortUrl(shortUrl)).thenReturn(Optional.empty());

        String result = urlShortenerService.getLongUrl(shortUrl);

        assertNull(result);
        verify(urlMappingRepository, times(1)).findByShortUrl(shortUrl);
        verify(urlMappingRepository, never()).save(any(UrlMapping.class));
    }
}
