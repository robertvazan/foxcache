// Part of Fox Cache: https://foxcache.machinezoo.com
package com.machinezoo.foxcache;

import java.util.*;
import java.util.function.*;
import com.machinezoo.hookless.*;

public class CacheDerivative<T> {
    private final ReactiveValue<T> value;
    public ReactiveValue<T> value() { return value; }
    private final CacheInput input;
    public CacheInput input() { return input; }
    public CacheDerivative(ReactiveValue<T> value, CacheInput input) {
        this.value = value;
        this.input = input;
    }
    public static <T> CacheDerivative<T> capture(Supplier<T> supplier) {
        var input = new CacheInput();
        try (var recording = input.record()) {
            var value = ReactiveValue.capture(supplier);
            input.freeze();
            return new CacheDerivative<>(value, input);
        }
    }
    public T unpack() {
        input.unpack();
        return value.get();
    }
    @Override public boolean equals(Object obj) { return obj instanceof CacheDerivative<?> other
        && value.equals(other.value)
        && input.equals(other.input); }
    @Override public int hashCode() { return Objects.hash(value, input); }
    @Override public String toString() { return value.toString(); }
}
