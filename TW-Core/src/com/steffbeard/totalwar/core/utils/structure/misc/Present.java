package com.steffbeard.totalwar.core.utils.structure.misc;

public interface Present {
    public default boolean has() {
        return isPresent();
    }
    
    public boolean isPresent();
    
    public boolean isEmpty();
}
