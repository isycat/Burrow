package com.isycat.burrow;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.annotations.Expose;
import com.isycat.burrow.serialization.HasMetadata;
import com.isycat.burrow.serialization.StrictSerializer;
import org.junit.Test;

import java.util.Map;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class SerializerTest {

    @Test
    public void hasMetadataRoundTripWithOverriddenField() {
        final MetadataTestClass obj = new MetadataTestClass();
        obj.field1 = "potato";
        obj.metadata = ImmutableMap.of(
                "potatoField", "metameta",
                "field1", "I'll never make it"
        );

        // field1's meta value shouldn't make it back through deserialization
        final MetadataTestClass expectedDeserialization = new MetadataTestClass();
        expectedDeserialization.field1 = obj.field1;
        expectedDeserialization.metadata = ImmutableMap.of(
                "potatoField", "metameta"
        );

        final String serialized = obj.toString();
        final MetadataTestClass deserialized = StrictSerializer.deserialize(serialized, MetadataTestClass.class);

        assertEquals(expectedDeserialization, deserialized);

        final String serialized2 = deserialized.toString();
        assertEquals(serialized, serialized2);
    }


    private static class MetadataTestClass implements HasMetadata {
        @Expose
        public String field1;
        public Map<String, Object> metadata;

        @Override
        public Map<String, Object> getMetadata() {
            return metadata;
        }

        @Override
        public void setMetadata(Map<String, Object> metadata) {
            this.metadata = metadata;
        }

        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof MetadataTestClass)) {
                return false;
            }
            final MetadataTestClass other = (MetadataTestClass) o;
            return Objects.equals(field1, other.field1) &&
                    Maps.difference(metadata, other.metadata).areEqual();
        }

        @Override
        public String toString() {
            return StrictSerializer.serialize(this);
        }
    }
}
