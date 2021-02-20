package net.petafuel.styx.erebos.control;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.petafuel.styx.erebos.Erebos;
import net.petafuel.styx.erebos.boundary.Cacheable;
import net.petafuel.styx.erebos.entity.CacheableWrapper;
import net.petafuel.styx.erebos.entity.Properties;

public final class Watcher implements Runnable {
    private static final Logger LOG = LogManager.getLogger(Watcher.class);
    private int maxUnusedLifetime;

    public Watcher() {
        try {
            maxUnusedLifetime = Integer
                    .parseInt(System.getProperty(Properties.WATCHER_CACHABLE_MAX_UNUSED_LIFETIME, "180"));
        } catch (NumberFormatException numberFormatException) {
            LOG.warn("Configuration {} seems to be in an invalid format={}. Setting to 180 default.",
                    Properties.WATCHER_CACHABLE_MAX_UNUSED_LIFETIME, numberFormatException.getMessage());
            maxUnusedLifetime = 180;
        }
    }

    @Override
    public void run() {
        int sizeBefore = Erebos.getInstance().getCachables().size();
        LOG.debug("CachedObjectAmount={}", sizeBefore);

        Erebos.getInstance().getCachables().entrySet().removeIf(this::checkUnusedObjects);
        LOG.debug("Removed {} objects from cache, object unused lifetime expired",
                sizeBefore - Erebos.getInstance().getCachables().size());
    }

    private boolean checkUnusedObjects(Entry<Object, CacheableWrapper> cachedObject) {
        Calendar currentTime = Calendar.getInstance();
        currentTime.setTime(new Date());
        Calendar overdueTime = Calendar.getInstance();
        overdueTime.setTimeInMillis(cachedObject.getValue().getLastAccessed());
        overdueTime.add(Calendar.SECOND, maxUnusedLifetime);
        return currentTime.after(overdueTime);
    }

}
