package com.codigo.code.test.service;

public interface CacheService {
    void set(String key, String value, int ttlInMin);
    String get(String key);
}
