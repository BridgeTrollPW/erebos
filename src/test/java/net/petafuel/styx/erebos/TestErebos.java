package net.petafuel.styx.erebos;

import java.util.UUID;

import org.junit.jupiter.api.Test;

import net.petafuel.styx.erebos.boundary.Cacheable;

public class TestErebos {
    @Test
    public void test() {
        Thread t = new Thread(() -> {
            Erebos.getInstance();
        });
        t.start();

        boolean running = true;
        int i = 0;
        while (running) {

            try {
                if (i >= 100) {
                    running = false;
                }
                Erebos.getInstance().save(UUID.randomUUID(), new Cacheable(){

                    public int data = 1;
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
                Thread.sleep(500);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
