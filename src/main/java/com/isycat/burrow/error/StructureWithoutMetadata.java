package com.isycat.burrow.error;

import com.isycat.burrow.serialization.StrictSerializer;

public class StructureWithoutMetadata {
    public String toString() {
        return StrictSerializer.serialize(getThis());
    }

    protected <ExtendedType extends StructureWithoutMetadata> ExtendedType getThis() {
        return (ExtendedType) this;
    }
}
