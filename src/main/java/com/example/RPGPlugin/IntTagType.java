package com.example.RPGPlugin;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.nio.ByteBuffer;

@SuppressWarnings("NullableProblems")
public class IntTagType implements PersistentDataType<byte[], Integer> {

    @Override
    public Class<byte[]> getPrimitiveType() {
        return byte[].class;
    }

    @Override
    public Class<Integer> getComplexType() {
        return Integer.class;
    }

    @Override
    public byte[] toPrimitive(Integer integer, PersistentDataAdapterContext context) {
        ByteBuffer buffer = ByteBuffer.allocate(16);
        buffer.putInt(integer);
        buffer.putInt(integer);
        return buffer.array();
    }

    @Override
    public Integer fromPrimitive(byte[] bytes, PersistentDataAdapterContext context) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        return buffer.getInt();
    }
}
