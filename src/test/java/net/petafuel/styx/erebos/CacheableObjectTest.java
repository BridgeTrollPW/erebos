package net.petafuel.styx.erebos;

import java.util.concurrent.TimeUnit;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import net.petafuel.styx.erebos.boundary.Cacheable;
import net.petafuel.styx.erebos.entity.CacheableWrapper;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CacheableObjectTest {

    class TestObject implements Cacheable {
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
    }

    @Test
    public void testInvalidCachableObject() {
        Assertions.assertThrows(NullPointerException.class, () -> {
            new CacheableWrapper(null);
        });
    }

    @Test
    public void testWriteToCache() throws NumberFormatException, InterruptedException {
        Erebos.getInstance().restart();
        Erebos.getInstance().save("test", new TestObject());
        Awaitility.given().pollThread(Thread::new).await().atMost(10, TimeUnit.SECONDS)
                .until(() -> {
                    CacheableWrapper wrapped = Erebos.getInstance().getRaw("test");
                    Erebos.getInstance().<TestObject>get("test");
                    return wrapped.getLastAccessed() > wrapped.getFirstWrite();
                });
    }
}
