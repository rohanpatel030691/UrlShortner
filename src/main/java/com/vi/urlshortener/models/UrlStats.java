package com.vi.urlshortener.models;

public class UrlStats {
    private String longUrl;
    private String shortUrl;
    private int accessCount;
    private int shortenedCount;

    public UrlStats(String longUrl, String shortUrl, int accessCount, int shortenedCount) {
        this.longUrl = longUrl;
        this.shortUrl = shortUrl;
        this.accessCount = accessCount;
        this.shortenedCount = shortenedCount;
    }

    public String getLongUrl() {
        return longUrl;
    }

    public void setLongUrl(String longUrl) {
        this.longUrl = longUrl;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    public int getAccessCount() {
        return accessCount;
    }

    public void setAccessCount(int accessCount) {
        this.accessCount = accessCount;
    }

    public int getShortenedCount() {
        return shortenedCount;
    }

    public void setShortenedCount(int shortenedCount) {
        this.shortenedCount = shortenedCount;
    }
}
