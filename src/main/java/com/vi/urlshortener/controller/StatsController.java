package com.vi.urlshortener.controller;

import com.vi.urlshortener.models.UrlMapping;
import com.vi.urlshortener.models.UrlStats;
import com.vi.urlshortener.repository.UrlMappingRepository;
import com.vi.urlshortener.services.interfaces.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class StatsController {

    @Value("${virtualidentity.baseurl}")
    private String baseUrl;

    private final UrlMappingRepository urlMappingRepository;

    @Autowired
    public StatsController(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }
    @GetMapping("/stats/{shortUrl}")
    public ResponseEntity getShortenedUrlStats(@PathVariable String shortUrl) {
        Optional<UrlMapping> urlMapping = urlMappingRepository.findByShortUrl(shortUrl);
        if(urlMapping.isPresent()){
            UrlMapping mapping = urlMapping.get();
            UrlStats urlStats = new UrlStats(mapping.getLongUrl(),baseUrl+mapping.getShortUrl(),mapping.getAccessCount(),mapping.getShortenedCount());
            return ResponseEntity.ok(urlStats);
        }else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
