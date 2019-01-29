package com.isycat.burrow.serialization;

import java.util.Map;

public interface HasMetadata {
    Map<String, Object> getMetadata();
    void setMetadata(final Map<String, Object> metadata);
}