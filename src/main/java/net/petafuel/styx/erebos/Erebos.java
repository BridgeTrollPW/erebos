package net.petafuel.styx.erebos;

import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.petafuel.styx.erebos.boundary.Cacheable;
import net.petafuel.styx.erebos.control.Watcher;
import net.petafuel.styx.erebos.entity.CacheableWrapper;
import net.petafuel.styx.erebos.entity.Properties;

public class Erebos {
    private static final Logger LOG = LogManager.getLogger(Erebos.class);
    private ConcurrentSkipListMap<Object, CacheableWrapper> cachables;
    ScheduledExecutorService scheduledThreadPoolExecutor;

    private Erebos() {
        LOG.info("Starting Erebos WATCHER_FREQUENCY={}, WATCHER_CACHABLE_MAX_UNUSED_LIFETIME={}",
                System.getProperty(Properties.WATCHER_FREQUENCY, "5"),
                System.getProperty(Properties.WATCHER_CACHABLE_MAX_UNUSED_LIFETIME, "180"));
        cachables = new ConcurrentSkipListMap<>();
        scheduledThreadPoolExecutor = Executors.newSingleThreadScheduledExecutor();
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Watcher(), 0, Integer
                .parseInt(System.getProperty(Properties.WATCHER_FREQUENCY, "5")),
                TimeUnit.SECONDS);
    }

    private static final class HOLDER {
        static final Erebos INSTANCE = new Erebos();
    }

    public static Erebos getInstance() {
        return HOLDER.INSTANCE;
    }

    /**
     * @return the cachables
     */
    public Map<Object, CacheableWrapper> getCachables() {
        return cachables;
    }

    public void save(Cacheable cacheable) {
        save(cacheable.getClass().getName(), cacheable);
    }

    public void save(Object identifier, Cacheable cacheable) {
        cachables.put(identifier, new CacheableWrapper(cacheable));
    }

    @SuppressWarnings({ "unchecked" })
    public <T> T get(Object identifier) {
        return (T) getRaw(identifier).getCachedObject();
    }

    public CacheableWrapper getRaw(Object identifier) {
        return cachables.get(identifier);
    }

    public void restart() throws NumberFormatException, InterruptedException {
        LOG.debug("restarting");
        scheduledThreadPoolExecutor.awaitTermination(
                Long.parseLong(System.getProperty(Properties.WATCHER_FREQUENCY, "5")), TimeUnit.SECONDS);
        cachables.clear();
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(new Watcher(), 0, Integer
                .parseInt(System.getProperty(Properties.WATCHER_FREQUENCY, "5")),
                TimeUnit.SECONDS);
        LOG.debug("restarted");
    }
}
