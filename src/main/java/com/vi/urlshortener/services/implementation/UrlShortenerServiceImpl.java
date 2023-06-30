package com.vi.urlshortener.services.implementation;

import com.vi.urlshortener.common.CommonUtil;
import com.vi.urlshortener.models.UrlMapping;
import com.vi.urlshortener.repository.UrlMappingRepository;
import com.vi.urlshortener.services.interfaces.UrlShortenerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UrlShortenerServiceImpl implements UrlShortenerService {

    private final UrlMappingRepository urlMappingRepository;

    @Value("${virtualidentity.baseurl}")
    private String baseUrl;

    @Autowired
    public UrlShortenerServiceImpl(UrlMappingRepository urlMappingRepository) {
        this.urlMappingRepository = urlMappingRepository;
    }

    @Override
    public String shortenUrl(String longUrl) {
        Optional<UrlMapping> existingMapping = urlMappingRepository.findByLongUrl(longUrl);
        if (existingMapping.isPresent()) {
            UrlMapping mapping = existingMapping.get();
            mapping.setShortenedCount(mapping.getShortenedCount() + 1); // Increment the shortened count
            urlMappingRepository.save(mapping);
            return baseUrl+existingMapping.get().getShortUrl();
        }

        UrlMapping newMapping = new UrlMapping();
        newMapping.setLongUrl(longUrl);

        String shortUrl = CommonUtil.generateShortUrl();
        newMapping.setShortUrl(shortUrl);
        urlMappingRepository.save(newMapping);

        return baseUrl+shortUrl;
    }

    @Override
    public String getLongUrl(String shortUrl) {
        Optional<UrlMapping> mapping = urlMappingRepository.findByShortUrl(shortUrl);
        if(mapping.isPresent()){
            UrlMapping urlMapping = mapping.get();
            urlMapping.setAccessCount(urlMapping.getAccessCount() + 1); // Increment the access count
            urlMappingRepository.save(urlMapping);
            return urlMapping.getLongUrl();
        }
        return null;
    }
}
