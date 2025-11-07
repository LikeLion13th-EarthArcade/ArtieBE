package com.project.team5backend.domain.common.cache;

public interface CachePort {
    boolean isValidated(String bizNumber);
    void invalidate(String bizNumber);
}
