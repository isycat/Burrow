package com.isycat.burrow.error;

import com.isycat.burrow.serialization.StrictSerializer;

public class Structure {
    public String toString() {
        return StrictSerializer.serialize(getThis());
    }

    private <ExtendedType> ExtendedType getThis() {
        return (ExtendedType) this;
    }
}
