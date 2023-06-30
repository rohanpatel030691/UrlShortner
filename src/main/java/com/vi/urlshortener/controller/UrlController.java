package com.vi.urlshortener.controller;

import com.vi.urlshortener.services.interfaces.UrlShortenerService;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class UrlController {

    Logger logger = LoggerFactory.getLogger(UrlController.class);

    private final UrlShortenerService urlShortenerService;

    @Autowired
    public UrlController(UrlShortenerService urlShortenerService) {
        this.urlShortenerService = urlShortenerService;
    }

    @PostMapping("/shorten")
    public ResponseEntity shortenUrl(@RequestBody String longUrl) {
        // Validation checks to determine if the supplied URL is valid
        UrlValidator validator = new UrlValidator(
                new String[]{"http", "https"}
        );
        if (!validator.isValid(longUrl)) {
            String message = "Please provide a valid URL.";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
        }
        String shortUrl = urlShortenerService.shortenUrl(longUrl);
        return ResponseEntity.ok(shortUrl);
    }

    @GetMapping("/{shortUrl}")
    public ResponseEntity redirectToLongUrl(@PathVariable String shortUrl, HttpServletResponse response) throws IOException {

        String longUrl = urlShortenerService.getLongUrl(shortUrl);
        if (longUrl != null) {
            response.sendRedirect(longUrl);
            return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).build();
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Short URL not found");
        }
    }

}

