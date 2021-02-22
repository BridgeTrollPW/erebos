package net.petafuel.styx.erebos;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import net.petafuel.styx.erebos.boundary.Cacheable;
import net.petafuel.styx.erebos.control.Watcher;
import net.petafuel.styx.erebos.entity.Properties;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WatcherTest {

    @Test
    public void testCheckUnusedTime() throws InterruptedException {
        System.setProperty(Properties.WATCHER_CACHABLE_MAX_UNUSED_LIFETIME, "1");
        System.setProperty(Properties.WATCHER_FREQUENCY, "1");
        Erebos.getInstance().restart();
        Erebos.getInstance().save(new Cacheable() {

            @Override
            public boolean equalsInCache(Object obj) {
                // TODO Auto-generated method stub
                return false;
            }

            @Override
            public boolean refresh() {
                // TODO Auto-generated method stub
                return false;
            }

        });
        Awaitility.given().pollThread(Thread::new).await().atMost(4, TimeUnit.SECONDS).until(cachedObjectSize(),
                Matchers.equalTo(0));
    }

    private Callable<Integer> cachedObjectSize() {
        return () -> Erebos.getInstance().getCachables().size(); // The suppling part of the condition
    }

    @Test
    public void testInvalidWatcherConfig() {
        System.setProperty(Properties.WATCHER_CACHABLE_MAX_UNUSED_LIFETIME, "WATCHER_CACHABLE_MAX_UNUSED_LIFETIME");
        Watcher watcher = new Watcher();
        Assertions.assertEquals(180, watcher.getMaxUnusedLifetime());
    }
}
