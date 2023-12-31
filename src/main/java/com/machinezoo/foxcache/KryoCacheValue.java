// Part of Fox Cache: https://foxcache.machinezoo.com
package com.machinezoo.foxcache;

import java.nio.file.*;
import com.esotericsoftware.kryo.io.*;
import com.machinezoo.noexception.*;

record KryoCacheValue(Path path) implements ComputeCache<Object> {
	@Override
	public Object compute() {
		return Exceptions.sneak().get(() -> {
			try (	var stream = Files.newInputStream(path);
					var input = new Input(stream)) {
				return ThreadLocalKryo.get().readClassAndObject(input);
			}
		});
	}
}
