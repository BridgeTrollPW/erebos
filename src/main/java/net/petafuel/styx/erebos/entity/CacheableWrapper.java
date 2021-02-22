package net.petafuel.styx.erebos.entity;

import java.util.Date;
import java.util.Objects;
import java.util.Calendar;

import net.petafuel.styx.erebos.boundary.Cacheable;

public final class CacheableWrapper {
    // First time this object was written into the cache
    private long firstWrite;
    // Last time the Cacheable Object was accessed
    private long lastAccessed;
    // The object that is cached
    private Cacheable cachedObject;

    public CacheableWrapper(Cacheable cacheable) {
        Objects.requireNonNull(cacheable);
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        firstWrite = now.getTimeInMillis();
        lastAccessed = now.getTimeInMillis();
        cachedObject = cacheable;
    }

    /**
     * @return the firstWrite
     */
    public long getFirstWrite() {
        return firstWrite;
    }

    /**
     * @return the lastAccessed
     */
    public long getLastAccessed() {
        return lastAccessed;
    }

    /**
     * @return the cachedObject
     */
    public Cacheable getCachedObject() {
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());
        lastAccessed = now.getTimeInMillis();
        return cachedObject;
    }
}
