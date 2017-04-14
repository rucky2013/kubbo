package com.sogou.map.kubbo.remote.serialization.hessian;

import com.caucho.hessian.io.SerializerFactory;

public class HessianSerializerFactory extends SerializerFactory {

	public static final SerializerFactory SERIALIZER_FACTORY = new HessianSerializerFactory();

	private HessianSerializerFactory() {
	}

	@Override
	public ClassLoader getClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}

}
