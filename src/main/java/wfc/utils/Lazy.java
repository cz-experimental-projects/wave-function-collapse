package wfc.utils;

import java.util.function.Supplier;

public class Lazy<T> {
    private T value;
    private final Supplier<T> supplier;
    
    public Lazy(Supplier<T> supplier) {
        this.value = null;
        this.supplier = supplier;
    }

    public T getValue() {
        if (value == null) value = supplier.get(); 
        return value;
    }
}
