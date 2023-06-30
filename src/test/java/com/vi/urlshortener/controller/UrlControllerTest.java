package com.vi.urlshortener.controller;

import com.vi.urlshortener.services.interfaces.UrlShortenerService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UrlControllerTest {

    private final UrlShortenerService urlShortenerService = mock(UrlShortenerService.class);
    private final UrlController urlController = new UrlController(urlShortenerService);

    @Test
    void shortenUrl_WithValidUrl_ReturnsShortenedUrl() {
        String longUrl = "https://example.com";
        String shortUrl = "abc123";

        when(urlShortenerService.shortenUrl(longUrl)).thenReturn(shortUrl);

        ResponseEntity<?> response = urlController.shortenUrl(longUrl);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(shortUrl, response.getBody());

        verify(urlShortenerService, times(1)).shortenUrl(longUrl);
    }

    @Test
    void shortenUrl_WithInvalidUrl_ReturnsBadRequest() {
        String longUrl = "invalid-url";

        ResponseEntity<?> response = urlController.shortenUrl(longUrl);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Please provide a valid URL.", response.getBody());

        verify(urlShortenerService, never()).shortenUrl(anyString());
    }

    @Test
    void redirectToLongUrl_WithExistingShortUrl_RedirectsToLongUrl() throws IOException {
        String shortUrl = "abc123";
        String longUrl = "https://example.com";

        when(urlShortenerService.getLongUrl(shortUrl)).thenReturn(longUrl);

        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        ResponseEntity<Void> response = urlController.redirectToLongUrl(shortUrl, mockResponse);

        assertEquals(HttpStatus.MOVED_PERMANENTLY, response.getStatusCode());
        assertEquals(longUrl, mockResponse.getHeader("Location"));

        verify(urlShortenerService, times(1)).getLongUrl(shortUrl);
    }

    @Test
    void redirectToLongUrl_WithNonExistingShortUrl_ReturnsNotFound() throws IOException {
        String shortUrl = "invalidshorturl";

        when(urlShortenerService.getLongUrl(shortUrl)).thenReturn(null);

        MockHttpServletResponse mockResponse = new MockHttpServletResponse();
        ResponseEntity<Void> response = urlController.redirectToLongUrl(shortUrl, mockResponse);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Short URL not found", response.getBody());

        verify(urlShortenerService, times(1)).getLongUrl(shortUrl);
    }
}
