package com.isycat.servlet;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.isycat.servlet.activity.ActivityRoute;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ActivityRouteTest {
    @Test
    public void testGroups() {
        final Map<String, String> expectedFields = ImmutableMap.of(
                "cameraId", "potatocam");
        final Map<String, String> fields = new ActivityRoute("/GetCamera/{cameraId}", null)
                .getPathFields("/GetCamera/potatocam");
        assertTrue("Fields differ in route matches",
                Maps.difference(fields, expectedFields).areEqual());
    }
}
