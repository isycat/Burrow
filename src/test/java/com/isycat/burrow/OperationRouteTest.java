package com.isycat.burrow;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.isycat.burrow.operation.OperationRoute;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class OperationRouteTest {
    @Test
    public void testGroups() {
        final Map<String, String> expectedFields = ImmutableMap.of(
                "cameraId", "potatocam");
        final Map<String, String> fields = new OperationRoute("/GetCamera/{cameraId}", null)
                .getPathFields("/GetCamera/potatocam");
        assertTrue("Fields differ in route matches",
                Maps.difference(fields, expectedFields).areEqual());
    }
}
