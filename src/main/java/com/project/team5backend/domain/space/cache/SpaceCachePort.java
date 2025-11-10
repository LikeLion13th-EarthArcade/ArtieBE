package com.project.team5backend.domain.space.cache;

public interface SpaceCachePort {
    boolean isValidated(String bizNumber);
    void invalidate(String bizNumber);
}
