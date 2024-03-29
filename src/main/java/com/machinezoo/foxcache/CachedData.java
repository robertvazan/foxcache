// Part of Fox Cache: https://foxcache.machinezoo.com
package com.machinezoo.foxcache;

import java.util.*;
import java.util.concurrent.*;
import com.google.common.cache.*;
import com.machinezoo.hookless.*;
import com.machinezoo.stagean.*;

@CodeIssue("Downgrade softref to weakref after one second. Anything that needs longer caching should be persisted.")
class CachedData {
    static final ConcurrentMap<LazyCache<?>, Object> lazy = new ConcurrentHashMap<>();
    /*
     * Soft-valued cache may cause extremely inefficient GC behavior:
     * https://bugs.openjdk.java.net/browse/JDK-6912889
     * 
     * It is however very simple and it will use all RAM that is allocated to Java process,
     * which is usually some fraction of physical RAM.
     * This cache can be tuned indirectly with -Xmx and -XX:SoftRefLRUPolicyMSPerMB.
     * 
     * Cached value is wrapped in Optional, because Guava cache does not tolerate null values.
     */
    static final LoadingCache<ComputeCache<?>, Optional<?>> compute = CacheBuilder.newBuilder()
        .softValues()
        .build(CacheLoader.from(k -> Optional.ofNullable(k.compute())));
    /*
     * Like in ComputeCaches, just specialized for DerivativeCache.
     */
    static final LoadingCache<DerivativeCache<?>, ReactiveLazy<CacheDerivative<Object>>> derivative = CacheBuilder.newBuilder()
        .softValues()
        .build(CacheLoader.from(k -> materialize(k)));
    private static <T> ReactiveLazy<CacheDerivative<Object>> materialize(DerivativeCache<T> cache) {
        return new ReactiveLazy<>(() -> CacheDerivative.capture(() -> {
            /*
             * Touch the cache just in case there are extra dependencies there.
             */
            cache.touch();
            /*
             * Report inconsistencies between linker and compute() early.
             */
            CacheInput.get().freeze();
            return cache.compute();
        }));
    }
}
