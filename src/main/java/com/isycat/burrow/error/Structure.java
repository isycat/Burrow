package com.isycat.burrow.error;

import com.isycat.burrow.serialization.HasMetadata;

import java.util.HashMap;
import java.util.Map;

public class Structure extends StructureWithoutMetadata implements HasMetadata {
    private Map<String, Object> metadata = new HashMap<>();

    @Override
    public Map<String, Object> getMetadata() {
        return this.metadata;
    }

    @Override
    public void setMetadata(final Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    /**
     * Associates the specified value with the specified key in this object.
     * If the map previously contained a mapping for the key, the old
     * value is replaced.
     *
     * @param key key with which the specified value is to be associated
     * @param value value to be associated with the specified key
     * @return this
     */
    public <ExtendedType extends Structure> ExtendedType with(final String key, final Object value) {
        metadata.put(key, value);
        return getThis();
    }
}
