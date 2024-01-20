// Part of Fox Cache: https://foxcache.machinezoo.com
package com.machinezoo.foxcache;

import org.apache.commons.lang3.exception.*;
import com.machinezoo.noexception.*;

public class EmptyCacheException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public static boolean caused(Throwable exception) { return ExceptionUtils.stream(exception).anyMatch(x -> x instanceof EmptyCacheException); }
    private static class EmptyCacheExceptionSilencing extends ExceptionHandler {
        @Override public boolean handle(Throwable exception) { return caused(exception); }
    }
    public static ExceptionHandler silence() { return new EmptyCacheExceptionSilencing(); }
}
