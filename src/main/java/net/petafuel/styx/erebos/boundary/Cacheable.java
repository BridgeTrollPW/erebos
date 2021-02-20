package net.petafuel.styx.erebos.boundary;

public interface Cacheable {
    boolean equalsInCache(Object obj);
    boolean refresh();
}
